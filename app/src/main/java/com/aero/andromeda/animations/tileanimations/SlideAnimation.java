package com.aero.andromeda.animations.tileanimations;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.aero.andromeda.R;
import com.aero.andromeda.helpers.SizeConverter;
import com.aero.andromeda.models.tiles.FakeTile;
import com.aero.andromeda.models.tiles.Folder;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.ui.BaseTileViewHolder;

public class SlideAnimation implements ITileAnimation
{
	private Context parentContext;
	
	private boolean isEnabled;
	private AnimatorSet tileSlideAnimation = null;
	
	public SlideAnimation(Context context)
	{
		this.parentContext = context;
	}
	
	@Override
	public boolean IsEnabled()
	{
		return this.isEnabled;
	}
	
	@Override
	public void Start(TileBase tile, int delay)
	{
		this.Load(tile);
		this.isEnabled = true;
		this.Animate(tile, delay);
	}
	
	@Override
	public void Start(TileBase tile)
	{
		this.Start(tile, 0);
	}
	
	@Override
	public void Stop()
	{
		this.Unload();
		this.isEnabled = false;
	}
	
	private void Load(TileBase tile)
	{
		final View tileView = tile.getParentViewHolder().getItemView();
		if (tileView == null)
			return;
		
		final FrameLayout secondTileView = tileView.findViewById(R.id.tileSecondViewLayout);
		if (secondTileView == null)
			return;
		
		ObjectAnimator upAnimation = ObjectAnimator.ofFloat(secondTileView, "translationY", SizeConverter.Current.GetTileHeight(tile.getTileSize()), 0);
		upAnimation.setDuration(300);
		upAnimation.setInterpolator(new android.view.animation.DecelerateInterpolator());
		
		ObjectAnimator downAnimation = ObjectAnimator.ofFloat(secondTileView, "translationY", 0, SizeConverter.Current.GetTileHeight(tile.getTileSize()));
		downAnimation.setDuration(300);
		downAnimation.setInterpolator(new android.view.animation.AccelerateInterpolator());
		downAnimation.setStartDelay(5000);
		
		this.tileSlideAnimation = new AnimatorSet();
		this.tileSlideAnimation.playSequentially(upAnimation);
		this.tileSlideAnimation.playSequentially(downAnimation);
	}
	
	private void Unload()
	{
		if (this.tileSlideAnimation != null)
		{
			this.tileSlideAnimation.end();
			this.tileSlideAnimation.setupStartValues();
			this.tileSlideAnimation.cancel();
		}
		
		this.tileSlideAnimation = null;
	}
	
	private void Animate(final TileBase tile, int delay)
	{
		if (tile == null)
			return;
		
		if (this.tileSlideAnimation == null)
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
		
		final FrameLayout secondTileView = tileView.findViewById(R.id.tileSecondViewLayout);
		
		if (secondTileView == null || this.parentContext == null || !(this.parentContext instanceof Activity))
			return;
		
		this.tileSlideAnimation.setStartDelay(delay);
		
		Activity activity = (Activity) this.parentContext;
		
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				synchronized (tile)
				{
					if (tileSlideAnimation == null || tileSlideAnimation.isRunning())
						return;
					
					tileSlideAnimation.setTarget(secondTileView);
					tileSlideAnimation.start();
				}
			}
		});
	}
}
