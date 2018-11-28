package com.aero.andromeda.models.tiles;

import android.graphics.Point;
import android.view.View;

import com.aero.andromeda.helpers.Colors;
import com.aero.andromeda.ui.BaseTileViewHolder;

import java.util.ArrayList;
import java.util.List;

public class Folder extends TileBase
{
	private static long FOLDER_SINGLE_ID = 999999;
	
	private List<Tile> subTiles;
	
	public Folder()
	{
		super(FOLDER_SINGLE_ID, TileType.Folder, null);
		
		this.subTiles = new ArrayList<>();
		this.setTileColor(Colors.rgb("#00FFFFFF"));
	}
	
	public List<Tile> getSubTiles()
	{
		return subTiles;
	}
	
	public void AddTiles(final List<Tile> tiles)
	{
		this.subTiles.addAll(tiles);
	}
	
	public void SetParentTile(FolderTile folderTile)
	{
		this.subTiles.clear();
		
		if (folderTile != null)
		{
			this.setCaption(folderTile.getCaption());
			this.AddTiles(folderTile.getSubTiles());
		}
		else
		{
			this.setCaption("");
		}
	}
	
	public int GetTotalRowCount()
	{
		int rowCount = 0;
		boolean[][] layoutMap = this.GetTileLayoutMap();
		for (int r = 0; r < layoutMap.length; r++)
		{
			for (int c = 0; c < layoutMap[r].length; c++)
			{
				if (layoutMap[r][c])
				{
					rowCount++;
					break;
				}
				
				return rowCount + 1;
			}
		}
		
		return rowCount;
	}
	
	private boolean[][] GetTileLayoutMap()
	{
		boolean[][] tileLayoutMatrix = new boolean[100][6];
		
		for (TileBase tile : this.subTiles)
		{
			int width = 0;
			int height = 0;
			
			switch (tile.getTileSize())
			{
				case Small:
				{
					width = 1;
					height = 1;
				}
				break;
				
				case Medium:
				{
					width = 2;
					height = 2;
				}
				break;
				
				case MediumWide:
				{
					width = 4;
					height = 2;
				}
				break;
				
				case Large:
				{
					width = 4;
					height = 4;
				}
				break;
			}
			
			Point point = this.FindFirstSufficientCell(tileLayoutMatrix, width);
			for (int r = point.x; r < point.x + width; r++)
			{
				for (int c = point.y; c < point.y + height; c++)
				{
					tileLayoutMatrix[r][c] = true;
				}
			}
		}
		
		return tileLayoutMatrix;
	}
	
	private Point FindFirstSufficientCell(final boolean[][] tileLayoutMatrix, int tileWidth)
	{
		int step = 0;
		Point point = FindFirstEmptyCell(tileLayoutMatrix);
		while (point.y + tileWidth > 6)
			point = FindFirstEmptyCell(tileLayoutMatrix, ++step);
		
		return point;
	}
	
	private Point FindFirstEmptyCell(final boolean[][] tileLayoutMatrix)
	{
		return FindFirstEmptyCell(tileLayoutMatrix, 0);
	}
	
	private Point FindFirstEmptyCell(final boolean[][] tileLayoutMatrix, final int step)
	{
		int stepCount = step;
		for (int r = 0; r < tileLayoutMatrix.length; r++)
		{
			for (int c = 0; c < tileLayoutMatrix[r].length; c++)
			{
				if (!tileLayoutMatrix[r][c] && stepCount-- == 0)
					return new Point(r, c);
			}
		}
		
		return null;
	}
	
	@Override
	public void OnClick(BaseTileViewHolder holder)
	{
	
	}
	
	@Override
	public boolean OnLongClick(BaseTileViewHolder holder)
	{
		return false;
	}
}
