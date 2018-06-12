package com.ertis.andromeda;

import android.animation.ObjectAnimator;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.managers.TileFolderManager;
import com.ertis.andromeda.managers.TileOrderManager;
import com.ertis.andromeda.receivers.ScreenLockReceiver;
import com.ertis.andromeda.receivers.WallpaperChangedReceiver;
import com.ertis.andromeda.services.AppService;
import com.ertis.andromeda.services.IAppService;
import com.ertis.andromeda.services.ServiceLocator;
import com.ertis.andromeda.slideup.SlideUp;
import com.ertis.andromeda.slideup.SlideUpBuilder;
import com.ertis.andromeda.utilities.TypefaceUtil;

public class AppDrawerActivity extends FragmentActivity
{
	private static final int NUM_PAGES = 2;
	private IAppService appService;
	private AppDrawerFragment appDrawerFragment;
	private AppListFragment appListFragment;
	private FrameLayout baseLayout;
	private ViewPager viewPager;
	private PagerAdapter viewPagerAdapter;
	private TileOrderManager tileOrderManager;
	
	private SlideUp slideUp;
	private View sliderView;
	
	private ObjectAnimator bounceAnimation;
	private ScreenLockReceiver screenStateReceiver;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_drawer);
		
		TypefaceUtil.overrideFont(getApplicationContext(), "SANS", "fonts/segoewp/segoe-wp-light.ttf");
		
		this.appService = new AppService(this);
		ServiceLocator.Current().RegisterInstance(this.appService);
		
		TilesAdapter tilesAdapter = this.appService.GetTilesAdapter();
		this.appDrawerFragment = AppDrawerFragment.newInstance(tilesAdapter);
		this.appListFragment = AppListFragment.newInstance(this.appService.GetMenuItemAdapter());
		
		this.tileOrderManager = new TileOrderManager(this, this.appDrawerFragment, tilesAdapter);
		ServiceLocator.Current().RegisterInstance(this.tileOrderManager);
		
		this.viewPager = findViewById(R.id.viewpager);
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
				}
				else
				{
					int aValue = (int) (max * positionOffset);
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
		
		slideUp = new SlideUpBuilder(sliderView).withStartGravity(Gravity.TOP).withLoggingEnabled(true).withStartState(SlideUp.State.SHOWED).withListeners(new SlideUp.Listener.Events()
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
					case View.GONE:
						appDrawerFragment.Enable();
						appListFragment.Enable();
						break;
					case View.VISIBLE:
						appDrawerFragment.Disable();
						appListFragment.Disable();
						appService.GetTilesAdapter().notifyDataSetChanged();
						break;
				}
			}
		}).build();
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
		
		this.RegisterToScreenLockBroadcast();
		
		bounceAnimation = CreateBounceAnimation(this.sliderView);
		
		this.setClockFonts();
		
		this.SetWallpaper();
		
		// FullScreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
		//this.loadFragment(appListFragment);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		try
		{
			unregisterReceiver(this.screenStateReceiver);
		}
		catch (Exception ex)
		{
			Log.e("onDestroy", ex.getMessage());
		}
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
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		// this.HideKeyboard();
	}
	
	private void HideKeyboard()
	{
		View view = this.getCurrentFocus();
		if (view != null)
		{
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
	
	public AppDrawerFragment getAppDrawerFragment()
	{
		return appDrawerFragment;
	}
	
	public AppListFragment getAppListFragment()
	{
		return appListFragment;
	}
	
	private void SetWallpaper()
	{
		this.RefreshWallpaper();
		
		IntentFilter filter = new IntentFilter("android.intent.action.WALLPAPER_CHANGED");
		
		WallpaperChangedReceiver wallpaperReceiver = new WallpaperChangedReceiver(this);
		registerReceiver(wallpaperReceiver, filter);
	}
	
	public void RefreshWallpaper()
	{
		final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
		final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
		
		this.baseLayout.setBackground(wallpaperDrawable);
		this.sliderView.setBackground(wallpaperDrawable);
	}
	
	private void RegisterToScreenLockBroadcast()
	{
		if (this.screenStateReceiver == null)
			this.screenStateReceiver = new ScreenLockReceiver(this.slideUp);
		
		IntentFilter screenStateFilter = new IntentFilter();
		screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
		screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(this.screenStateReceiver, screenStateFilter);
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
		
		TextView hourTextView = (TextView) findViewById(R.id.hour_text);
		hourTextView.setTypeface(segoeTypeface);
		
		TextView dayOfWeeokTextView = (TextView) findViewById(R.id.dayofweek_text);
		dayOfWeeokTextView.setTypeface(segoeTypeface);
		
		TextView dateTextView = (TextView) findViewById(R.id.date_text);
		dateTextView.setTypeface(segoeTypeface);
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
