package com.aero.andromeda.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.aero.andromeda.Andromeda;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.ISettingsService;
import com.aero.andromeda.settings.UISettings;

import java.util.regex.MatchResult;

/**
 * Created by ertugrulozcan on 19.04.2018.
 */

public class SizeConverter
{
	private int LAYOUT_WIDTH, TILE_MARGIN, SCREEN_WIDTH, FULL_TILE_SIZE, SMALL_TILE_SIZE, MEDIUM_TILE_SIZE, WIDE_TILE_SIZE;
	
	private Context context;
	
	public static SizeConverter Current;
	
	private DisplayMetrics deviceResolution;
	
	private SizeConverter(Context context)
	{
		Current = this;
		this.context = context;
		
		ISettingsService settingsService = ServiceLocator.Current().GetInstance(ISettingsService.class);
		UISettings uiSettings = settingsService.getUISettings();
		
		this.LAYOUT_WIDTH = uiSettings.getLayoutWidth();
		this.TILE_MARGIN = 12;
		this.SCREEN_WIDTH = 1440;
		this.FULL_TILE_SIZE = SCREEN_WIDTH - 2 * TILE_MARGIN;
		this.SMALL_TILE_SIZE = (FULL_TILE_SIZE - (LAYOUT_WIDTH - 1) * TILE_MARGIN) / LAYOUT_WIDTH;
		this.MEDIUM_TILE_SIZE = 2 * SMALL_TILE_SIZE + TILE_MARGIN;
		this.WIDE_TILE_SIZE = 4 * SMALL_TILE_SIZE + 3 * TILE_MARGIN;
	}
	
	public static SizeConverter Init(Context context)
	{
		return new SizeConverter(context);
	}
	
	public static double ConvertToDP(double pixel, Context view)
	{
		//return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, view.getResources().getDisplayMetrics());
		return pixel;
	}
	
	public int GetTileWidth(TileBase.TileSize tileSize)
	{
		int displayWidth = this.GetDeviceResolution().widthPixels;
		
		switch (tileSize)
		{
			default:
			case Small:
				return displayWidth * SMALL_TILE_SIZE / SCREEN_WIDTH;
			case Medium:
				return displayWidth * MEDIUM_TILE_SIZE / SCREEN_WIDTH;
			case MediumWide:
				return displayWidth * WIDE_TILE_SIZE / SCREEN_WIDTH;
			case Large:
				return displayWidth * WIDE_TILE_SIZE / SCREEN_WIDTH;
		}
	}
	
	public int GetTileHeight(TileBase.TileSize tileSize)
	{
		switch (tileSize)
		{
			default:
			case Small:
				return GetTileWidth(TileBase.TileSize.Small);
			case Medium:
				return GetTileWidth(TileBase.TileSize.Medium);
			case MediumWide:
				return GetTileWidth(TileBase.TileSize.Medium);
			case Large:
				return GetTileWidth(TileBase.TileSize.Large);
		}
	}
	
	public int GetTileMargin()
	{
		return this.GetDeviceResolution().widthPixels * TILE_MARGIN / SCREEN_WIDTH;
	}
	
	public int GetTilePanelFullWidth()
	{
		return this.GetDeviceResolution().widthPixels * FULL_TILE_SIZE / SCREEN_WIDTH;
	}
	
	public int GetFolderTileThumbnailSize()
	{
		return (this.GetDeviceResolution().widthPixels * (MEDIUM_TILE_SIZE - 2) / SCREEN_WIDTH) / 3 + 1;
	}
	
	public DisplayMetrics GetDeviceResolution()
	{
		if (this.deviceResolution == null)
		{
			DisplayMetrics metrics = new DisplayMetrics();
			((Activity) this.context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			this.deviceResolution = metrics;
		}
		
		return this.deviceResolution;
	}
	
	public int GetDefaultIconSize()
	{
		int deviceWidth = this.GetDeviceResolution().widthPixels;
		
		return (deviceWidth / LAYOUT_WIDTH) * 2 / 3 + 10;
	}
	
	public float GetTileSizeProportion()
	{
		return (float)6 / (float)this.LAYOUT_WIDTH;
	}
	
	public int GetTileCornerButtonSize()
	{
		return (int)(70 * this.GetTileSizeProportion());
	}
	
	public float GetDefaultFontSize()
	{
		if (this.LAYOUT_WIDTH == 6)
			return 16;
		else
			return 11;
	}
}
