package com.aero.andromeda.models.tiles;

import android.view.View;

import com.aero.andromeda.helpers.Colors;
import com.aero.andromeda.managers.TileFolderManager;
import com.aero.andromeda.ui.BaseTileViewHolder;
import com.aero.andromeda.ui.FolderTileViewHolder;

import java.util.ArrayList;
import java.util.List;

public class FolderTile extends Tile
{
	private List<Tile> subTiles;
	
	public FolderTile(final long id)
	{
		super(id, TileType.FolderTile, null);
		
		this.setTileColor(Colors.rgb("#54FFFFFF"));
		this.subTiles = new ArrayList<>();
	}
	
	public List<Tile> getSubTiles()
	{
		return subTiles;
	}
	
	public void AddTiles(final List<Tile> tiles)
	{
		this.subTiles.addAll(tiles);
	}
	
	@Override
	public void OnClick(BaseTileViewHolder holder)
	{
		if (holder instanceof FolderTileViewHolder)
		{
            holder.setIsRecyclable(false);
			FolderTileViewHolder folderTileViewHolder = (FolderTileViewHolder)holder;
            TileFolderManager.Current.OnClickFolderTileAsync(this);
		}
	}
	
	@Override
	public boolean OnLongClick(BaseTileViewHolder holder, View holdedView)
	{
		return false;
	}
}
