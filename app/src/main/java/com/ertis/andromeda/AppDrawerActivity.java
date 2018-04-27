package com.ertis.andromeda;

import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.managers.AppsLoader;
import com.ertis.andromeda.managers.SpannedGridLayoutManager;
import com.ertis.andromeda.models.AppModel;
import com.ertis.andromeda.models.Tile;

import java.util.ArrayList;
import java.util.List;

public class AppDrawerActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<ArrayList<AppModel>>
{
	private List<Tile> tileList = new ArrayList<>();
	private RecyclerView recyclerView;
	private TilesAdapter tilesAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_drawer);
		
		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		
		/*
		SpannedGridLayoutManager.GridSpanLookup gridSpanLookup = new SpannedGridLayoutManager.GridSpanLookup()
		{
			@Override
			public SpannedGridLayoutManager.SpanInfo getSpanInfo(int position)
			{
				Tile.TileType tileType = tileList.get(position).getTileType();
				switch (tileType)
				{
					case Small:
						return new SpannedGridLayoutManager.SpanInfo(1, 1);
					case Medium:
						return new SpannedGridLayoutManager.SpanInfo(2, 2);
					case MediumWide:
						return new SpannedGridLayoutManager.SpanInfo(4, 2);
					case Big:
						return new SpannedGridLayoutManager.SpanInfo(4, 4);
				}
				
				return new SpannedGridLayoutManager.SpanInfo(2, 2);
			}
		};
		
		RecyclerView.LayoutManager mLayoutManager = new SpannedGridLayoutManager(gridSpanLookup, 6, 1);
		*/
		
		SpannedGridLayoutManager spannedGridLayoutManager = new SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL, 6);
		spannedGridLayoutManager.setItemOrderIsStable(true);
		recyclerView.setLayoutManager(spannedGridLayoutManager);
		
		tilesAdapter = new TilesAdapter(this, tileList);
		recyclerView.setAdapter(tilesAdapter);
		
		// create the loader to load the apps list in background
		getLoaderManager().initLoader(0, null, this);
		
		// FullScreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
		//loadTiles();
	}
	
	private void loadTiles(ArrayList<AppModel> appList)
	{
		if (appList == null)
		{
			this.tileList.clear();
			this.tilesAdapter.notifyDataSetChanged();
			return;
		}
		
		// appList.size()
		for (int i = 0; i < 22; i++)
		{
			AppModel application = appList.get(i);
			tileList.add(new Tile(application, this.GetTileType2(i + 1)));
		}
		
		this.tilesAdapter.notifyDataSetChanged();
	}
	
	private Tile.TileType GetTileType(int index)
	{
		switch (index)
		{
			case 1 : return Tile.TileType.Medium;
			case 2 : return Tile.TileType.Medium;
			case 3 : return Tile.TileType.Small;
			case 4 : return Tile.TileType.Small;
			case 5 : return Tile.TileType.Small;
			case 6 : return Tile.TileType.Small;
			case 7 : return Tile.TileType.Medium;
			case 8 : return Tile.TileType.MediumWide;
			case 9 : return Tile.TileType.Small;
			case 10 : return Tile.TileType.Small;
			case 11 : return Tile.TileType.Medium;
			case 12 : return Tile.TileType.Medium;
			case 13 : return Tile.TileType.Small;
			case 14 : return Tile.TileType.Small;
			case 15 : return Tile.TileType.MediumWide;
			case 16 : return Tile.TileType.Medium;
			case 17 : return Tile.TileType.Big;
			case 18 : return Tile.TileType.Medium;
			case 19 : return Tile.TileType.Small;
			case 20 : return Tile.TileType.Small;
			case 21 : return Tile.TileType.Small;
			case 22 : return Tile.TileType.Small;
			
			case 23 : return Tile.TileType.Medium;
			case 24 : return Tile.TileType.Medium;
			case 25 : return Tile.TileType.Small;
			case 26 : return Tile.TileType.Small;
			default:
			{
				return this.GetTileType(index - 26);
			}
		}
	}
	
	private Tile.TileType GetTileType2(int index)
	{
		switch (index)
		{
			case 1 : return Tile.TileType.Medium;
			case 2 : return Tile.TileType.Small;
			case 3 : return Tile.TileType.Small;
			case 4 : return Tile.TileType.Small;
			case 5 : return Tile.TileType.Small;
			case 6 : return Tile.TileType.MediumWide;
			case 7 : return Tile.TileType.Small;
			case 8 : return Tile.TileType.Small;
			case 9 : return Tile.TileType.Medium;
			case 10 : return Tile.TileType.Medium;
			case 11 : return Tile.TileType.Medium;
			case 12 : return Tile.TileType.Medium;
			case 13 : return Tile.TileType.MediumWide;
			case 14 : return Tile.TileType.Small;
			case 15 : return Tile.TileType.Small;
			case 16 : return Tile.TileType.Medium;
			case 17 : return Tile.TileType.Medium;
			case 18 : return Tile.TileType.Small;
			case 19 : return Tile.TileType.Small;
			case 20 : return Tile.TileType.MediumWide;
			case 21 : return Tile.TileType.Medium;
			case 22 : return Tile.TileType.Big;
			case 23 : return Tile.TileType.Medium;
			case 24 : return Tile.TileType.Small;
			case 25 : return Tile.TileType.Small;
			case 26 : return Tile.TileType.Small;
			
			default:
			{
				return this.GetTileType2(index - 26);
			}
		}
	}
	
	@Override
	public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle args)
	{
		return new AppsLoader(this);
	}
	
	@Override
	public void onLoadFinished(Loader<ArrayList<AppModel>> loader, ArrayList<AppModel> data)
	{
		this.loadTiles(data);
	}
	
	@Override
	public void onLoaderReset(Loader<ArrayList<AppModel>> loader)
	{
		this.loadTiles(null);
	}
}
