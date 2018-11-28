package com.aero.andromeda.managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;

import com.aero.andromeda.R;
import com.aero.andromeda.adapters.TilesAdapter;
import com.aero.andromeda.models.tiles.FakeTile;
import com.aero.andromeda.models.tiles.Folder;
import com.aero.andromeda.models.tiles.FolderTile;
import com.aero.andromeda.models.tiles.TileBase;

import java.util.LinkedList;
import java.util.List;

import static android.view.View.LAYER_TYPE_NONE;

public class WobbleAnimationManager
{
	private TilesAdapter tilesAdapter;
	
	private static WobbleAnimationManager self;
	
	public static WobbleAnimationManager Current(TilesAdapter tilesAdapter)
	{
		if (self == null)
			self = new WobbleAnimationManager(tilesAdapter);
		
		return self;
	}
	
	private WobbleAnimationManager(TilesAdapter tilesAdapter)
	{
		this.tilesAdapter = tilesAdapter;
	}
	
	private List<ObjectAnimator> mWobbleAnimators = new LinkedList<ObjectAnimator>();
	
	private int getChildCount()
	{
		return this.tilesAdapter.getTileViewList().size();
	}
	
	private View getChildAt(int index)
	{
		return this.tilesAdapter.getTileViewList().get(index);
	}
	
	public void startWobbleAnimation()
	{
		for (int i = 0; i < getChildCount(); i++)
		{
			TileBase tile = this.tilesAdapter.getItem(i);
			
			if (tile == null ||
				tile instanceof Folder ||
				tile instanceof FolderTile ||
				tile instanceof FakeTile ||
				tile.getTileType() == TileBase.TileType.TilesHeader ||
				tile.getTileType() == TileBase.TileType.TilesFooter ||
				tile.getTileType() == TileBase.TileType.FolderTile ||
				tile.getTileType() == TileBase.TileType.Folder)
				return;
			
			View v = getChildAt(i);
			if (v != null)
			{
				if (i % 2 == 0)
					animateWobble(v);
				else
					animateWobbleInverse(v);
			}
		}
	}
	
	public void stopWobble(boolean resetRotation)
	{
		for (Animator wobbleAnimator : mWobbleAnimators)
		{
			wobbleAnimator.cancel();
		}
		
		mWobbleAnimators.clear();
		for (int i = 0; i < getChildCount(); i++)
		{
			View v = getChildAt(i);
			
			if (v != null)
			{
				if (resetRotation)
					v.setRotation(0);
			}
		}
	}
	
	private void restartWobble()
	{
		stopWobble(false);
		startWobbleAnimation();
	}
	
	private void animateWobble(View v)
	{
		ObjectAnimator animator = createBaseWobble(v);
		animator.setFloatValues(-2, 2);
		
		mWobbleAnimators.add(animator);
		animator.start();
	}
	
	private void animateWobbleInverse(View v)
	{
		ObjectAnimator animator = createBaseWobble(v);
		animator.setFloatValues(2, -2);
		
		mWobbleAnimators.add(animator);
		animator.start();
	}
	
	private ObjectAnimator createBaseWobble(final View v)
	{
		ObjectAnimator animator = new ObjectAnimator();
		animator.setDuration(180);
		animator.setRepeatMode(ValueAnimator.REVERSE);
		animator.setRepeatCount(ValueAnimator.INFINITE);
		animator.setPropertyName("rotation");
		animator.setTarget(v);
		animator.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				v.setLayerType(LAYER_TYPE_NONE, null);
			}
		});
		
		return animator;
	}
}
