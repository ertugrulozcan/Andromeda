package com.ertis.andromeda.managers;

import android.support.v7.widget.RecyclerView;

import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.models.*;

import java.util.ArrayList;
import java.util.List;

public class TileFolderManager
{
	private static TileFolderManager self = new TileFolderManager();
	public static TileFolderManager Current = self;
	
	private boolean isConstructed;
	private TilesAdapter.TileFolderViewHolder folderViewHolder;
	private TilesAdapter mainTilesAdapter;
	private TilesAdapter folderTilesAdapter;
	private List<Tile> subTiles;
	
	private boolean isFolderOpen;
	private FolderTile openedFolderTile;
	private TileFolder tileFolder;
	
	private TileFolderManager()
	{
		this.subTiles = new ArrayList<>();
		this.tileFolder = new TileFolder("");
	}
	
	public void Construct(final TilesAdapter.TileFolderViewHolder folderViewHolder)
	{
		if (this.isConstructed)
			return;
		
		this.folderViewHolder = folderViewHolder;
		this.folderTilesAdapter = new TilesAdapter(folderViewHolder.getFolderLayoutBase().getContext(), this.subTiles);
		RecyclerView recyclerView = this.folderViewHolder.getRecyclerView();
		SpannedGridLayoutManager spannedGridLayoutManager = new SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL, 6);
		spannedGridLayoutManager.setItemOrderIsStable(true);
		recyclerView.setLayoutManager(spannedGridLayoutManager);
		this.folderViewHolder.getRecyclerView().setAdapter(this.folderTilesAdapter);
		this.folderTilesAdapter.notifyDataSetChanged();
		
		this.isConstructed = true;
	}
	
	public void setTilesAdapter(TilesAdapter tilesAdapter)
	{
		this.mainTilesAdapter = tilesAdapter;
	}
	
	public void OnClickFolderTile(FolderTile folderTile)
	{
		if (!isFolderOpen)
			this.OpenFolder(folderTile);
		else
			this.CloseFolder();
	}
	
	public void OpenFolder(FolderTile folderTile)
	{
		if (folderTile == null)
			return;
		
		try
		{
			this.CloseFolder();
			
			this.openedFolderTile = folderTile;
			int indexOfTile = this.mainTilesAdapter.getItemIndex(this.openedFolderTile);
			int indexOfFolder = indexOfTile + 1;
			this.tileFolder.setCustomLabel(folderTile.getCaption());
			
			this.subTiles.clear();
			this.subTiles.addAll(folderTile.getSubTiles());
			if (this.folderTilesAdapter != null)
				this.folderTilesAdapter.notifyDataSetChanged();
			
			this.mainTilesAdapter.InsertTile(tileFolder, indexOfFolder);
			
			this.mainTilesAdapter.ScrollToItem(indexOfTile);
		}
		finally
		{
			this.isFolderOpen = true;
		}
	}
	
	public void CloseFolder()
	{
		if (this.tileFolder == null)
			return;
		
		try
		{
			int indexOfTile = this.mainTilesAdapter.getItemIndex(this.tileFolder);
			if (indexOfTile >= 0)
				this.mainTilesAdapter.RemoveTile(indexOfTile);
			
			this.subTiles.clear();
		}
		finally
		{
			this.openedFolderTile = null;
			this.isFolderOpen = false;
		}
	}
}
