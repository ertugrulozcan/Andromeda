package com.aero.andromeda.animations;

import android.animation.Animator;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TileAnimationManager implements Animator.AnimatorListener
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

    private HashMap<Animator, TileBase> tileAnimatorDictionary;

    private Object lock = new Object();

	private TileAnimationManager()
	{
		this.appService = ServiceLocator.Current().GetInstance(IAppService.class);
		this.tileAnimatorDictionary = new LinkedHashMap<>();
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

    public void ImmediatelySlideAnimation(TileBase tile)
    {
        if (tile == null)
            return;

        synchronized (lock)
        {
            if (tileAnimatorDictionary.containsValue(tile))
                return;
        }

        SlideAnimation slideAnimation = this.GenerateSlideAnimation(tile);
        if (slideAnimation != null)
            slideAnimation.Start(tile);
    }

    public void ImmediatelyFlipAnimation(TileBase tile)
    {
        if (tile == null)
            return;

        synchronized (lock)
        {
            if (tileAnimatorDictionary.containsValue(tile))
                return;
        }

        FlipAnimation flipAnimation = this.GenerateFlipAnimation();
        if (flipAnimation != null)
            flipAnimation.Start(tile);
    }

    public FlipAnimation GenerateFlipAnimation()
    {
        return new FlipAnimation(appService.getMainContext(), this);
    }

    public SlideAnimation GenerateSlideAnimation(TileBase tile)
    {
        if (tile != null && tile.getTileSize() != TileBase.TileSize.Small)
        {
            INotificationService notificationService = ServiceLocator.Current().GetInstance(INotificationService.class);
            NotificationGroup notificationGroup = notificationService.GetNotificationGroup(tile);
            if (notificationGroup != null && notificationGroup.GetCount() > 0)
            {
                final BaseTileViewHolder baseViewHolder = tile.getParentViewHolder();
                if (baseViewHolder != null && baseViewHolder instanceof TileViewHolder)
                {
                    Activity activity = (Activity) appService.getMainContext();
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ((TileViewHolder) baseViewHolder).UpdateTileNotification();
                        }
                    });

                    return new SlideAnimation(appService.getMainContext(), this);
                }
            }
        }

        return null;
    }

	private ITileAnimation GenerateRandomTileAnimation(TileBase tile, Random random)
	{
		int no = random.nextInt(100);
		if (0 <= no && no <= 20)
        {
            return this.GenerateFlipAnimation();
        }
        else if (20 < no && no <= 100)
        {
            SlideAnimation slideAnimation = this.GenerateSlideAnimation(tile);
            if (slideAnimation != null)
                return slideAnimation;
        }

        return this.GenerateFlipAnimation();
	}

	public void addTileAnimatorRelation(TileBase tile, Animator tileAnimation)
    {
        synchronized (this.lock)
        {
            if (!this.tileAnimatorDictionary.containsKey(tileAnimation))
                this.tileAnimatorDictionary.put(tileAnimation, tile);
        }
    }

    public void removeTileAnimatorRelation(Animator tileAnimation)
    {
        synchronized (this.lock)
        {
            if (this.tileAnimatorDictionary.containsKey(tileAnimation))
                this.tileAnimatorDictionary.remove(tileAnimation);
        }
    }

    @Override
    public void onAnimationStart(Animator animation)
    {

    }

    @Override
    public void onAnimationEnd(Animator animation)
    {
        this.removeTileAnimatorRelation(animation);
    }

    @Override
    public void onAnimationCancel(Animator animation)
    {
        this.removeTileAnimatorRelation(animation);
    }

    @Override
    public void onAnimationRepeat(Animator animation)
    {

    }

    class AnimationTask extends AsyncTask<Void, Void, Void>
	{
		private Timer timer;

		private final int syncAnimationCount = 3;
		
		private ITileAnimation[] animationArray = new ITileAnimation[syncAnimationCount];
		
		private void Start()
		{
			this.Stop();
			
			this.timer = new Timer();
			timer.schedule(new AnimationTimer(), 100, 5400);
		}
		
		private void Stop()
		{
		    for (int i = 0; i < syncAnimationCount; i++)
            {
                if (animationArray[i] != null)
                    animationArray[i].Stop();

                animationArray[i] = null;
            }
			
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
                        int rand = -1;

                        for (int i = 0; i < syncAnimationCount; i++)
                        {
                            rand = this.GenerateRandomIndex(random, tiles.size() - 1, rand);
                            final TileBase tile = tiles.get(rand);

                            synchronized (lock)
                            {
                                if (tileAnimatorDictionary.containsValue(tile))
                                    continue;
                            }

                            animationArray[i] = GenerateRandomTileAnimation(tile, random);
                            animationArray[i].Start(tile, i * 1500);
                        }
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