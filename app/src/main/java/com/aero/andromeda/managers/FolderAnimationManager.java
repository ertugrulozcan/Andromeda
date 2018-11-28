package com.aero.andromeda.managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.View;

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
	private Context parentContext;
	
	private int tileDelaySpan = 100;
	private int folderDelaySpan = 120;
	private int animTime;
	
	private Folder openingFolder = null;
	private Folder closingFolder = null;
	private int openFolderAnimationCounter = 0;
	private int closeFolderAnimationCounter = 0;
	
	private AnimatorListenerAdapter openFolderAnimationListener;
	private AnimatorListenerAdapter closeFolderAnimationListener;
	
	private List<FolderAnimationListener> listeners = new ArrayList<>();
	
	public FolderAnimationManager(Context context)
	{
		this.parentContext = context;
		this.setEventListeners();
	}
	
	private void setEventListeners()
	{
		this.openFolderAnimationListener = new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				openFolderAnimationCounter++;
				if (openingFolder != null)
				{
					if (openFolderAnimationCounter == openingFolder.getSubTiles().size() - 1)
					{
						for (FolderAnimationListener hl : listeners)
							hl.OpenFolderAnimationEnded();
					}
				}
				else
				{
					for (FolderAnimationListener hl : listeners)
						hl.OpenFolderAnimationEnded();
				}
			}
		};
		
		this.closeFolderAnimationListener = new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				closeFolderAnimationCounter++;
				if (closingFolder != null)
				{
					if (closeFolderAnimationCounter > closingFolder.getSubTiles().size() - 2)
					{
						for (FolderAnimationListener hl : listeners)
							hl.CloseFolderAnimationEnded();
					}
				}
				else
				{
					for (FolderAnimationListener hl : listeners)
						hl.CloseFolderAnimationEnded();
				}
			}
		};
	}
	
	public void addAnimationListener(FolderAnimationListener listener)
	{
		this.listeners.add(listener);
	}
	
	public void AnimateOpenFolder(FolderTile folderTile)
	{
		BaseTileViewHolder viewHolderBase = folderTile.getParentViewHolder();
		if (viewHolderBase instanceof FolderTileViewHolder)
		{
			FolderTileViewHolder folderTileViewHolder = (FolderTileViewHolder)viewHolderBase;
			List<View> subTileViews = new ArrayList<>(folderTileViewHolder.getSubTileViewList());
			Collections.reverse(subTileViews);
			
			int maxThumbnailCountOnRow = this.getTileCountOnSingleRow(folderTile);
			this.animTime = tileDelaySpan;// subTileViews.size() * this.tileDelaySpan;
			
			long delay = 0;
			for (int i = 0; i < subTileViews.size(); i++)
			{
				View view = subTileViews.get(i);
				int rowNo = (subTileViews.size() - i - 1) / maxThumbnailCountOnRow;
				delay += tileDelaySpan;
				this.AnimateOpenFolder(view, rowNo, delay);
			}
		}
	}
	
	private void AnimateOpenFolder(final View tileView, final int rowNo, final long delay)
	{
		if (tileView == null)
			return;
		
		if (this.parentContext == null || !(this.parentContext instanceof Activity))
			return;
		
		final int thumbnailSize = (SizeConverter.Current.GetTileWidth(TileBase.TileSize.Medium) - 2) / 3;
		
		Activity activity = (Activity) this.parentContext;
		
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				synchronized (tileView)
				{
					ObjectAnimator animation = GenerateTranslateAnimation(tileView, thumbnailSize * rowNo, 312f + thumbnailSize * rowNo, delay);
					animation.start();
				}
			}
		});
	}
	
	public void AnimateOpenFolder(Folder folder)
	{
		if (folder == null)
		return;
		
		this.openingFolder = folder;
		this.openFolderAnimationCounter = 0;
		
		long delay = 0;
		for (final TileBase subTile : folder.getSubTiles())
		{
			BaseTileViewHolder viewHolder = subTile.getParentViewHolder();
			final View view = viewHolder.getItemView();
			
			Activity activity = (Activity) this.parentContext;
			
			delay += folderDelaySpan;
			final long delayFinal = delay;
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					ObjectAnimator animation = GenerateTranslateAnimation(view, -400f, 0, delayFinal);
					animation.addListener(openFolderAnimationListener);
					animation.start();
				}
			});
		}
	}
	
	public void AnimateCloseFolder(FolderTile folderTile)
	{
		BaseTileViewHolder viewHolderBase = folderTile.getParentViewHolder();
		if (viewHolderBase instanceof FolderTileViewHolder)
		{
			FolderTileViewHolder folderTileViewHolder = (FolderTileViewHolder)viewHolderBase;
			List<View> subTileViews = folderTileViewHolder.getSubTileViewList();
			
			int maxThumbnailCountOnRow = this.getTileCountOnSingleRow(folderTile);
			
			long delay = 0;
			for (int i = 0; i < subTileViews.size(); i++)
			{
				View view = subTileViews.get(i);
				int rowNo = i / maxThumbnailCountOnRow;
				this.AnimateCloseFolder(view, rowNo, delay);
				delay += tileDelaySpan;
			}
		}
	}
	
	private void AnimateCloseFolder(final View tileView, final int rowNo, final long delay)
	{
		if (tileView == null)
			return;
		
		if (this.parentContext == null || !(this.parentContext instanceof Activity))
			return;
		
		final int thumbnailSize = (SizeConverter.Current.GetTileWidth(TileBase.TileSize.Medium) - 2) / 3;
		
		Activity activity = (Activity) this.parentContext;
		
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				synchronized (tileView)
				{
					ObjectAnimator animation = GenerateTranslateAnimation(tileView, 312f + thumbnailSize, thumbnailSize * rowNo, delay);
					animation.start();
				}
			}
		});
	}
	
	public void AnimateCloseFolder(Folder folder)
	{
		if (folder == null)
			return;
		
		this.closingFolder = folder;
		this.closeFolderAnimationCounter = 0;
		
		long delay = 0;
		List<Tile> subTiles = new ArrayList<>(folder.getSubTiles());
		Collections.reverse(subTiles);
		
		for (final Tile subTile : subTiles)
		{
			BaseTileViewHolder viewHolder = subTile.getParentViewHolder();
			final View view = viewHolder.getItemView();
			
			Activity activity = (Activity) this.parentContext;
			
			final long delayFinal = delay;
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					synchronized (subTile)
					{
						ObjectAnimator animation = GenerateTranslateAnimation(view, 0f, -400f, delayFinal);
						animation.addListener(closeFolderAnimationListener);
						animation.start();
					}
				}
			});
			
			delay += folderDelaySpan;
		}
	}
	
	private ObjectAnimator GenerateTranslateAnimation(View view, float fromY, float toY, long delay)
	{
		ObjectAnimator anim = ObjectAnimator.ofFloat(view, "y", fromY, toY);
		anim.setDuration(this.animTime);
		anim.setStartDelay(delay);
		//anim.start();
		
		return anim;
	}
	
	private int getTileCountOnSingleRow(FolderTile folderTile)
	{
		if (folderTile.getTileSize() == TileBase.TileSize.Small)
			return 1;
		if (folderTile.getTileSize() == TileBase.TileSize.Medium)
			return 3;
		if (folderTile.getTileSize() == TileBase.TileSize.MediumWide)
			return 6;
		if (folderTile.getTileSize() == TileBase.TileSize.Large)
			return 6;
		
		return 3;
	}
	
	public interface FolderAnimationListener
	{
		void OpenFolderAnimationEnded();
		void CloseFolderAnimationEnded();
	}
}
