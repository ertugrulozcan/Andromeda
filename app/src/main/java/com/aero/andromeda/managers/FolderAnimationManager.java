package com.aero.andromeda.managers;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;
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
	public static FolderAnimationManager Current;
	
	private FolderAnimationManager()
	{
		Current = this;
	}
	
	public static FolderAnimationManager Init()
	{
		return new FolderAnimationManager();
	}
	
	private final long OPEN_THUMBNAIL_ANIMATION_DURATION = 400;
	private final long OPEN_THUMBNAIL_ANIMATION_DELAY = 120;
	private final long OPEN_TILE_ANIMATION_DURATION = 200;
	private final long OPEN_TILE_ANIMATION_DELAY = 200;
	private final long CLOSE_THUMBNAIL_ANIMATION_DURATION = 400;
	private final long CLOSE_THUMBNAIL_ANIMATION_DELAY = 120;
	private final long CLOSE_TILE_ANIMATION_DURATION = 350;
	private final long CLOSE_TILE_ANIMATION_DELAY = 200;
	private final float TILE_START_POSITION = -(SizeConverter.Current.GetTileWidth(TileBase.TileSize.Medium) + 10);
	
	
	public void OpenFolder(FolderTile folderTile, Folder folder, AnimatorListenerAdapter afterOpened)
	{
		AnimatorSet thumbnailsOpenAnimation = this.GenerateAnimationOpenFolderThumbnails(folderTile);
		AnimatorSet tilesOpenAnimation = this.GenerateAnimationOpenFolderTiles(folder);
		
		thumbnailsOpenAnimation.playTogether(tilesOpenAnimation);
		if (afterOpened != null)
			thumbnailsOpenAnimation.addListener(afterOpened);
		
		thumbnailsOpenAnimation.start();
	}
	
	public void CloseFolder(FolderTile folderTile, Folder folder, AnimatorListenerAdapter afterOpened)
	{
		AnimatorSet tilesCloseAnimation = this.GenerateAnimationCloseFolderTiles(folder);
		AnimatorSet thumbnailsCloseAnimation = this.GenerateAnimationCloseFolderThumbnails(folderTile);
		
		tilesCloseAnimation.playTogether(thumbnailsCloseAnimation);
		if (afterOpened != null)
			tilesCloseAnimation.addListener(afterOpened);
		
		tilesCloseAnimation.start();
	}
	
	private AnimatorSet GenerateAnimationOpenFolderThumbnails(FolderTile folderTile)
	{
		AnimatorSet animatorSet = new AnimatorSet();
		
		List<View> thumbnailViews = this.GetThumbnailList(folderTile);
		Collections.reverse(thumbnailViews);
		
		long delay = 0;
		for (View view : thumbnailViews)
		{
			ObjectAnimator animation = this.GenerateThumbnailDownAnimation(view);
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
		long delay = 0;
		for (View view : tileViews)
		{
			view.setTranslationY(TILE_START_POSITION);
			view.setAlpha(0.0f);
			
			ObjectAnimator animation = this.GenerateTileDownAnimation(view);
			animation.setStartDelay(delay);
			animatorSet.play(animation);
			animatorSet.playTogether(this.GenerateAlphaAnimation(view, false));
			delay += OPEN_TILE_ANIMATION_DELAY;
		}
		
		return animatorSet;
	}
	
	private ObjectAnimator GenerateThumbnailDownAnimation(View view)
	{
		float translationDistance = SizeConverter.Current.GetTileWidth(TileBase.TileSize.Medium) * 2 / 3;
		ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", translationDistance);
		animation.setDuration(OPEN_THUMBNAIL_ANIMATION_DURATION);
		animation.setInterpolator(new AnticipateInterpolator());
		return animation;
	}
	
	private ObjectAnimator GenerateTileDownAnimation(View view)
	{
		ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", TILE_START_POSITION, 0f);
		animation.setDuration(OPEN_TILE_ANIMATION_DURATION);
		animation.setInterpolator(new OvershootInterpolator());
		return animation;
	}
	
	private AnimatorSet GenerateAnimationCloseFolderThumbnails(FolderTile folderTile)
	{
		AnimatorSet animatorSet = new AnimatorSet();
		
		List<View> thumbnailViews = this.GetThumbnailList(folderTile);
		long delay = 0;
		for (View view : thumbnailViews)
		{
			ObjectAnimator animation = this.GenerateThumbnailUpAnimation(view);
			animation.setStartDelay(delay);
			animatorSet.play(animation);
			delay += CLOSE_THUMBNAIL_ANIMATION_DELAY;
		}
		
		return animatorSet;
	}
	
	private AnimatorSet GenerateAnimationCloseFolderTiles(Folder folder)
	{
		AnimatorSet animatorSet = new AnimatorSet();
		
		List<View> tileViews = this.GetTileViewList(folder);
		long delay = 0;
		for (View view : tileViews)
		{
			ObjectAnimator animation = this.GenerateTileUpAnimation(view);
			animation.setStartDelay(delay);
			animatorSet.play(animation);
			animatorSet.playTogether(this.GenerateAlphaAnimation(view, true));
			delay += CLOSE_TILE_ANIMATION_DELAY;
		}
		
		return animatorSet;
	}
	
	private ObjectAnimator GenerateThumbnailUpAnimation(View view)
	{
		ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", 0f);
		animation.setDuration(CLOSE_THUMBNAIL_ANIMATION_DURATION);
		animation.setInterpolator(new OvershootInterpolator());
		return animation;
	}
	
	private ObjectAnimator GenerateTileUpAnimation(View view)
	{
		ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", 0f, TILE_START_POSITION);
		animation.setDuration(CLOSE_TILE_ANIMATION_DURATION);
		animation.setInterpolator(new AnticipateInterpolator());
		return animation;
	}
	
	private ObjectAnimator GenerateAlphaAnimation(View view, boolean inverse)
	{
		float startValue = 0.0f;
		float endValue = 1.0f;
		long duration = OPEN_TILE_ANIMATION_DURATION * 2;
		
		if (inverse)
		{
			startValue = 1.0f;
			endValue = 0.0f;
			duration = CLOSE_TILE_ANIMATION_DURATION * 2;
		}
		
		ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", startValue, endValue);
		anim.setInterpolator(new TileOpacityInterpolator());
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
	
	class TileOpacityInterpolator implements TimeInterpolator
	{
		@Override
		public float getInterpolation(float t)
		{
			if (t == 1)
				return 1.0f;
			
			if (t < 0.677f)
				return 0.0f;
			else
				return t;
		}
	}
}
