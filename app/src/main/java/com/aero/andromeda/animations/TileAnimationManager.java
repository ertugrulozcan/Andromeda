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
import com.aero.andromeda.animations.tileanimations.FlipAnimation;
import com.aero.andromeda.animations.tileanimations.ITileAnimation;
import com.aero.andromeda.models.tiles.FakeTile;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.models.tiles.Folder;
import com.aero.andromeda.services.interfaces.IAppService;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TileAnimationManager
{
	private final IAppService appService;
	private final AnimationTask animationTask;
	
	private final Class[] AnimationTypes = { FlipAnimation.class };
	
	public TileAnimationManager(IAppService appService)
	{
		this.appService = appService;
		this.animationTask = new AnimationTask();
	}
	
	public void Start()
	{
		this.animationTask.Start();
	}
	
	public void Stop()
	{
		this.animationTask.Stop();
	}
	
	private ITileAnimation GenerateTileAnimation(int no)
	{
		switch (no)
		{
			case 0: return new FlipAnimation(appService.getMainContext());
		}
		
		return null;
	}
	
	class AnimationTask
	{
		private Timer timer;
		
		private ITileAnimation tileAnimation1;
		private ITileAnimation tileAnimation2;
		private ITileAnimation tileAnimation3;
		
		public void Start()
		{
			this.Stop();
			
			this.timer = new Timer();
			timer.schedule(new AnimationTimer(), 100, 3600);
		}
		
		public void Stop()
		{
			if (tileAnimation1 != null)
				tileAnimation1.Stop();
			
			if (tileAnimation2 != null)
				tileAnimation2.Stop();
			
			if (tileAnimation3 != null)
				tileAnimation3.Stop();
			
			tileAnimation1 = null;
			tileAnimation2 = null;
			tileAnimation3 = null;
			
			if (this.timer != null)
				this.timer.cancel();
			
			this.timer = null;
		}
		
		class AnimationTimer extends TimerTask
		{
			@Override
			public void run()
			{
				try
				{
					List<TileBase> tiles = appService.getTileList();
					if (tiles != null && tiles.size() > 0)
					{
						Random random = new Random();
						
						int rand1 = this.GenerateRandomIndex(random, tiles.size() - 1, -1);
						final TileBase tile1 = tiles.get(rand1);
						tileAnimation1 = GenerateTileAnimation(random.nextInt(AnimationTypes.length));
						
						int rand2 = this.GenerateRandomIndex(random, tiles.size() - 1, rand1);
						final TileBase tile2 = tiles.get(rand2);
						tileAnimation2 = GenerateTileAnimation(random.nextInt(AnimationTypes.length));
						
						int rand3 = this.GenerateRandomIndex(random, tiles.size() - 1, rand2);
						final TileBase tile3 = tiles.get(rand3);
						tileAnimation3 = GenerateTileAnimation(random.nextInt(AnimationTypes.length));
						
						tileAnimation1.Start(tile1);
						tileAnimation2.Start(tile2, 1000);
						tileAnimation3.Start(tile3, 3000);
					}
				}
				catch (Exception ex)
				{
					System.err.println("AnimationTask error! : " + ex.getMessage());
				}
			}
			
			private int GenerateRandomIndex(Random random, int bound, int ignore)
			{
				int rand;
				
				do { rand = random.nextInt(bound); }
				while (rand == ignore);
				
				return rand;
			}
		}
	}
}