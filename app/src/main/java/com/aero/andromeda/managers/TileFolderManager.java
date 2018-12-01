package com.aero.andromeda.managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;

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
	
	private final FolderAnimationManager folderAnimationManager;
	
	private FolderViewHolder folderViewHolder;
	private TilesAdapter folderTilesAdapter;
	
	private FolderTile openedFolderTile;
	private Folder tileFolder;
	
	private TileFolderManager()
	{
		this.folderAnimationManager = FolderAnimationManager.Init();
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
		if (folderTile == null)
			return;
		
		try
		{
			TilesAdapter mainTilesAdapter = ServiceLocator.Current().GetInstance(IAppService.class).getTilesAdapter();
			
			this.openedFolderTile = folderTile;
			this.tileFolder.SetParentTile(this.openedFolderTile);
			
			int indexOfTile = mainTilesAdapter.getItemIndex(this.openedFolderTile);
			mainTilesAdapter.InsertTile(this.tileFolder, indexOfTile + 1);
			
			FolderAnimationManager.Current.OpenFolder(folderTile, this.tileFolder, null);
		}
		catch (Exception ex)
		{
		
		}
	}
	
	synchronized public void CloseFolder()
	{
		try
		{
			if (!this.IsFolderOpened())
				return;
			
			FolderAnimationManager.Current.CloseFolder(this.openedFolderTile, this.tileFolder, new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					super.onAnimationEnd(animation);
					
					TilesAdapter mainTilesAdapter = ServiceLocator.Current().GetInstance(IAppService.class).getTilesAdapter();
					TileFolderManager.this.tileFolder.SetParentTile(null);
					int indexOfTile = mainTilesAdapter.getItemIndex(TileFolderManager.this.tileFolder);
					mainTilesAdapter.RemoveTile(indexOfTile);
					TileFolderManager.this.openedFolderTile = null;
				}
			});
		}
		catch (Exception ex)
		{
		
		}
	}
}
