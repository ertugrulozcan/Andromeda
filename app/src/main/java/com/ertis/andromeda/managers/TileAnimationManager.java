package com.ertis.andromeda.managers;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.ertis.andromeda.R;
import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.models.Tile;
import com.ertis.andromeda.models.TileFolder;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TileAnimationManager
{
	private Context parentContext;
	private TilesAdapter tilesAdapter;
	
	private Timer timer;
	
	private boolean isEnabled;
	
	private AnimatorSet tileFlipAnimation = null;
	private AnimatorSet tileFlipAnimation1, tileFlipAnimation2, tileFlipAnimation3;
	private TileFlipInterpolator tileFlipInterpolator;
	
	public TileAnimationManager(Context context, TilesAdapter tilesAdapter)
	{
		this.parentContext = context;
		this.tilesAdapter = tilesAdapter;
		
		this.timer = new Timer();
		this.tileFlipInterpolator = new TileFlipInterpolator();
		this.LoadAnimations(this.parentContext);
	}
	
	@SuppressLint("ResourceType")
	private void LoadAnimations(Context context)
	{
		tileFlipAnimation1 = (AnimatorSet) AnimatorInflater.loadAnimator(this.parentContext, R.anim.tile_flip);
		tileFlipAnimation2 = (AnimatorSet) AnimatorInflater.loadAnimator(this.parentContext, R.anim.tile_flip);
		tileFlipAnimation3 = (AnimatorSet) AnimatorInflater.loadAnimator(this.parentContext, R.anim.tile_flip);
		
		tileFlipAnimation2.setStartDelay(tileFlipAnimation2.getStartDelay() + 1000);
		tileFlipAnimation3.setStartDelay(tileFlipAnimation3.getStartDelay() + 2000);
		
		timer = new Timer();
		timer.schedule(new TileAnimationManager.AnimationTask(), 100, 3600);
	}
	
	public boolean isEnabled()
	{
		return isEnabled;
	}
	
	public void Start()
	{
		isEnabled = true;
	}
	
	public void Stop()
	{
		isEnabled = false;
	}
	
	private void AnimateTileFlip(final View tileView)
	{
		if (tileView == null)
			return;
		
		Tile tile = tilesAdapter.getDataContext(tileView);
		if (tile.getTileType() == Tile.TileType.FolderTile || tile instanceof TileFolder)
			return;
		
		if (this.parentContext == null || !(this.parentContext instanceof Activity))
			return;
		
		Activity activity = (Activity) this.parentContext;
		
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				synchronized (tilesAdapter)
				{
					if (!tileFlipAnimation1.isRunning())
					{
						tileFlipAnimation = tileFlipAnimation1;
					}
					else if (!tileFlipAnimation2.isRunning())
					{
						tileFlipAnimation = tileFlipAnimation2;
					}
					else if (!tileFlipAnimation3.isRunning())
					{
						tileFlipAnimation = tileFlipAnimation3;
					}
					else
					{
						return;
					}
					
					if (tileFlipAnimation != null)
					{
						tileFlipAnimation.setInterpolator(tileFlipInterpolator);
						tileFlipAnimation.setTarget(tileView);
						tileFlipAnimation.start();
					}
				}
			}
		});
	}
	
	class AnimationTask extends TimerTask
	{
		@Override
		public void run()
		{
			try
			{
				if (!isEnabled)
					return;
				
				List<View> tileViews = tilesAdapter.getTileViewList();
				if (tileViews != null && tileViews.size() > 0)
				{
					Random random = new Random();
					int rand1 = random.nextInt(tileViews.size() - 1);
					final View view1 = tileViews.get(rand1);
					
					int rand2 = random.nextInt(tileViews.size() - 1);
					final View view2 = tileViews.get(rand2);
					
					int rand3 = random.nextInt(tileViews.size() - 1);
					final View view3 = tileViews.get(rand3);
					
					AnimateTileFlip(view1);
					AnimateTileFlip(view2);
					AnimateTileFlip(view3);
				}
			}
			catch (Exception ex)
			{
				System.err.println("AnimationTask error! : " + ex.getMessage());
			}
		}
	}
	
	;
	
	class TileFlipInterpolator implements TimeInterpolator
	{
		@Override
		public float getInterpolation(float t)
		{
			if (t == 1)
				return 1.0f;
			/*
			return (float)(Math.sin(3.6f * t - 2.0f) * 0.522f + 0.476f);
			*/
			if (t < 0.677f)
			{
				return (float) (Math.sin(5f * (t - 0.067f) - 2.0f) * 0.522f + 0.376f);
			}
			else
			{
				return (float) (Math.sin(t - 1.4f + (Math.PI / 2)) + 0.079f);
			}
		}
	}
}
