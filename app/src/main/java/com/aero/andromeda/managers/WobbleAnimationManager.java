package com.aero.andromeda.managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.aero.andromeda.R;
import com.aero.andromeda.adapters.TilesAdapter;
import com.aero.andromeda.models.tiles.FakeTile;
import com.aero.andromeda.models.tiles.Folder;
import com.aero.andromeda.models.tiles.FolderTile;
import com.aero.andromeda.models.tiles.TileBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static android.view.View.LAYER_TYPE_HARDWARE;
import static android.view.View.LAYER_TYPE_NONE;

public class WobbleAnimationManager extends AsyncTask<View, Void, View>
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
	
	private HashMap<View, ObjectAnimator> viewAnimatorDictionary = new HashMap<>();
	
	
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
	}
	
	@Override
	protected View doInBackground(View... params)
	{
		View view = null;
		if (params != null && params.length > 0)
		{
			view = params[0];
		}
		
		return view;
	}
	
	public void stop(View view)
	{
		if (view == null)
			this.stopWobble(true);
		else
			this.stopWobble(view);
		
		this.cancel(true);
	}
	
	@Override
	protected void onPostExecute(View view)
	{
		super.onPostExecute(view);
		
		if (view == null)
			this.startWobble();
		else
			this.startWobble(view);
	}
	
	@Override
	protected void onCancelled(View view)
	{
		super.onCancelled(view);
	}
	
	
	private int getChildCount()
	{
		return this.tilesAdapter.getTileViewList().size();
	}
	
	private View getChildAt(int index)
	{
		return this.tilesAdapter.getTileViewList().get(index);
	}
	
	private void startWobble(View view)
	{
		if (this.viewAnimatorDictionary.containsKey(view))
		{
			Animator wobbleAnimator = this.viewAnimatorDictionary.get(view);
			wobbleAnimator.start();
		}
		else
		{
			this.animateWobble(view);
		}
	}
	
	private void startWobble()
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
				continue;
			
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
	
	private void stopWobble(View view)
	{
		if (this.viewAnimatorDictionary.containsKey(view))
		{
			Animator wobbleAnimator = this.viewAnimatorDictionary.get(view);
			wobbleAnimator.cancel();
			view.setRotation(0);
		}
	}
	
	private void stopWobble(boolean resetRotation)
	{
		List<Animator> mWobbleAnimators = new ArrayList(this.viewAnimatorDictionary.values());
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
		startWobble();
	}
	
	private void animateWobble(View v)
	{
		if (!this.viewAnimatorDictionary.containsKey(v))
		{
			ObjectAnimator animator = createBaseWobble(v);
			animator.setFloatValues(-3, 3);
			v.setLayerType(LAYER_TYPE_HARDWARE, null);
			this.viewAnimatorDictionary.put(v, animator);
		}
		
		ObjectAnimator animator = this.viewAnimatorDictionary.get(v);
		animator.start();
	}
	
	private void animateWobbleInverse(View v)
	{
		if (!this.viewAnimatorDictionary.containsKey(v))
		{
			ObjectAnimator animator = createBaseWobble(v);
			animator.setFloatValues(3, -3);
			v.setLayerType(LAYER_TYPE_HARDWARE, null);
			this.viewAnimatorDictionary.put(v, animator);
		}
		
		ObjectAnimator animator = this.viewAnimatorDictionary.get(v);
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
