package com.aero.andromeda.managers;

import android.support.v7.widget.RecyclerView;

import com.aero.andromeda.AppDrawerFragment;
import com.aero.andromeda.adapters.TilesAdapter;
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
	
	private FolderViewHolder folderViewHolder;
	private TilesAdapter folderTilesAdapter;
	
	private FolderTile openedFolderTile;
	private Folder tileFolder;
	
	private TileFolderManager()
	{
		this.tileFolder = new Folder();
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
		
		FolderAnimationManager folderAnimationManager = ServiceLocator.Current().GetInstance(FolderAnimationManager.class);
		folderAnimationManager.addAnimationListener(new FolderAnimationManager.FolderAnimationListener()
		{
			@Override
			public void OpenFolderAnimationEnded()
			{
				OnOpenFolderAnimationEnded();
			}
			
			@Override
			public void CloseFolderAnimationEnded()
			{
				OnCloseFolderAnimationEnded();
			}
		});
	}
	
	public void OnClickFolderTile(FolderTile folderTile)
	{
		FolderAnimationManager folderAnimationManager = ServiceLocator.Current().GetInstance(FolderAnimationManager.class);
		if (folderAnimationManager.isAnimatedNow())
			return;
		
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
		
		/*
		if (this.folderTilesAdapter != null)
		{
			synchronized (this.folderTilesAdapter)
			{
				this.folderTilesAdapter.notifyAll();
			}
		}
		*/
	}
	
	synchronized private void OpenFolder(FolderTile folderTile)
	{
		if (folderTile == null)
			return;
		
		try
		{
			TilesAdapter mainTilesAdapter = ServiceLocator.Current().GetInstance(IAppService.class).getTilesAdapter();
			
			this.openedFolderTile = folderTile;
			this.tileFolder.SetParentTile(this.openedFolderTile);
			
			int indexOfTile = mainTilesAdapter.getItemIndex(this.openedFolderTile);
			mainTilesAdapter.InsertTile(this.tileFolder, indexOfTile + 1);
			
			this.OnFolderOpening(folderTile);
			this.OnFolderOpened(folderTile);
			
			//this.mainTilesAdapter.ScrollToItem(indexOfTile);
		}
		catch (Exception ex)
		{
		
		}
	}
	
	private void OnFolderOpening(FolderTile folderTile)
	{
		FolderAnimationManager folderAnimationManager = ServiceLocator.Current().GetInstance(FolderAnimationManager.class);
		folderAnimationManager.AnimateOpenFolder(folderTile);
	}
	
	private void OnFolderOpened(FolderTile folderTile)
	{
		FolderAnimationManager folderAnimationManager = ServiceLocator.Current().GetInstance(FolderAnimationManager.class);
		folderAnimationManager.AnimateOpenFolder(this.tileFolder);
	}
	
	synchronized public void CloseFolder()
	{
		try
		{
			if (!this.IsFolderOpened())
				return;
				
			this.OnFolderClosing(this.openedFolderTile);
			this.OnFolderClosed(this.openedFolderTile);
			// Devamı CloseFolderAnimationEnded() metodunda
		}
		catch (Exception ex)
		{
		
		}
	}
	
	private void OnFolderClosing(FolderTile folderTile)
	{
		FolderAnimationManager folderAnimationManager = ServiceLocator.Current().GetInstance(FolderAnimationManager.class);
		folderAnimationManager.AnimateCloseFolder(this.tileFolder);
	}
	
	private void OnFolderClosed(FolderTile folderTile)
	{
		FolderAnimationManager folderAnimationManager = ServiceLocator.Current().GetInstance(FolderAnimationManager.class);
		folderAnimationManager.AnimateCloseFolder(folderTile);
	}
	
	public void OnOpenFolderAnimationEnded()
	{
	
	}
	
	public void OnCloseFolderAnimationEnded()
	{
		TilesAdapter mainTilesAdapter = ServiceLocator.Current().GetInstance(IAppService.class).getTilesAdapter();
		this.tileFolder.SetParentTile(null);
		int indexOfTile = mainTilesAdapter.getItemIndex(this.tileFolder);
		mainTilesAdapter.RemoveTile(indexOfTile);
		this.openedFolderTile = null;
	}
}
