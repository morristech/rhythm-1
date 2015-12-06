package com.laithlab.core.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.laithlab.core.R;
import com.laithlab.core.adapter.AlbumGridAdapter;
import com.laithlab.core.dto.AlbumDTO;
import com.laithlab.core.dto.ArtistDTO;

public class ArtistActivity extends AppCompatActivity {

	private DrawerLayout drawerLayout;

	public static Intent getIntent(Context context, ArtistDTO artist) {
		Intent artistActivity = new Intent(context, ArtistActivity.class);
		artistActivity.putExtra("artist", artist);
		return artistActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		Bundle extras = getIntent().getExtras();
		final ArtistDTO currentArtist = extras.getParcelable("artist");

		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
			actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary));
		View tiltedView = findViewById(R.id.tilted_view);
		tiltedView.setPivotX(0f);
		tiltedView.setPivotY(0f);
		tiltedView.setRotation(-5f);

		final GridView albumGrid = (GridView) findViewById(R.id.album_grid);
		albumGrid.setAdapter(new AlbumGridAdapter(this, currentArtist.getAlbums()));
		albumGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent albumActivity = new Intent(ArtistActivity.this, AlbumActivity.class);
				albumActivity.putExtra("album", (AlbumDTO) albumGrid.getItemAtPosition(position));
				albumActivity.putExtra("artist", currentArtist);
				startActivity(albumActivity);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_browse, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int i = item.getItemId();
		if (i == android.R.id.home) {
			drawerLayout.openDrawer(GravityCompat.START);
			return true;
		} else if (i == R.id.search_menu_item) {
			startActivity(SearchActivity.getIntent(this));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
