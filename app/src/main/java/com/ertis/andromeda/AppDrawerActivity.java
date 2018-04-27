package com.ertis.andromeda;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.ertis.andromeda.adapters.AppMenuAdapter;
import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.managers.AppsLoader;
import com.ertis.andromeda.models.AppMenuItem;
import com.ertis.andromeda.models.AppModel;
import com.ertis.andromeda.models.Tile;
import com.ertis.andromeda.utilities.TypefaceUtil;

import java.util.ArrayList;
import java.util.List;

public class AppDrawerActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<ArrayList<AppModel>>
{
	private AppDrawerFragment appDrawerFragment;
	private AppListFragment appListFragment;
	
	private TilesAdapter tilesAdapter;
	private AppMenuAdapter menuItemAdapter;
	
	private List<Tile> tileList = new ArrayList<>();
	private List<AppMenuItem> menuItemList = new ArrayList<>();
	
	private static final int NUM_PAGES = 2;
	private ViewPager viewPager;
	private PagerAdapter viewPagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_drawer);
		
		TypefaceUtil.overrideFont(getApplicationContext(), "SANS", "fonts/segoeui.ttf");
		
		this.tilesAdapter = new TilesAdapter(this, tileList);
		this.appDrawerFragment = AppDrawerFragment.newInstance(tilesAdapter);
		
		this.menuItemAdapter = new AppMenuAdapter(this, this.menuItemList);
		this.appListFragment = AppListFragment.newInstance(this.menuItemAdapter);
		
		this.viewPager = (ViewPager) findViewById(R.id.viewpager);
		this.viewPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		this.viewPager.setAdapter(this.viewPagerAdapter);
		
		// create the loader to load the apps list in background
		getLoaderManager().initLoader(0, null, this);
		
		// FullScreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
		//this.loadFragment(appListFragment);
	}
	
	@Override
	public void onBackPressed()
	{
		if (this.viewPager.getCurrentItem() == 0)
		{
			// If the user is currently looking at the first step, allow the system to handle the
			// Back button. This calls finish() on this activity and pops the back stack.
			super.onBackPressed();
		}
		else
		{
			// Otherwise, select the previous step.
			this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() - 1);
		}
	}
	
	/*
	private void loadFragment(Fragment fragment)
	{
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.frameLayout, fragment);
		fragmentTransaction.commit();
	}
	*/
	
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
	
	private void loadMenuItemList(ArrayList<AppModel> appList)
	{
		if (appList == null)
		{
			this.menuItemList.clear();
			this.menuItemAdapter.notifyDataSetChanged();
			return;
		}
		
		// appList.size()
		for (int i = 0; i < appList.size(); i++)
		{
			AppModel application = appList.get(i);
			menuItemList.add(new AppMenuItem(application));
		}
		
		this.menuItemAdapter.notifyDataSetChanged();
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
		this.loadMenuItemList(data);
	}
	
	@Override
	public void onLoaderReset(Loader<ArrayList<AppModel>> loader)
	{
		this.loadTiles(null);
		this.loadMenuItemList(null);
	}
	
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
	{
		public ScreenSlidePagerAdapter(FragmentManager fm)
		{
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position)
		{
			switch (position)
			{
				case 0:
					return appDrawerFragment;
				case 1:
					return appListFragment;
			}
			
			return appDrawerFragment;
		}
		
		@Override
		public int getCount()
		{
			return NUM_PAGES;
		}
	}
}
