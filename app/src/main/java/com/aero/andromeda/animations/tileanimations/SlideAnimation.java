package com.aero.andromeda.animations.tileanimations;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.aero.andromeda.R;
import com.aero.andromeda.animations.TileAnimationManager;
import com.aero.andromeda.helpers.SizeConverter;
import com.aero.andromeda.models.tiles.FakeTile;
import com.aero.andromeda.models.tiles.Folder;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.ui.BaseTileViewHolder;
import com.aero.andromeda.ui.TileViewHolder;

public class SlideAnimation extends TileAnimationBase
{
    private final static int Duration = 10000;

    public SlideAnimation(Context context, TileAnimationManager tileAnimationManager)
	{
	    super(context, tileAnimationManager);
	}

	@Override
	protected void Load(TileBase tile)
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
		downAnimation.setStartDelay(Duration);
		
		this.setAnimator(new AnimatorSet());
		this.getAnimator().playSequentially(upAnimation);
		this.getAnimator().playSequentially(downAnimation);
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
		
		final FrameLayout secondTileView = tileView.findViewById(R.id.tileSecondViewLayout);
		
		if (secondTileView == null || this.getContext() == null || !(this.getContext() instanceof Activity))
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

                    getAnimator().setTarget(secondTileView);
                    getAnimator().start();
				}
			}
		});
	}
}
