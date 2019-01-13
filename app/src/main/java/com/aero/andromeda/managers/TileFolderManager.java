package com.aero.andromeda.managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;

import com.aero.andromeda.MainActivity;
import com.aero.andromeda.adapters.TilesAdapter;
import com.aero.andromeda.animations.FolderAnimationManager;
import com.aero.andromeda.animations.FolderAnimationTask;
import com.aero.andromeda.models.tiles.FolderTile;
import com.aero.andromeda.models.tiles.Folder;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.services.interfaces.ISettingsService;
import com.aero.andromeda.settings.UISettings;
import com.aero.andromeda.ui.FolderViewHolder;

import java.util.ArrayList;
import java.util.List;

public class TileFolderManager
{
	private static TileFolderManager self = new TileFolderManager();
	public static TileFolderManager Current = self;
	
	private final FolderAnimationManager folderAnimationManager;
	
	private FolderViewHolder folderViewHolder;
	private TilesAdapter folderTilesAdapter;
	
	private FolderTile openedFolderTile;
	private Folder tileFolder;
	
	private boolean isFolderOpening = false;
	private boolean isFolderClosing = false;
	
	private Object openFolderLock = new Object();
	private Object closeFolderLock = new Object();
	
	private TileFolderManager()
	{
		this.folderAnimationManager = FolderAnimationManager.Init();
		this.tileFolder = new Folder();
		
		//Choreographer.getInstance().postFrameCallback();
	}
	
	public boolean IsFolderOpened()
	{
		return this.openedFolderTile != null;
	}
	
	public void BindFolderViewHolder(final FolderViewHolder folderViewHolder)
	{
		ISettingsService settingsService = ServiceLocator.Current().GetInstance(ISettingsService.class);
		UISettings uiSettings = settingsService.getUISettings();
		
		this.folderViewHolder = folderViewHolder;
		List<TileBase> folderSubTileList = new ArrayList<>();
		folderSubTileList.addAll(this.tileFolder.getSubTiles());
		this.folderTilesAdapter = new TilesAdapter(folderSubTileList, false, false);
		
		RecyclerView recyclerView = this.folderViewHolder.getRecyclerView();
		TilesLayoutManager spannedGridLayoutManager = new TilesLayoutManager(TilesLayoutManager.Orientation.VERTICAL, uiSettings.getLayoutWidth());
		spannedGridLayoutManager.setItemOrderIsStable(true);
		recyclerView.setLayoutManager(spannedGridLayoutManager);
		recyclerView.setAdapter(this.folderTilesAdapter);
		
		this.folderTilesAdapter.notifyDataSetChanged();
	}
	
	public void OnClickFolderTileAsync(FolderTile folderTile)
	{
		FolderAnimationTask task = new FolderAnimationTask(this);
		task.execute(folderTile);
	}
	
	public void OnClickFolderTile(FolderTile folderTile)
	{
		if (!this.IsFolderOpened())
		{
			this.OpenFolder(folderTile);
		}
		else
		{
			boolean aDifferentTile = this.openedFolderTile != folderTile;
			this.CloseFolder();
			
			if (aDifferentTile)
				this.OpenFolder(folderTile);
		}
	}
	
	synchronized private void OpenFolder(FolderTile folderTile)
	{
		synchronized (this.openFolderLock)
		{
			if (this.IsFolderOpened() || folderTile == null || this.isFolderOpening)
				return;
			
			this.isFolderOpening = true;
			
			try
			{
				TilesAdapter mainTilesAdapter = ServiceLocator.Current().GetInstance(IAppService.class).getTilesAdapter();
				
				this.openedFolderTile = folderTile;
				this.tileFolder.SetParentTile(this.openedFolderTile);
				
				int indexOfTile = mainTilesAdapter.getItemIndex(this.openedFolderTile);
				mainTilesAdapter.InsertTile(this.tileFolder, indexOfTile + 1);
				
				this.folderAnimationManager.OpenFolder(folderTile, this.tileFolder, new AnimatorListenerAdapter()
				{
					@Override
					public void onAnimationEnd(Animator animation)
					{
						super.onAnimationEnd(animation);
						
						synchronized (TileFolderManager.this.openFolderLock)
						{
							TileFolderManager.this.isFolderOpening = false;
						}
					}
				});
			}
			catch (Exception ex)
			{
				this.isFolderOpening = false;
			}
		}
	}
	
	synchronized public void CloseFolder()
	{
		synchronized (this.closeFolderLock)
		{
			if (!this.IsFolderOpened() || this.isFolderClosing)
				return;
			
			this.isFolderClosing = true;
			
			try
			{
				if (!this.IsFolderOpened())
					return;
				
				// Start closing animation
				this.folderAnimationManager.CloseFolder(this.openedFolderTile, this.tileFolder, new AnimatorListenerAdapter()
				{
					@Override
					public void onAnimationEnd(Animator animation)
					{
						super.onAnimationEnd(animation);
						
						TileFolderManager.this.tileFolder.SetParentTile(null);
						TileFolderManager.this.openedFolderTile = null;
						
						synchronized (TileFolderManager.this.closeFolderLock)
						{
							TileFolderManager.this.isFolderClosing = false;
						}
					}
				});
				
				// Remove closed tile
				this.RemoveTileOnBackground(this.tileFolder);
			}
			catch (Exception ex)
			{
				this.isFolderClosing = false;
				ex.printStackTrace();
			}
		}
	}
	
	private void RemoveTileOnBackground(final Folder tileFolder)
	{
		synchronized (tileFolder)
		{
			final IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
			final MainActivity mainActivity = (MainActivity)appService.getMainContext();
			final TilesAdapter mainTilesAdapter = appService.getTilesAdapter();
			
			final long delay = FolderAnimationManager.CLOSE_TILE_ANIMATION_DURATION - 50;
			
			Thread thread = new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						sleep(delay);
						
						mainActivity.runOnUiThread(
								new Runnable()
								{
									@Override
									public void run()
									{
										int indexOfTile = mainTilesAdapter.getItemIndex(tileFolder);
										mainTilesAdapter.RemoveTile(indexOfTile);
									}
								}
						);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			};
			
			thread.start();
		}
	}
}
