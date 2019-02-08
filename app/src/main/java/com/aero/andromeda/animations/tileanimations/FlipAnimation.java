package com.aero.andromeda.animations.tileanimations;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.aero.andromeda.R;
import com.aero.andromeda.animations.TileAnimationManager;
import com.aero.andromeda.helpers.SizeConverter;
import com.aero.andromeda.models.tiles.FakeTile;
import com.aero.andromeda.models.tiles.Folder;
import com.aero.andromeda.models.tiles.FolderTile;
import com.aero.andromeda.models.tiles.Tile;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.ui.BaseTileViewHolder;
import com.aero.andromeda.ui.FolderTileViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

public class FlipAnimation extends TileAnimationBase
{
	private TileFlipInterpolator tileFlipInterpolator;
	
	public FlipAnimation(Context context, TileAnimationManager tileAnimationManager)
	{
        super(context, tileAnimationManager);
		this.tileFlipInterpolator = new TileFlipInterpolator();
	}
	

	@SuppressLint("ResourceType")
    @Override
    protected void Load(final TileBase tile)
	{
		this.setAnimator((AnimatorSet) AnimatorInflater.loadAnimator(this.getContext(), R.anim.tile_flip));
	}

    @Override
    protected void Unload()
	{
		if (this.getAnimator() != null)
		{
			this.getAnimator().end();
			this.getAnimator().setupStartValues();
			this.getAnimator().cancel();
		}
		
		this.setAnimator(null);
	}

    @Override
	protected void Animate(final TileBase tile, int delay)
	{
		if (tile == null)
			return;
		
		if (this.getAnimator() == null)
			return;
		
		if (tile.getTileType() == TileBase.TileType.FolderTile ||
			tile.getTileType() == TileBase.TileType.TilesHeader ||
			tile.getTileType() == TileBase.TileType.TilesFooter ||
			tile instanceof Folder ||
			tile instanceof FakeTile)
			return;
		
		BaseTileViewHolder tileViewHolder = tile.getParentViewHolder();
		if (tileViewHolder == null)
			return;
		
		final View tileView = tile.getParentViewHolder().getItemView();
		if (tileView == null)
			return;
		
		if (this.getContext() == null || !(this.getContext() instanceof Activity))
			return;
		
		this.getAnimator().setStartDelay(delay);
		
		Activity activity = (Activity) this.getContext();
		
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				synchronized (tile)
				{
					if (getAnimator() == null || getAnimator().isRunning())
						return;

                    getAnimator().setInterpolator(tileFlipInterpolator);
                    getAnimator().setTarget(tileView);
                    getAnimator().start();
				}
			}
		});
	}
	
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
