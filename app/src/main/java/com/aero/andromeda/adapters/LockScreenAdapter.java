package com.aero.andromeda.adapters;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import com.aero.andromeda.AppDrawerFragment;
import com.aero.andromeda.AppListFragment;
import com.aero.andromeda.MainActivity;
import com.aero.andromeda.R;
import com.aero.andromeda.managers.TilePopupMenuManager;
import com.aero.andromeda.receivers.ScreenLockReceiver;
import com.aero.andromeda.slideup.SlideUp;
import com.aero.andromeda.slideup.SlideUpBuilder;

public class LockScreenAdapter
{
	private final MainActivity mainActivity;
	private final AppDrawerFragment appDrawerFragment;
	private final AppListFragment appListFragment;
	
	private SlideUp slideUp;
	private View sliderView;
	
	private ObjectAnimator bounceAnimation;
	
	private ScreenLockReceiver screenStateReceiver;
	
	public LockScreenAdapter(final MainActivity mainActivity)
	{
		this.mainActivity = mainActivity;
		
		this.appDrawerFragment = this.mainActivity.getAppDrawerFragment();
		this.appListFragment = this.mainActivity.getAppListFragment();
		
		this.sliderView = this.mainActivity.findViewById(R.id.slideView);
		
		this.slideUp = new SlideUpBuilder(this.sliderView).withStartGravity(Gravity.TOP).withLoggingEnabled(true).withStartState(SlideUp.State.SHOWED).withListeners(new SlideUp.Listener.Events()
		{
			@Override
			public void onSlide(float percent, boolean isShowOrHide)
			{
				if (percent == 0 && !isShowOrHide)
					bounceAnimation.start();
				else
					bounceAnimation.cancel();
			}
			
			@Override
			public void onVisibilityChanged(int visibility)
			{
				switch (visibility)
				{
					case View.GONE:
						appDrawerFragment.Enable();
						appListFragment.Enable();
						mainActivity.UnlockNavigationDrawer();
						
						break;
					case View.VISIBLE:
						appDrawerFragment.Disable();
						appListFragment.Disable();
						mainActivity.LockNavigationDrawer();
						//appService.GetTilesAdapter().notifyDataSetChanged();
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
		this.appDrawerFragment.Disable();
		this.appListFragment.Disable();
		this.mainActivity.LockNavigationDrawer();
		
		this.RegisterToScreenLockBroadcast();
		this.bounceAnimation = this.CreateBounceAnimation(this.sliderView);
		
		this.setClockFonts();
	}
	
	public boolean isLocked()
	{
		return this.slideUp.isVisible() || this.slideUp.isAnimationRunning();
	}
	
	private void RegisterToScreenLockBroadcast()
	{
		if (this.screenStateReceiver == null)
			this.screenStateReceiver = new ScreenLockReceiver(this.slideUp);
		
		IntentFilter screenStateFilter = new IntentFilter();
		screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
		screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
		this.mainActivity.registerReceiver(this.screenStateReceiver, screenStateFilter);
	}
	
	private ObjectAnimator CreateBounceAnimation(View targetView)
	{
		ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationY", 0, -150, 0);
		animator.setInterpolator(new BounceInterpolator());
		animator.setDuration(1200);
		
		return animator;
	}
	
	public ScreenLockReceiver getScreenStateReceiver()
	{
		return screenStateReceiver;
	}
	
	public void setWallpaper(Drawable wallpaper)
	{
		this.sliderView.setBackground(wallpaper);
	}
	
	public void lock()
	{
		TilePopupMenuManager.Current().CloseAllMenus();
		this.slideUp.show();
	}
	
	private void setClockFonts()
	{
		Typeface segoeTypeface = Typeface.createFromAsset(this.mainActivity.getAssets(), "fonts/segoewp/segoe-wp-light.ttf");
		
		TextView hourTextView = (TextView) this.mainActivity.findViewById(R.id.hour_text);
		hourTextView.setTypeface(segoeTypeface);
		
		TextView dayOfWeeokTextView = (TextView) this.mainActivity.findViewById(R.id.dayofweek_text);
		dayOfWeeokTextView.setTypeface(segoeTypeface);
		
		TextView dateTextView = (TextView) this.mainActivity.findViewById(R.id.date_text);
		dateTextView.setTypeface(segoeTypeface);
	}
}
