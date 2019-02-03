package com.aero.andromeda.animations;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.aero.andromeda.MainActivity;
import com.aero.andromeda.animations.tileanimations.FlipAnimation;
import com.aero.andromeda.animations.tileanimations.ITileAnimation;
import com.aero.andromeda.animations.tileanimations.SlideAnimation;
import com.aero.andromeda.models.NotificationGroup;
import com.aero.andromeda.models.NotificationInfo;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.services.interfaces.INotificationService;
import com.aero.andromeda.ui.BaseTileViewHolder;
import com.aero.andromeda.ui.TileViewHolder;

import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TileAnimationManager
{
	private static TileAnimationManager self;
	
	public static TileAnimationManager Current()
	{
		if (self == null)
			self = new TileAnimationManager();
		
		return self;
	}
	
	private IAppService appService;
	private AnimationTask animationTask;
	private final Class[] AnimationTypes = { FlipAnimation.class, SlideAnimation.class};
	
	private TileAnimationManager()
	{
		this.appService = ServiceLocator.Current().GetInstance(IAppService.class);
	}
	
	public void Start()
	{
		if (this.animationTask != null)
			return;
		
		this.Stop();
		
		this.animationTask = new AnimationTask();
		this.animationTask.execute();
	}
	
	public void Stop()
	{
		if (this.animationTask == null)
			return;
		
		this.animationTask.Stop();
		this.animationTask = null;
	}
	
	private ITileAnimation GenerateTileAnimation(TileBase tile, int no)
	{
		switch (no)
		{
			case 0:
			default:
				return new FlipAnimation(appService.getMainContext());
			case 1:
			case 2:
			case 3:
			{
				if (tile.getTileSize() != TileBase.TileSize.Small)
				{
					INotificationService notificationService = ServiceLocator.Current().GetInstance(INotificationService.class);
					NotificationGroup notificationGroup = notificationService.GetNotificationGroup(tile);
					if (notificationGroup != null && notificationGroup.GetCount() > 0)
					{
						BaseTileViewHolder baseViewHolder = tile.getParentViewHolder();
						if (baseViewHolder != null && baseViewHolder instanceof TileViewHolder)
						{
							final TileViewHolder viewHolder = (TileViewHolder) baseViewHolder;
							
							ServiceLocator.Current().GetInstance(MainActivity.class).runOnUiThread(
								new Runnable()
								{
									public void run()
									{
										viewHolder.UpdateTileNotification();
									}
								});
							
							return new SlideAnimation(appService.getMainContext());
						}
					}
				}
				
				return new FlipAnimation(appService.getMainContext());
			}
		}
	}
	
	class AnimationTask extends AsyncTask<Void, Void, Void>
	{
		private Timer timer;
		
		private ITileAnimation tileAnimation1;
		private ITileAnimation tileAnimation2;
		private ITileAnimation tileAnimation3;
		
		private void Start()
		{
			this.Stop();
			
			this.timer = new Timer();
			timer.schedule(new AnimationTimer(), 100, 5400);
		}
		
		private void Stop()
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
			{
				this.timer.purge();
				this.timer.cancel();
			}
			
			this.timer = null;
			
			this.cancel(true);
		}
		
		@Override
		protected Void doInBackground(Void... voids)
		{
			this.Start();
			
			return null;
		}
		
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(Void param)
		{
			super.onPostExecute(param);
		}
		
		@Override
		protected void onCancelled(Void param)
		{
			super.onCancelled(param);
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
						tileAnimation1 = GenerateTileAnimation(tile1, random.nextInt(AnimationTypes.length + 2));
						
						int rand2 = this.GenerateRandomIndex(random, tiles.size() - 1, rand1);
						final TileBase tile2 = tiles.get(rand2);
						tileAnimation2 = GenerateTileAnimation(tile2, random.nextInt(AnimationTypes.length + 2));
						
						int rand3 = this.GenerateRandomIndex(random, tiles.size() - 1, rand2);
						final TileBase tile3 = tiles.get(rand3);
						tileAnimation3 = GenerateTileAnimation(tile3, random.nextInt(AnimationTypes.length + 2));
						
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
			
			private Queue<Integer> lastUsingIndexes = new ConcurrentLinkedQueue<>();
			
			private int GenerateRandomIndex(Random random, int bound, int ignore)
			{
				int rand;
				
				do { rand = random.nextInt(bound); }
				while (rand == ignore || lastUsingIndexes.contains(rand));
				
				if (lastUsingIndexes.size() > 4)
				{
					lastUsingIndexes.poll();
				}
				
				lastUsingIndexes.add(rand);
				
				return rand;
			}
		}
	}
}