package com.laithlab.core.adapter;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.laithlab.core.R;
import com.laithlab.core.dto.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final List<SearchResult> originalSearchResults;
    private List<SearchResult> currentSearchResults;
    private MusicFilter musicFilter;

    private static final int TYPE_SONG = 0;
    private static final int TYPE_ALBUM = 1;
    private static final int TYPE_ARTIST = 2;

    public SearchAdapter(List<SearchResult> searchResults) {
        this.originalSearchResults = searchResults;
        this.currentSearchResults = searchResults;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if (currentSearchResults.get(position).getResultType() == SearchResult.ResultType.ARTIST) {
            viewType = TYPE_ARTIST;
        } else if (currentSearchResults.get(position).getResultType() == SearchResult.ResultType.ALBUM) {
            viewType = TYPE_ALBUM;
        } else if (currentSearchResults.get(position).getResultType() == SearchResult.ResultType.SONG) {
            viewType = TYPE_SONG;
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_SONG:
                ViewGroup vSong = (ViewGroup) mInflater.inflate(R.layout.song_list_item, parent, false);
                return new SongViewHolder(vSong);
            case TYPE_ALBUM:
                ViewGroup vAlbum = (ViewGroup) mInflater.inflate(R.layout.search_album_item, parent, false);
                return new AlbumViewHolder(vAlbum);
            case TYPE_ARTIST:
                ViewGroup vArtist = (ViewGroup) mInflater.inflate(R.layout.search_artist_item, parent, false);
                return new ArtistViewHolder(vArtist);
            default:
                ViewGroup vDefault = (ViewGroup) mInflater.inflate(R.layout.song_list_item, parent, false);
                return new SongViewHolder(vDefault);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (currentSearchResults.get(position).getResultType()) {
            case SONG:
                SongViewHolder songViewHolder = (SongViewHolder) holder;
                songViewHolder.songTitle.setText(currentSearchResults.get(position).getMainTitle());
                songViewHolder.songDuration.setText(currentSearchResults.get(position).getSubTitle());
                break;

            case ALBUM:
                AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
                albumViewHolder.albumTitle.setText(currentSearchResults.get(position).getMainTitle());
                albumViewHolder.artistTitle.setText(currentSearchResults.get(position).getSubTitle());
                break;

            case ARTIST:
                ArtistViewHolder artistViewHolder = (ArtistViewHolder) holder;
                artistViewHolder.artistName.setText(currentSearchResults.get(position).getMainTitle());
                artistViewHolder.artistDetails.setText(currentSearchResults.get(position).getSubTitle());
                break;

        }

    }

    @Override
    public int getItemCount() {
        return currentSearchResults.size();
    }

    @Override
    public Filter getFilter() {
        if (musicFilter == null) {
            musicFilter = new MusicFilter();
        }
        return musicFilter;
    }

    private class MusicFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                filterResults.values = originalSearchResults;
                filterResults.count = originalSearchResults.size();
            } else {
                ArrayList<SearchResult> filteredSongList = new ArrayList<SearchResult>();
                for (SearchResult result : originalSearchResults) {
                    if (result.getMainTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                            || result.getSubTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredSongList.add(result);
                    }
                }
                filterResults.values = filteredSongList;
                filterResults.count = filteredSongList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                currentSearchResults = (ArrayList<SearchResult>) results.values;
                SearchAdapter.this.notifyDataSetChanged();
            } else {
                SearchAdapter.this.notifyDataSetChanged();
            }
        }
    }


    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView songTitle;
        public TextView songDuration;

        public SongViewHolder(View v) {
            super(v);
            songTitle = (TextView) v.findViewById(R.id.txt_song_item_title);
            songDuration = (TextView) v.findViewById(R.id.txt_song_item_duration);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            List<SearchResult> singleList = new ArrayList<>(Collections.singletonList(currentSearchResults.get(getLayoutPosition())));

            Log.v("lnln", "position - " + singleList.get(0).getMainTitle());
            Log.v("lnln", "position - " + singleList.get(0).getSubTitle());
//            Intent playerActivity = new Intent(context, SwipePlayerActivity.class);
//            playerActivity.putParcelableArrayListExtra("songs",
//                    (ArrayList<? extends Parcelable>) singleList);
//            playerActivity.putExtra("songPosition", 0);
//            context.startActivity(playerActivity);
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView albumTitle;
        public TextView artistTitle;

        public AlbumViewHolder(View v) {
            super(v);
            albumTitle = (TextView) v.findViewById(R.id.txt_album_title);
            artistTitle = (TextView) v.findViewById(R.id.txt_album_artist);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            List<SearchResult> singleList = new ArrayList<>(Collections.singletonList(currentSearchResults.get(getLayoutPosition())));

            Log.v("lnln", "position - " + singleList.get(0).getMainTitle());
            Log.v("lnln", "position - " + singleList.get(0).getSubTitle());
//            Intent playerActivity = new Intent(context, SwipePlayerActivity.class);
//            playerActivity.putParcelableArrayListExtra("songs",
//                    (ArrayList<? extends Parcelable>) singleList);
//            playerActivity.putExtra("songPosition", 0);
//            context.startActivity(playerActivity);
        }
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView artistName;
        public TextView artistDetails;

        public ArtistViewHolder(View v) {
            super(v);
            artistName = (TextView) v.findViewById(R.id.txt_artist_name);
            artistDetails = (TextView) v.findViewById(R.id.txt_artist_details);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            List<SearchResult> singleList = new ArrayList<>(Collections.singletonList(currentSearchResults.get(getLayoutPosition())));

            Log.v("lnln", "position - " + singleList.get(0).getMainTitle());
            Log.v("lnln", "position - " + singleList.get(0).getSubTitle());
//            Intent playerActivity = new Intent(context, SwipePlayerActivity.class);
//            playerActivity.putParcelableArrayListExtra("songs",
//                    (ArrayList<? extends Parcelable>) singleList);
//            playerActivity.putExtra("songPosition", 0);
//            context.startActivity(playerActivity);
        }
    }
}