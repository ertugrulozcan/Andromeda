package com.aero.andromeda.animations;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.aero.andromeda.R;
import com.aero.andromeda.adapters.TilesAdapter;
import com.aero.andromeda.models.tiles.FakeTile;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.models.tiles.Folder;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TileAnimationManager
{
	private Context parentContext;
	private TilesAdapter tilesAdapter;
	
	private Timer timer;
	
	public TileAnimationManager(Context context, TilesAdapter tilesAdapter)
	{
		this.parentContext = context;
		this.tilesAdapter = tilesAdapter;
		
		this.timer = new Timer();
	}
	
	public void Start()
	{
	
	}
	
	public void Stop()
	{
	
	}
	
	class AnimationTask extends TimerTask
	{
		@Override
		public void run()
		{
			try
			{
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
					
					//AnimateTileFlip(view1);
					//AnimateTileFlip(view2);
					//AnimateTileFlip(view3);
				}
			}
			catch (Exception ex)
			{
				System.err.println("AnimationTask error! : " + ex.getMessage());
			}
		}
	}
}