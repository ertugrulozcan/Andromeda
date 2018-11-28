package com.aero.andromeda.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.aero.andromeda.AppDrawerFragment;
import com.aero.andromeda.AppListFragment;
import com.aero.andromeda.MainActivity;
import com.aero.andromeda.R;
import com.aero.andromeda.TestFragment;

public class ViewPagerAdapter
{
	public static final int NUM_PAGES = 2;
	public static final int HOME_PAGE_NO = 0;
	
	private final MainActivity mainActivity;
	
	private ViewPager viewPager;
	private PagerAdapter viewPagerAdapter;
	
	private final TestFragment testFragment;
	private final AppDrawerFragment appDrawerFragment;
	private final AppListFragment appListFragment;
	
	public ViewPagerAdapter(MainActivity mainActivity)
	{
		this.mainActivity = mainActivity;
		
		this.testFragment = this.mainActivity.getTestFragment();
		this.appDrawerFragment = this.mainActivity.getAppDrawerFragment();
		this.appListFragment = this.mainActivity.getAppListFragment();
		
		this.viewPager = this.mainActivity.findViewById(R.id.viewpager);
		this.viewPagerAdapter = new ScreenSlidePagerAdapter(this.mainActivity.getSupportFragmentManager());
		this.viewPager.setAdapter(this.viewPagerAdapter);
		
		this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
				int max = 0xB1;
				
				if (position == HOME_PAGE_NO + 1)
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
	}
	
	public void SwipeToHome()
	{
		this.viewPager.setCurrentItem(HOME_PAGE_NO);
	}
	
	public void SwipeToAppList()
	{
		this.viewPager.setCurrentItem(HOME_PAGE_NO + 1);
	}
	
	public int getCurrentPageIndex()
	{
		return this.viewPager.getCurrentItem();
	}
	
	public void setCurrentPageIndex(int index)
	{
		this.viewPager.setCurrentItem(index);
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
	
	public void LockViewPager()
	{
	
	}
	
	public void UnlockViewPager()
	{
	
	}
}
