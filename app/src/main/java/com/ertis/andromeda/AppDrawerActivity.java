package com.ertis.andromeda;

import android.animation.ObjectAnimator;
import android.app.WallpaperManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ertis.andromeda.adapters.AppMenuAdapter;
import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.helpers.Colors;
import com.ertis.andromeda.managers.AppsLoader;
import com.ertis.andromeda.managers.TileFolderManager;
import com.ertis.andromeda.models.AppMenuItem;
import com.ertis.andromeda.models.AppModel;
import com.ertis.andromeda.models.Tile;
import com.ertis.andromeda.models.FolderTile;
import com.ertis.andromeda.slideup.SlideUp;
import com.ertis.andromeda.slideup.SlideUpBuilder;
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
	private FrameLayout baseLayout;
	private ViewPager viewPager;
	private PagerAdapter viewPagerAdapter;
	
	private SlideUp slideUp;
	private View sliderView;
	
	private ObjectAnimator bounceAnimation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_drawer);
		
		TypefaceUtil.overrideFont(getApplicationContext(), "SANS", "fonts/segoewp/segoe-wp-light.ttf");
		
		this.tilesAdapter = new TilesAdapter(this, tileList);
		this.appDrawerFragment = AppDrawerFragment.newInstance(tilesAdapter);
		
		this.menuItemAdapter = new AppMenuAdapter(this, this.menuItemList);
		this.appListFragment = AppListFragment.newInstance(this.menuItemAdapter);
		
		this.viewPager = (ViewPager) findViewById(R.id.viewpager);
		this.viewPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		this.viewPager.setAdapter(this.viewPagerAdapter);
		
		this.baseLayout = findViewById(R.id.baseLayout);
		
		this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
				int max = 0xB1;
				
				if (position == 1)
				{
					appListFragment.setBackgroundColor(max * 0x1000000);
					int appCount = menuItemList.size();
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
		
		sliderView = this.findViewById(R.id.slideView);
		
		slideUp = new SlideUpBuilder(sliderView).withStartGravity(Gravity.TOP)
				.withLoggingEnabled(true)
				.withStartState(SlideUp.State.SHOWED)
				.withListeners(new SlideUp.Listener.Events()
				{
					@Override
					public void onSlide(float percent, boolean isShowOrHide)
					{
						if (percent == 0 && !isShowOrHide)
							bounceAnimation.start();
					}
					
					@Override
					public void onVisibilityChanged(int visibility)
					{
						switch (visibility)
						{
							case View.GONE :
								appDrawerFragment.Enable();
								appListFragment.Enable();
								break;
							case View.VISIBLE :
								appDrawerFragment.Disable();
								appListFragment.Disable();
								tilesAdapter.notifyDataSetChanged();
								break;
						}
					}
				})
				.build();
		//.withSlideFromOtherView(anotherView)
		//.withGesturesEnabled()
		//.withHideSoftInputWhenDisplayed()
		//.withInterpolator()
		//.withAutoSlideDuration()
		//.withLoggingEnabled()
		//.withTouchableAreaPx()
		//.withTouchableAreaDp()
		//.withListeners()
		//.withSavedState()
		
		// Slider baslangicta acik geldigi icin;
		appDrawerFragment.Disable();
		appListFragment.Disable();
		
		bounceAnimation = CreateBounceAnimation(this.sliderView);
		
		this.setClockFonts();
		
		final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
		final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
		
		this.baseLayout.setBackground(wallpaperDrawable);
		this.sliderView.setBackground(wallpaperDrawable);
		
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
			//super.onBackPressed();
			
			if (TileFolderManager.Current.IsFolderOpened())
				TileFolderManager.Current.CloseFolder();
			else
				slideUp.show();
		}
		else
		{
			// Otherwise, select the previous step.
			this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() - 1);
		}
	}
	
	private ObjectAnimator CreateBounceAnimation(View targetView)
	{
		ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationY", 0, -150, 0);
		animator.setInterpolator(new BounceInterpolator());
		animator.setDuration(1200);
		
		return animator;
	}
	
	private void setClockFonts()
	{
		Typeface segoeTypeface = Typeface.createFromAsset(getAssets(), "fonts/segoewp/segoe-wp-light.ttf");
		
		TextView hourTextView = (TextView)findViewById(R.id.hour_text);
		hourTextView.setTypeface(segoeTypeface);
		
		TextView dayOfWeeokTextView = (TextView)findViewById(R.id.dayofweek_text);
		dayOfWeeokTextView.setTypeface(segoeTypeface);
		
		TextView dateTextView = (TextView)findViewById(R.id.date_text);
		dateTextView.setTypeface(segoeTypeface);
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
			this.tileList.clear();
			
			String jsonStr = this.ReadTileLayoutsFromJsonResource();
			this.tileList.addAll(this.ExtractTilesFromJson(jsonStr, appList));
		}
		catch (Exception ex)
		{
		
		}
		
		this.tilesAdapter.notifyDataSetChanged();
	}
	
	private List<Tile> ExtractTiles(JSONArray tiles, final ArrayList<AppModel> appList)
	{
		List<Tile> tileList = new ArrayList<>();
		
		try
		{
			for (int i = 0; i < tiles.length(); i++)
			{
				Tile tile = null;
				
				JSONObject tileData = tiles.getJSONObject(i);
				
				int tileTypeValue = tileData.getInt("tileSize");
				if (tileTypeValue < 0 || tileTypeValue >= Tile.TileSize.values().length)
					tileTypeValue = 0;
				
				Tile.TileSize tileSize = Tile.TileSize.values()[tileTypeValue];
				
				int tileStyleValue = tileData.getInt("tileStyle");
				if (tileStyleValue < 0 || tileStyleValue >= Tile.TileStyle.values().length)
					tileStyleValue = 0;
				
				Tile.TileStyle tileStyle = Tile.TileStyle.values()[tileStyleValue];
				
				String tileBackgroundStr = tileData.getString("tileBackground");
				ColorDrawable tileColor = Colors.rgb(tileBackgroundStr);
				if (tileBackgroundStr == null || tileBackgroundStr.isEmpty())
					tileColor = new ColorDrawable(getResources().getColor(R.color.colorTileBackground));
				
				if (!tileData.isNull("packageName"))
				{
					String appPackageName = tileData.getString("packageName");
					for (int a = 0; a < appList.size(); a++)
					{
						AppModel application = appList.get(a);
						if (appPackageName.equals(application.getApplicationPackageName()))
						{
							tile = new Tile(application, tileSize, tileColor, tileStyle);
							
							continue;
						}
					}
					
					if (tile == null)
					{
						tile = Tile.CreateFakeTile(tileSize);
					}
					
					String queryParams = tileData.getString("queryParams");
					tile.setQueryParams(queryParams);
					
					if (queryParams.equals("phoneDialer"))
					{
						Resources res = getResources();
						Drawable drawable = res.getDrawable(R.drawable.phone);
						tile.setCustomIcon(drawable);
						
						tile.setCustomLabel("Phone");
					}
				}
				else if (!tileData.isNull("folderName"))
				{
					String folderName = tileData.getString("folderName");
					tile = new FolderTile(folderName, tileSize);
					FolderTile folderTile = (FolderTile)tile;
					
					Resources res = getResources();
					Drawable drawable = res.getDrawable(R.drawable.tile_folder_bg);
					tile.setCustomIcon(drawable);
					
					JSONArray subTilesArray = tileData.getJSONArray("subTiles");
					List<Tile> subTiles = this.ExtractTiles(subTilesArray, appList);
					folderTile.AddTiles(subTiles);
				}
				
				tileList.add(tile);
			}
			
			return tileList;
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	private List<Tile> ExtractTilesFromJson(String jsonStr, final ArrayList<AppModel> appList)
	{
		try
		{
			JSONObject jsonObj = new JSONObject(jsonStr);
			JSONArray tiles = jsonObj.getJSONArray("tiles");
			
			return this.ExtractTiles(tiles, appList);
		}
		catch (Exception ex)
		{
			return null;
		}
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
	
	private Tile.TileSize GetTileType(int index)
	{
		switch (index)
		{
			case 1 : return Tile.TileSize.Medium;
			case 2 : return Tile.TileSize.Medium;
			case 3 : return Tile.TileSize.Small;
			case 4 : return Tile.TileSize.Small;
			case 5 : return Tile.TileSize.Small;
			case 6 : return Tile.TileSize.Small;
			case 7 : return Tile.TileSize.Medium;
			case 8 : return Tile.TileSize.MediumWide;
			case 9 : return Tile.TileSize.Small;
			case 10 : return Tile.TileSize.Small;
			case 11 : return Tile.TileSize.Medium;
			case 12 : return Tile.TileSize.Medium;
			case 13 : return Tile.TileSize.Small;
			case 14 : return Tile.TileSize.Small;
			case 15 : return Tile.TileSize.MediumWide;
			case 16 : return Tile.TileSize.Medium;
			case 17 : return Tile.TileSize.Large;
			case 18 : return Tile.TileSize.Medium;
			case 19 : return Tile.TileSize.Small;
			case 20 : return Tile.TileSize.Small;
			case 21 : return Tile.TileSize.Small;
			case 22 : return Tile.TileSize.Small;
			
			case 23 : return Tile.TileSize.Medium;
			case 24 : return Tile.TileSize.Medium;
			case 25 : return Tile.TileSize.Small;
			case 26 : return Tile.TileSize.Small;
			default:
			{
				return this.GetTileType(index - 26);
			}
		}
	}
	
	private Tile.TileSize GetTileType2(int index)
	{
		switch (index)
		{
			case 1 : return Tile.TileSize.Medium;
			case 2 : return Tile.TileSize.Small;
			case 3 : return Tile.TileSize.Small;
			case 4 : return Tile.TileSize.Small;
			case 5 : return Tile.TileSize.Small;
			case 6 : return Tile.TileSize.MediumWide;
			case 7 : return Tile.TileSize.Small;
			case 8 : return Tile.TileSize.Small;
			case 9 : return Tile.TileSize.Medium;
			case 10 : return Tile.TileSize.Medium;
			case 11 : return Tile.TileSize.Medium;
			case 12 : return Tile.TileSize.Medium;
			case 13 : return Tile.TileSize.MediumWide;
			case 14 : return Tile.TileSize.Small;
			case 15 : return Tile.TileSize.Small;
			case 16 : return Tile.TileSize.Medium;
			case 17 : return Tile.TileSize.Medium;
			case 18 : return Tile.TileSize.Small;
			case 19 : return Tile.TileSize.Small;
			case 20 : return Tile.TileSize.MediumWide;
			case 21 : return Tile.TileSize.Medium;
			case 22 : return Tile.TileSize.Large;
			case 23 : return Tile.TileSize.Medium;
			case 24 : return Tile.TileSize.Small;
			case 25 : return Tile.TileSize.Small;
			case 26 : return Tile.TileSize.Small;
			
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
