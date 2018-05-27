package com.ertis.andromeda.models;

import com.ertis.andromeda.helpers.Colors;

import java.util.ArrayList;
import java.util.List;

public class FolderTile extends Tile
{
	private List<Tile> subTiles;
	
	public FolderTile(String folderName, TileSize tileSize)
	{
		super(null, tileSize, Colors.rgb(0x33FFFFFF), TileStyle.Image);
		
		this.type = TileType.FolderTile;
		this.subTiles = new ArrayList<>();
		this.setCustomLabel(folderName);
	}
	
	public List<Tile> getSubTiles()
	{
		return subTiles;
	}
	
	public void AddTiles(final List<Tile> tiles)
	{
		this.subTiles.addAll(tiles);
	}
}
