package com.laithlab.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.laithlab.core.R;
import com.laithlab.core.db.Song;
import com.laithlab.core.dto.SongDTO;
import com.squareup.picasso.Picasso;
import io.realm.RealmList;

import java.util.List;

public class SongGridAdapter extends BaseAdapter {
	private final Context context;
	private final List<SongDTO> songs;
	private final LayoutInflater inflater;

	public SongGridAdapter(Context context, List<SongDTO> songs) {
		this.context = context;
		this.songs = songs;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null || convertView.getTag() == null) {
			convertView = inflater.inflate(R.layout.grid_item, parent, false);

			holder = new ViewHolder();
			holder.gridItemImage = (ImageView) convertView.findViewById(R.id.grid_image);
			holder.gridItemImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
			holder.gridItemTitle = (TextView) convertView.findViewById(R.id.grid_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Picasso.with(context).load("http://artwork-cdn.7static.com/static/img/sleeveart/00/009/559/0000955983_200.jpg")
				.into(holder.gridItemImage);

		holder.gridItemTitle.setText(songs.get(position).getSongTitle());
		return convertView;
	}

	@Override
	public int getCount() {
		return songs.size();
	}

	@Override
	public SongDTO getItem(int position) {
		return songs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private static class ViewHolder {
		ImageView gridItemImage;
		TextView gridItemTitle;
	}
}