package com.aero.andromeda;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.aero.andromeda.adapters.LockScreenAdapter;
import com.aero.andromeda.adapters.ViewPagerAdapter;
import com.aero.andromeda.blur.BlurDrawerLayout;
import com.aero.andromeda.helpers.SizeConverter;
import com.aero.andromeda.managers.TileFolderManager;
import com.aero.andromeda.managers.TileOrderManager;
import com.aero.andromeda.receivers.WallpaperChangedReceiver;
import com.aero.andromeda.services.AppService;
import com.aero.andromeda.services.BadgeIntentService;
import com.aero.andromeda.services.SearchService;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.SettingsService;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.services.interfaces.IBadgeIntentService;
import com.aero.andromeda.services.interfaces.ISearchService;
import com.aero.andromeda.services.interfaces.ISettingsService;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.nav.DaliBlurDrawerToggle;
import at.favre.lib.dali.builder.nav.NavigationDrawerListener;

public class MainActivity extends FragmentActivity
{
	private TestFragment testFragment;
	private AppDrawerFragment appDrawerFragment;
	private AppListFragment appListFragment;
	
	private ViewPagerAdapter viewPagerAdapter;
	private LockScreenAdapter lockScreenAdapter;
	
	private FrameLayout baseLayout;
	private FrameLayout baseLayoutCover;
	private BlurDrawerLayout navigationDrawerLayout;
	
	public AppDrawerFragment getAppDrawerFragment()
	{
		return appDrawerFragment;
	}
	
	public AppListFragment getAppListFragment()
	{
		return appListFragment;
	}
	
	public TestFragment getTestFragment()
	{
		return testFragment;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setTheme(R.style.AppTheme);
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			getWindow().setNavigationBarColor(getResources().getColor(R.color.colorAccent));
		}
		
		// FullScreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
		ISettingsService settingsService = new SettingsService();
		ServiceLocator.Current().RegisterInstance(settingsService);
		
		SizeConverter.Init(this);
		
		setContentView(R.layout.activity_main);
		ServiceLocator.Current().RegisterInstance(this);
		
		IBadgeIntentService badgeIntentService = new BadgeIntentService();
		ServiceLocator.Current().RegisterInstance(badgeIntentService);
		
		this.baseLayout = findViewById(R.id.baseLayout);
		this.baseLayoutCover = findViewById(R.id.baseLayoutCover);
		this.navigationDrawerLayout = findViewById(R.id.drawer_layout);
		
		IAppService appService = new AppService(this);
		ServiceLocator.Current().RegisterInstance(appService);
		
		ISearchService searchService = new SearchService(appService);
		ServiceLocator.Current().RegisterInstance(searchService);
		
		this.testFragment = TestFragment.newInstance();
		this.appDrawerFragment = AppDrawerFragment.newInstance();
		this.appListFragment = AppListFragment.newInstance();
		
		this.viewPagerAdapter = new ViewPagerAdapter(this);
		this.lockScreenAdapter = new LockScreenAdapter(this);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		this.SetWallpaper();
		
		this.viewPagerAdapter.SwipeToHome();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		this.appDrawerFragment.Enable();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		if (Andromeda.isEditMode)
		{
			TileOrderManager.Current().ExitEditMode();
		}
		
		this.appDrawerFragment.Disable();
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		
		this.HideKeyboard();
	}
	
	@Override
	protected void onRestart()
	{
		super.onRestart();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		try
		{
			unregisterReceiver(this.lockScreenAdapter.getScreenStateReceiver());
		}
		catch (Exception ex)
		{
			Log.e("onDestroy", ex.getMessage());
		}
	}
	
	@Override
	public void onBackPressed()
	{
		if (Andromeda.isEditMode)
		{
			TileOrderManager.Current().ExitEditMode();
			return;
		}
		
		if (this.viewPagerAdapter.getCurrentPageIndex() == ViewPagerAdapter.HOME_PAGE_NO)
		{
			// If the user is currently looking at the first step, allow the system to handle the
			// Back button. This calls finish() on this activity and pops the back stack.
			//super.onBackPressed();
			
			if (TileFolderManager.Current.IsFolderOpened())
				TileFolderManager.Current.CloseFolder();
			else
				this.lockScreenAdapter.lock();
		}
		else
		{
			// Otherwise, select the previous step.
			this.viewPagerAdapter.setCurrentPageIndex(this.viewPagerAdapter.getCurrentPageIndex() - 1);
		}
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
		this.lockScreenAdapter.setWallpaper(wallpaperDrawable);
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
	
	public void CloseNavigationDrawer()
	{
		this.navigationDrawerLayout.closeDrawers();
	}
	
	public void LockNavigationDrawer()
	{
		this.navigationDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}
	
	public void UnlockNavigationDrawer()
	{
		this.navigationDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	}
	
	public void LockViewPager()
	{
		this.viewPagerAdapter.LockViewPager();
	}
	
	public void UnlockViewPager()
	{
		this.viewPagerAdapter.UnlockViewPager();
	}
	
	public void SwipeToHome()
	{
		this.viewPagerAdapter.SwipeToHome();
	}
	
	public void SwipeToAppList()
	{
		this.viewPagerAdapter.SwipeToAppList();
	}
	
	public void CoverDarkBackground()
	{
		this.baseLayoutCover.setVisibility(View.VISIBLE);
	}
	
	public void UncoverDarkBackground()
	{
		this.baseLayoutCover.setVisibility(View.INVISIBLE);
	}
}
