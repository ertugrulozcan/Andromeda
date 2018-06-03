package com.ertis.andromeda.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.ertis.andromeda.models.Tile;

/**
 * Created by ertugrulozcan on 19.04.2018.
 */

public class SizeConverter
{
	private final static int SMALL_TILE_SIZE = 221;
	private final static int MEDIUM_TILE_SIZE = 458;
	private final static int WIDE_TILE_SIZE = 932;
	private final static int FULL_TILE_SIZE = 1406;
	private final static int TILE_MARGIN = 8;
	
	public static double ConvertToDP(double pixel, Context view)
	{
		//return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, view.getResources().getDisplayMetrics());
		return pixel;
	}
	
	public static int GetTileWidth(Context context, Tile.TileSize tileSize)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int displayWidth = metrics.widthPixels;
		
		switch (tileSize)
		{
			default:
			case Small:
				return (int) (displayWidth * SMALL_TILE_SIZE / 1440);
			case Medium:
				return (int) (displayWidth * MEDIUM_TILE_SIZE / 1440);
			case MediumWide:
				return (int) (displayWidth * WIDE_TILE_SIZE / 1440);
			case Large:
				return (int) (displayWidth * WIDE_TILE_SIZE / 1440);
		}
	}
	
	public static int GetTileMargin(Context context)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int displayWidth = metrics.widthPixels;
		
		return (int) (displayWidth * TILE_MARGIN / 1440);
	}
	
	public static int GetTilePanelFullWidth(Context context)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int displayWidth = metrics.widthPixels;
		
		return (int) (displayWidth * FULL_TILE_SIZE / 1440);
	}
	
	public static int GetFolderTileThumbnailSize(Context context)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int displayWidth = metrics.widthPixels;
		
		return (int) ((displayWidth * (MEDIUM_TILE_SIZE - 2) / 1440) / 3);
	}
}
