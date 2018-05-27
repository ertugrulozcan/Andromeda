package com.ertis.andromeda.models;

import android.graphics.Point;

import com.ertis.andromeda.helpers.Colors;

import java.util.ArrayList;
import java.util.List;

public class TileFolder extends Tile
{
	private List<Tile> subTiles;
	
	public TileFolder(String folderName)
	{
		super(null, null, Colors.rgb(0x00000000));
		this.setCustomLabel(folderName);
		
		this.subTiles = new ArrayList<>();
	}
	
	public List<Tile> getSubTiles()
	{
		return subTiles;
	}
	
	public void SetParentTile(FolderTile folderTile)
	{
		this.subTiles.clear();
		
		if (folderTile != null)
		{
			this.setCustomLabel(folderTile.getCaption());
			this.subTiles.addAll(folderTile.getSubTiles());
		}
		else
		{
			this.setCustomLabel("");
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
		
		for (Tile tile : this.subTiles)
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
}
