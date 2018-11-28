package com.aero.andromeda.managers;

import android.graphics.Point;

import com.aero.andromeda.models.tiles.TileBase;

import java.util.ArrayList;
import java.util.List;

public class TileMap
{
	private static int SPAN_SIZE = 6;
	private static int MAX_ROW_COUNT = 100;
	
	private int[][] map;
	private List<Integer> emptyCellIndexes = new ArrayList<>();
	
	public TileMap(final List<TileBase> tileList)
	{
		this.map = this.GenerateTileMap();
		this.PlotMap(this.map, tileList);
	}
	
	public int[][] getMap()
	{
		return this.map;
	}
	
	public List<Integer> getEmptyCellIndexes()
	{
		return emptyCellIndexes;
	}
	
	private int[][] GenerateTileMap()
	{
		int[][] map = new int[MAX_ROW_COUNT][SPAN_SIZE];
		
		for (int i = 0; i < MAX_ROW_COUNT; i++)
		{
			map[i] = new int[SPAN_SIZE];
			for (int j = 0; j < SPAN_SIZE; j++)
				map[i][j] = 0;
		}
		
		return map;
	}
	
	private void PlotMap(int[][] map, final List<TileBase> tileList)
	{
		int tileNo = 0;
		for (TileBase tile : tileList)
		{
			int tryCount = 0;
			RectSize tileSize = this.GetTileSize(tile);
			Point cell;
			
			do
			{
				cell = this.FindFirstEmptyCell(map, tryCount);
				if (cell != null)
				{
					if (this.IsEmptyRect(map, cell, tileSize))
					{
						this.FillCells(map, cell, tileSize);
						boolean isEmpty = this.FlagVisitedEmptyCells(map, cell);
						if (isEmpty)
							this.emptyCellIndexes.add(tileNo);
						break;
					}
				}
				
				tryCount++;
			}
			while (!this.IsEmptyRect(map, cell, tileSize));
			
			tileNo++;
		}
	}
	
	public List<Point> GetEmptyCellCoordinates()
	{
		List<Point> emptyCells = new ArrayList<>();
		for (int i = 0; i < SPAN_SIZE; i++)
		{
			for (int j = 0; j < MAX_ROW_COUNT; j++)
			{
				if (map[j][i] == -1)
					emptyCells.add(new Point(i, j));
			}
		}
		
		return emptyCells;
	}
	
	public List<List<Point>> GetSequentialEmptyCellGroups()
	{
		List<Point> emptyCells = this.GetEmptyCellCoordinates();
		List<List<Point>> cellGroups = new ArrayList<>();
		
		List<Point> pivotGroup = new ArrayList<>();
		cellGroups.add(pivotGroup);
		
		for (Point cell : emptyCells)
		{
			if (pivotGroup.size() > 0)
			{
				Point lastCell = pivotGroup.get(pivotGroup.size() - 1);
				if (lastCell.y == cell.y && lastCell.x == cell.x - 1)
				{
					pivotGroup.add(cell);
				}
				else
				{
					pivotGroup = new ArrayList<>();
					pivotGroup.add(cell);
					cellGroups.add(pivotGroup);
				}
			}
			else
			{
				pivotGroup.add(cell);
			}
		}
		
		return cellGroups;
	}
	
	private Point FindFirstEmptyCell(int[][] map)
	{
		return this.FindFirstEmptyCell(map, 0);
	}
	
	private Point FindFirstEmptyCell(int[][] map, int skipCount)
	{
		int skip = 0;
		for (int i = 0; i < MAX_ROW_COUNT; i++)
		{
			for (int j = 0; j < SPAN_SIZE; j++)
			{
				if (map[i][j] == 0)
				{
					if (skip == skipCount)
						return new Point(j, i);
					else
						skip++;
				}
			}
		}
		
		return null;
	}
	
	private boolean IsEmptyRect(int[][] map, Point origin, RectSize rect)
	{
		if (origin.x + rect.width > SPAN_SIZE)
			return false;
		
		if (origin.y + rect.height > MAX_ROW_COUNT)
			return false;
		
		int startX = origin.x;
		int startY = origin.y;
		int endX = origin.x + rect.width - 1;
		int endY = origin.y + rect.height - 1;
		
		startX = Math.min(startX, SPAN_SIZE - 1);
		startY = Math.min(startY, MAX_ROW_COUNT - 1);
		endX = Math.min(endX, SPAN_SIZE - 1);
		endY = Math.min(endY, MAX_ROW_COUNT - 1);
		
		for (int i = startX; i <= endX; i++)
		{
			for (int j = startY; j <= endY; j++)
			{
				if (map[j][i] == 1)
					return false;
			}
		}
		
		return true;
	}
	
	private void FillCells(int[][] map, Point origin, RectSize rect)
	{
		for (int i = origin.x; i < origin.x + rect.width; i++)
		{
			for (int j = origin.y; j < origin.y + rect.height; j++)
			{
				map[j][i] = 1;
			}
		}
	}
	
	private boolean FlagVisitedEmptyCells(int[][] map, Point lastVisitedCell)
	{
		boolean isFound = false;
		for (int i = 0; i <= lastVisitedCell.y; i++)
		{
			int x = SPAN_SIZE - 1;
			if (i == lastVisitedCell.y)
				x = lastVisitedCell.x;
			
			for (int j = 0; j <= x; j++)
			{
				if (map[i][j] == 0)
				{
					map[i][j] = -1;
					isFound = true;
				}
			}
		}
		
		return isFound;
	}
	
	private RectSize GetTileSize(TileBase tile)
	{
		switch (tile.getTileSize())
		{
			case Small: return new RectSize(1, 1);
			case Medium: return new RectSize(2, 2);
			case MediumWide: return new RectSize(4, 2);
			case Large: return new RectSize(4, 4);
		}
		
		return new RectSize(0, 0);
	}
	
	public class RectSize
	{
		public int width;
		public int height;
		
		public RectSize(int w, int h)
		{
			this.width = w;
			this.height = h;
		}
	}
}
