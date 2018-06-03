package com.ertis.andromeda.managers;

import android.support.v7.widget.RecyclerView;

import com.ertis.andromeda.AppDrawerFragment;
import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.listeners.TileClickListener;
import com.ertis.andromeda.models.FolderTile;
import com.ertis.andromeda.models.TileFolder;

public class TileFolderManager
{
	private static TileFolderManager self = new TileFolderManager();
	public static TileFolderManager Current = self;
	
	private boolean isConstructed;
	private TilesAdapter.TileFolderViewHolder folderViewHolder;
	private TilesAdapter mainTilesAdapter;
	private TilesAdapter folderTilesAdapter;
	
	private FolderTile openedFolderTile;
	private TileFolder tileFolder;
	
	private TileFolderManager()
	{
		this.tileFolder = new TileFolder("");
	}
	
	public boolean IsFolderOpened()
	{
		return this.openedFolderTile != null;
	}
	
	public void Construct(final TilesAdapter.TileFolderViewHolder folderViewHolder)
	{
		if (this.isConstructed)
			return;
		
		this.folderViewHolder = folderViewHolder;
		this.folderTilesAdapter = new TilesAdapter(folderViewHolder.getFolderLayoutBase().getContext(), this.tileFolder.getSubTiles(), null);
		
		TileClickListener tileClickListener = AppDrawerFragment.Current.GenerateTileClickListener(this.folderTilesAdapter);
		this.folderTilesAdapter.setOnClickListener(tileClickListener);
		this.folderTilesAdapter.setOnLongClickListener(tileClickListener);
		
		RecyclerView recyclerView = this.folderViewHolder.getRecyclerView();
		SpannedGridLayoutManager spannedGridLayoutManager = new SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL, 6);
		spannedGridLayoutManager.setItemOrderIsStable(true);
		recyclerView.setLayoutManager(spannedGridLayoutManager);
		recyclerView.setAdapter(this.folderTilesAdapter);
		this.folderTilesAdapter.notifyDataSetChanged();
		
		this.isConstructed = true;
	}
	
	public void setTilesAdapter(TilesAdapter tilesAdapter)
	{
		this.mainTilesAdapter = tilesAdapter;
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
		
		if (this.folderTilesAdapter != null)
		{
			synchronized (this.folderTilesAdapter)
			{
				this.folderTilesAdapter.notifyAll();
			}
		}
	}
	
	synchronized private void OpenFolder(FolderTile folderTile)
	{
		if (folderTile == null)
			return;
		
		try
		{
			this.openedFolderTile = folderTile;
			
			this.tileFolder.SetParentTile(this.openedFolderTile);
			if (this.folderTilesAdapter != null)
			{
				synchronized (this.folderTilesAdapter)
				{
					this.folderTilesAdapter.notifyAll();
				}
			}
			
			int indexOfTile = this.mainTilesAdapter.getItemIndex(this.openedFolderTile);
			int indexOfFolder = indexOfTile + 1;
			this.mainTilesAdapter.InsertTile(this.tileFolder, indexOfFolder);
			
			//this.mainTilesAdapter.ScrollToItem(indexOfTile);
		}
		catch (Exception ex)
		{

		}
	}
	
	synchronized public void CloseFolder()
	{
		try
		{
			this.openedFolderTile = null;
			this.tileFolder.SetParentTile(this.openedFolderTile);
			
			if (this.folderTilesAdapter != null)
			{
				synchronized (this.folderTilesAdapter)
				{
					this.folderTilesAdapter.notifyAll();
				}
			}
			
			int indexOfTile = this.mainTilesAdapter.getItemIndex(this.tileFolder);
			if (indexOfTile >= 0)
				this.mainTilesAdapter.RemoveTile(indexOfTile);
		}
		finally
		{

		}
	}
}
