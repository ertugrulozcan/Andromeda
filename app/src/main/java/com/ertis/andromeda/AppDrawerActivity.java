package com.ertis.andromeda;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.ertis.andromeda.adapters.AppMenuAdapter;
import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.helpers.Colors;
import com.ertis.andromeda.managers.AppsLoader;
import com.ertis.andromeda.models.AppMenuItem;
import com.ertis.andromeda.models.AppModel;
import com.ertis.andromeda.models.Tile;
import com.ertis.andromeda.utilities.TypefaceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
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
		
		TypefaceUtil.overrideFont(getApplicationContext(), "SANS", "font/segoeui.ttf");
		
		this.tilesAdapter = new TilesAdapter(this, tileList);
		this.appDrawerFragment = AppDrawerFragment.newInstance(tilesAdapter);
		
		this.menuItemAdapter = new AppMenuAdapter(this, this.menuItemList);
		this.appListFragment = AppListFragment.newInstance(this.menuItemAdapter);
		
		this.viewPager = (ViewPager) findViewById(R.id.viewpager);
		this.viewPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		this.viewPager.setAdapter(this.viewPagerAdapter);
		
		this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
				int max = 0xB1;
				
				if (position == 1)
				{
					appListFragment.setBackgroundColor(max * 0x1000000);
				}
				else
				{
					int aValue = (int)(max * positionOffset);
					int colorValue = aValue * 0x1000000;
					
					appListFragment.setBackgroundColor(colorValue);
				}
			}
			
			@Override
			public void onPageSelected(int position)
			{
			
			}
			
			@Override
			public void onPageScrollStateChanged(int state)
			{
			
			}
		});
		
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
	
	private void loadTiles(final ArrayList<AppModel> appList)
	{
		if (appList == null)
		{
			this.tileList.clear();
			this.tilesAdapter.notifyDataSetChanged();
			return;
		}
		
		try
		{
			String jsonStr = this.ReadTileLayoutsFromJsonResource();
			
			JSONObject jsonObj = new JSONObject(jsonStr);
			JSONArray tiles = jsonObj.getJSONArray("tiles");
			
			for (int i = 0; i < tiles.length(); i++)
			{
				JSONObject tileData = tiles.getJSONObject(i);
				String appPath = tileData.getString("appPath");
				int tileTypeValue = tileData.getInt("tileType");
				int tileStyleValue = tileData.getInt("tileStyle");
				String tileBackgroundStr = tileData.getString("tileBackground");
				
				if (tileTypeValue < 0 || tileTypeValue >= Tile.TileType.values().length)
					tileTypeValue = 0;
				
				if (tileStyleValue < 0 || tileStyleValue >= Tile.TileStyle.values().length)
					tileStyleValue = 0;
				
				Tile.TileType tileType = Tile.TileType.values()[tileTypeValue];
				Tile.TileStyle tileStyle = Tile.TileStyle.values()[tileStyleValue];
				
				ColorDrawable tileColor = Colors.rgb(tileBackgroundStr);
				if (tileBackgroundStr == null || tileBackgroundStr.isEmpty())
					tileColor = new ColorDrawable(getResources().getColor(R.color.colorTileBackground));
				
				Tile tile = null;
				for (int a = 0; a < appList.size(); a++)
				{
					AppModel application = appList.get(a);
					String sourceDir = application.getAppInfo().sourceDir;
					if (appPath.equals(sourceDir))
					{
						tile = new Tile(application, tileType, tileColor, tileStyle);
						continue;
					}
				}
				
				if (tile == null)
					tile = Tile.CreateFakeTile(tileType);
				
				tileList.add(tile);
			}
		}
		catch (Exception ex)
		{
		
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
		
		char lastHeaderChar = '?';
		for (int i = 0; i < appList.size(); i++)
		{
			AppModel application = appList.get(i);
			AppMenuItem item = new AppMenuItem(application);
			
			Character firstLetter = item.getLabel().charAt(0);
			if (!Character.isLetterOrDigit(firstLetter))
				firstLetter = '#';
			
			firstLetter = Character.toUpperCase(firstLetter);
			
			if (firstLetter != lastHeaderChar)
			{
				menuItemList.add(AppMenuItem.CreateHeaderMenuItem(firstLetter.toString()));
				lastHeaderChar = firstLetter;
			}
			
			menuItemList.add(item);
		}
		
		this.menuItemAdapter.notifyDataSetChanged();
	}
	
	private String ReadTileLayoutsFromJsonResource() throws IOException
	{
		InputStream is = getResources().openRawResource(R.raw.tiles_layout);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		
		try
		{
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1)
			{
				writer.write(buffer, 0, n);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			is.close();
		}
		
		String jsonString = writer.toString();
		return jsonString;
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
