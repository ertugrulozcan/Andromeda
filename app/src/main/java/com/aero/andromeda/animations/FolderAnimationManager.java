package com.aero.andromeda.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.aero.andromeda.helpers.SizeConverter;
import com.aero.andromeda.models.tiles.Folder;
import com.aero.andromeda.models.tiles.FolderTile;
import com.aero.andromeda.models.tiles.Tile;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.ui.BaseTileViewHolder;
import com.aero.andromeda.ui.FolderTileViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FolderAnimationManager
{
	public static com.aero.andromeda.animations.FolderAnimationManager Current;
	
	private FolderAnimationManager()
	{
		Current = this;
	}
	
	public static com.aero.andromeda.animations.FolderAnimationManager Init()
	{
		return new com.aero.andromeda.animations.FolderAnimationManager();
	}
	
	public static final long OPEN_THUMBNAIL_ANIMATION_DELAY = 90;
	public static final long OPEN_THUMBNAIL_ANIMATION_DURATION = 300;
	public static final long OPEN_TILE_ANIMATION_DELAY = 100;
	public static final long OPEN_TILE_ANIMATION_DURATION = 300;
	
	public static final long CLOSE_THUMBNAIL_ANIMATION_DELAY = 90;
	public static final long CLOSE_THUMBNAIL_ANIMATION_DURATION = 300;
	public static final long CLOSE_TILE_ANIMATION_DELAY = 100;
	public static final long CLOSE_TILE_ANIMATION_DURATION = 300;
	
	private final float TILE_START_POSITION = -(SizeConverter.Current.GetTileWidth(TileBase.TileSize.Medium) + 10);
	
	
	public void OpenFolder(final FolderTile folderTile, final Folder folder, AnimatorListenerAdapter afterOpened)
	{
		final AnimatorSet thumbnailsOpenAnimation = this.GenerateAnimationOpenFolderThumbnails(folderTile);
		final AnimatorSet tilesOpenAnimation = this.GenerateAnimationOpenFolderTiles(folder);
		
		if (thumbnailsOpenAnimation == null || tilesOpenAnimation == null)
		{
			if (afterOpened != null)
			{
				if (thumbnailsOpenAnimation != null)
					afterOpened.onAnimationEnd(thumbnailsOpenAnimation);
				
				if (tilesOpenAnimation != null)
					afterOpened.onAnimationEnd(tilesOpenAnimation);
			}
			
			return;
		}
		
		thumbnailsOpenAnimation.playSequentially(tilesOpenAnimation);
		if (afterOpened != null)
			thumbnailsOpenAnimation.addListener(afterOpened);
		
		thumbnailsOpenAnimation.addListener(this.GenerateHardwareLayerCleanerAdaptor(folderTile, folder));
		
		thumbnailsOpenAnimation.start();
	}
	
	public void CloseFolder(final FolderTile folderTile, final Folder folder, AnimatorListenerAdapter afterClosed)
	{
		final AnimatorSet tilesCloseAnimation = this.GenerateAnimationCloseFolderTiles(folder);
		final AnimatorSet thumbnailsCloseAnimation = this.GenerateAnimationCloseFolderThumbnails(folderTile);
		
		if (tilesCloseAnimation == null || thumbnailsCloseAnimation == null)
		{
			if (afterClosed != null)
			{
				if (tilesCloseAnimation != null)
					afterClosed.onAnimationEnd(tilesCloseAnimation);
				
				if (thumbnailsCloseAnimation != null)
					afterClosed.onAnimationEnd(thumbnailsCloseAnimation);
			}
			
			return;
		}
		
		if (afterClosed != null)
			tilesCloseAnimation.addListener(afterClosed);
		
		tilesCloseAnimation.addListener(this.GenerateHardwareLayerCleanerAdaptor(folderTile, folder));
		
		tilesCloseAnimation.playSequentially(thumbnailsCloseAnimation);
		tilesCloseAnimation.start();
	}
	
	private AnimatorSet GenerateAnimationOpenFolderThumbnails(FolderTile folderTile)
	{
		AnimatorSet animatorSet = new AnimatorSet();
		
		List<View> thumbnailViews = this.GetThumbnailList(folderTile);
		if (thumbnailViews.size() == 0)
			return null;
		
		Collections.reverse(thumbnailViews);
		
		long delay = 50;
		
		for (View view : thumbnailViews)
		{
			ObjectAnimator animation = this.GenerateThumbnailDownAnimation(view, OPEN_THUMBNAIL_ANIMATION_DURATION);
			//view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			animation.setStartDelay(delay);
			animatorSet.play(animation);
			delay += OPEN_THUMBNAIL_ANIMATION_DELAY;
		}
		
		return animatorSet;
	}
	
	private AnimatorSet GenerateAnimationOpenFolderTiles(Folder folder)
	{
		AnimatorSet animatorSet = new AnimatorSet();
		
		List<View> tileViews = this.GetTileViewList(folder);
		if (tileViews.size() == 0)
			return null;
		
		long delay = 300;
		
		for (View view : tileViews)
		{
			//view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			view.setTranslationY(TILE_START_POSITION);
			view.setAlpha(0.0f);
			
			ObjectAnimator animation = this.GenerateTileDownAnimation(view, OPEN_TILE_ANIMATION_DURATION);
			delay += OPEN_TILE_ANIMATION_DELAY;
			animation.setStartDelay(delay);
			animatorSet.play(animation);
			animatorSet.playTogether(this.GenerateAlphaAnimation(view, OPEN_TILE_ANIMATION_DURATION, false));
		}
		
		return animatorSet;
	}
	
	private ObjectAnimator GenerateThumbnailDownAnimation(View view, final long duration)
	{
		float translationDistance = SizeConverter.Current.GetTileWidth(TileBase.TileSize.Medium) * 2 / 3;
		ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", translationDistance);
		animation.setDuration(duration);
		animation.setInterpolator(new AnticipateInterpolator());
		return animation;
	}
	
	private ObjectAnimator GenerateTileDownAnimation(View view, final long duration)
	{
		ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", TILE_START_POSITION, 0f);
		animation.setDuration(duration);
		animation.setInterpolator(new OvershootInterpolator());
		return animation;
	}
	
	private AnimatorSet GenerateAnimationCloseFolderThumbnails(FolderTile folderTile)
	{
		AnimatorSet animatorSet = new AnimatorSet();
		
		List<View> thumbnailViews = this.GetThumbnailList(folderTile);
		if (thumbnailViews.size() == 0)
			return null;
		
		long delay = 150;
		
		for (View view : thumbnailViews)
		{
			ObjectAnimator animation = this.GenerateThumbnailUpAnimation(view, CLOSE_THUMBNAIL_ANIMATION_DURATION);
			//view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			delay += CLOSE_THUMBNAIL_ANIMATION_DELAY;
			animation.setStartDelay(delay);
			animatorSet.play(animation);
		}
		
		return animatorSet;
	}
	
	private AnimatorSet GenerateAnimationCloseFolderTiles(Folder folder)
	{
		AnimatorSet animatorSet = new AnimatorSet();
		
		List<View> tileViews = this.GetTileViewList(folder);
		if (tileViews.size() == 0)
			return null;
		
		long delay = 0;
		
		for (View view : tileViews)
		{
			ObjectAnimator animation = this.GenerateTileUpAnimation(view, CLOSE_TILE_ANIMATION_DURATION);
			//view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			animation.setStartDelay(delay);
			
			ObjectAnimator alphaAnimation = this.GenerateAlphaAnimation(view, CLOSE_TILE_ANIMATION_DURATION, true);
			alphaAnimation.setStartDelay(delay);
			animatorSet.playTogether(alphaAnimation);
			
			animatorSet.play(animation);
			delay += CLOSE_TILE_ANIMATION_DELAY;
		}
		
		return animatorSet;
	}
	
	private ObjectAnimator GenerateThumbnailUpAnimation(View view, final long duration)
	{
		ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", 0f);
		animation.setDuration(duration);
		animation.setInterpolator(new OvershootInterpolator());
		return animation;
	}
	
	private ObjectAnimator GenerateTileUpAnimation(View view, final long duration)
	{
		ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", 0f, TILE_START_POSITION);
		animation.setDuration(duration);
		animation.setInterpolator(new AnticipateInterpolator());
		return animation;
	}
	
	private ObjectAnimator GenerateAlphaAnimation(View view, final long duration, boolean inverse)
	{
		float startValue = 0.0f;
		float endValue = 1.0f;
		
		if (inverse)
		{
			startValue = 1.0f;
			endValue = 0.0f;
		}
		
		ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", startValue, endValue);
		anim.setInterpolator(new com.aero.andromeda.animations.FolderAnimationManager.TileOpacityInterpolator(inverse));
		anim.setDuration(duration);
		
		return anim;
	}
	
	private List<View> GetThumbnailList(FolderTile folderTile)
	{
		if (folderTile == null)
			return null;
		
		BaseTileViewHolder viewHolderBase = folderTile.getParentViewHolder();
		if (viewHolderBase instanceof FolderTileViewHolder)
		{
			FolderTileViewHolder folderTileViewHolder = (FolderTileViewHolder)viewHolderBase;
			List<View> subTileViews = folderTileViewHolder.getSubTileViewList();
			
			return subTileViews;
		}
		
		return new ArrayList<>();
	}
	
	private List<View> GetTileViewList(Folder folder)
	{
		if (folder == null)
			return null;
		
		List<View> tileViewList = new ArrayList<>();
		
		List<Tile> subTiles = folder.getSubTiles();
		for (TileBase subTile : subTiles)
		{
			BaseTileViewHolder viewHolder = subTile.getParentViewHolder();
			if (viewHolder != null)
			{
				View view = viewHolder.getItemView();
				if (view != null)
				{
					tileViewList.add(view);
				}
			}
		}
		
		return tileViewList;
	}
	
	private AnimatorListenerAdapter GenerateHardwareLayerCleanerAdaptor(final FolderTile folderTile, final Folder folder)
	{
		AnimatorListenerAdapter hardwareLayerCleaningAdapter =  new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				super.onAnimationEnd(animation);
				
				//com.aero.andromeda.animations.FolderAnimationManager.this.ClearHardwareLayerFrames(folderTile);
				//com.aero.andromeda.animations.FolderAnimationManager.this.ClearHardwareLayerFrames(folder);
			}
		};
		
		return hardwareLayerCleaningAdapter;
	}
	
	private void ClearHardwareLayerFrames(FolderTile folderTile)
	{
		List<View> thumbnailViews = this.GetThumbnailList(folderTile);
		for (View view : thumbnailViews)
		{
			view.setLayerType(View.LAYER_TYPE_NONE, null);
		}
	}
	
	private void ClearHardwareLayerFrames(Folder folder)
	{
		List<View> tileViews = this.GetTileViewList(folder);
		for (View view : tileViews)
		{
			view.setLayerType(View.LAYER_TYPE_NONE, null);
		}
	}
	
	class TileOpacityInterpolator implements TimeInterpolator
	{
		private boolean inverse;
		
		public TileOpacityInterpolator(boolean inverse)
		{
			this.inverse = inverse;
		}
		
		@Override
		public float getInterpolation(float t)
		{
			if (!inverse)
			{
				if (t > 0.5f)
					return 1.0f;
				else
					return t * 2;
			}
			else
			{
				if (t <= 0.99f)
					return 0.0f;
				else
					return 1.0f;
				/*
				if (t <= 0.9f)
					return 0.0f;
				else
					return (t - 0.9f) * 10;
				*/
			}
		}
	}
}