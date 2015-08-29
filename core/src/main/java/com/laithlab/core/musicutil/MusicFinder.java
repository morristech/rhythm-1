package com.laithlab.core.musicutil;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import com.laithlab.core.db.Album;
import com.laithlab.core.db.Artist;
import com.laithlab.core.db.Song;
import io.realm.Realm;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class MusicFinder {

	private static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private static String mp3Pattern = ".mp3";

	// Constructor
	public MusicFinder() {
	}

	public static ArrayList<HashMap<String, String>> getMusicFromStorages() {
		for(String storage : getStorageDirectories()){
			getSongList(storage);
		}
		return songsList;
	}

	public static void getSongList(String path) {

		if (path != null) {
			File home = new File(path);
			File[] listFiles = home.listFiles();
			if (listFiles != null && listFiles.length > 0) {
				for (File file : listFiles) {
					if (file.isDirectory()) {
						scanDirectory(file);
					} else {
						addSongToList(file);
					}
				}
			}
		}
		// return songs list array
	}

	private static void scanDirectory(File directory) {
		if (directory != null) {
			File[] listFiles = directory.listFiles();
			if (listFiles != null && listFiles.length > 0) {
				for (File file : listFiles) {
					if (file.isDirectory()) {
						scanDirectory(file);
					} else {
						addSongToList(file);
					}

				}
			}
		}
	}

	private static void addSongToList(File song) {
		if (song.getName().endsWith(mp3Pattern)) {
			HashMap<String, String> songMap = new HashMap<String, String>();
			songMap.put("songTitle",
					song.getName().substring(0, (song.getName().length() - 4)));
			songMap.put("songPath", song.getPath());

			// Adding each song to SongList
			songsList.add(songMap);
		}
	}

	public static void updateMusicDB(Context context) {
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		for (final HashMap<String, String> song : getMusicFromStorages()) {
			mmr.setDataSource(song.get("songPath"));
			createSongRecord(mmr, context, song.get("songPath"));
		}
	}

	private static void createSongRecord(MediaMetadataRetriever mmr, Context context, String songPath) {
		final Realm realm = Realm.getInstance(context);

		final String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		final String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		final String trackTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		final String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

		realm.beginTransaction();
		Artist artistRecord = getOrCreateArtist(realm, artist);
		Album albumRecord = getOrCreateAlbum(realm, artistRecord, album);
		getOrCreateSong(realm, albumRecord, trackTitle, duration, songPath);
		realm.commitTransaction();

	}

	private static Artist getOrCreateArtist(Realm realm, String artist) {
		if (artist == null) {
			Artist query = realm.where(Artist.class)
					.contains("artistName", "Untitled Artist")
					.findFirst();
			if (query != null) {
				return query;
			} else {
				Artist newArtist = realm.createObject(Artist.class);
				newArtist.setArtistName("Untitled Artist");
				return newArtist;
			}
		} else {
			Artist query = realm.where(Artist.class)
					.contains("artistName", artist)
					.findFirst();
			if (query != null) {
				return query;
			} else {
				Artist newArtist = realm.createObject(Artist.class);
				newArtist.setArtistName(artist);
				return newArtist;
			}
		}
	}

	private static Album getOrCreateAlbum(Realm realm, Artist artistRecord, String albumTitle) {
		Album albumRecord = null;
		if (albumTitle == null) {
			for (Album albumItem : artistRecord.getAlbums()) {
				if (albumItem.getAlbumTitle().equals("Untitled Album")) {
					albumRecord = albumItem;
				}
			}
			if (albumRecord != null) {
				return albumRecord;
			} else {
				albumRecord = realm.createObject(Album.class);
				albumRecord.setAlbumTitle("Untitled Album");
				artistRecord.getAlbums().add(albumRecord);
				return albumRecord;
			}
		} else {
			for (Album albumItem : artistRecord.getAlbums()) {
				if (albumItem.getAlbumTitle().equals(albumTitle)) {
					albumRecord = albumItem;
				}
			}
			if (albumRecord != null) {
				return albumRecord;
			} else {
				albumRecord = realm.createObject(Album.class);
				albumRecord.setAlbumTitle(albumTitle);
				artistRecord.getAlbums().add(albumRecord);
				return albumRecord;
			}
		}
	}

	private static void getOrCreateSong(Realm realm, Album albumRecord, String songTitle, String duration, String songPath) {
		Song songRecord = null;
		for (Song songItem : albumRecord.getSongs()) {
			if (songItem.getSongLocation().equals(songPath)) {
				songRecord = songItem;
			}
		}
		if (songRecord == null) {
			songRecord = realm.createObject(Song.class);
			songRecord.setSongTitle(songTitle != null ? songTitle : "Untitle Song");
			songRecord.setSongLocation(songPath);
			if (duration != null) {
				songRecord.setSongDuration(Integer.parseInt(duration));
			}
			albumRecord.getSongs().add(songRecord);
		}

	}


	public static List<Artist> allArtists(Context context) {
		Realm realm = Realm.getInstance(context);
		return realm.allObjects(Artist.class);
	}

	private static final Pattern DIR_SEPORATOR = Pattern.compile("/");


	public static String[] getStorageDirectories()
	{
		// Final set of paths
		final Set<String> rv = new HashSet<String>();
		// Primary physical SD-CARD (not emulated)
		final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
		// All Secondary SD-CARDs (all exclude primary) separated by ":"
		final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
		// Primary emulated SD-CARD
		final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
		if(TextUtils.isEmpty(rawEmulatedStorageTarget))
		{
			// Device has physical external storage; use plain paths.
			if(TextUtils.isEmpty(rawExternalStorage))
			{
				// EXTERNAL_STORAGE undefined; falling back to default.
				rv.add("/storage/sdcard0");
			}
			else
			{
				rv.add(rawExternalStorage);
			}
		}
		else
		{
			// Device has emulated storage; external storage paths should have
			// userId burned into them.
			final String rawUserId;
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
			{
				rawUserId = "";
			}
			else
			{
				final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
				final String[] folders = DIR_SEPORATOR.split(path);
				final String lastFolder = folders[folders.length - 1];
				boolean isDigit = false;
				try
				{
					Integer.valueOf(lastFolder);
					isDigit = true;
				}
				catch(NumberFormatException ignored)
				{
				}
				rawUserId = isDigit ? lastFolder : "";
			}
			// /storage/emulated/0[1,2,...]
			if(TextUtils.isEmpty(rawUserId))
			{
				rv.add(rawEmulatedStorageTarget);
			}
			else
			{
				rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
			}
		}
		// Add all secondary storages
		if(!TextUtils.isEmpty(rawSecondaryStoragesStr))
		{
			// All Secondary SD-CARDs splited into array
			final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
			Collections.addAll(rv, rawSecondaryStorages);
		}
		return rv.toArray(new String[rv.size()]);
	}
}