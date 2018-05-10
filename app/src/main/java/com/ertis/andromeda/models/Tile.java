package com.ertis.andromeda.models;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.ertis.andromeda.helpers.Colors;

/**
 * Created by ertugrulozcan on 18.04.2018.
 */

public class Tile
{
	private String caption;
	private String customLabel;
	private Drawable icon;
	private Drawable customIcon;
	private ColorDrawable tileColor;
	private int iconId;
	private AppModel application;
	protected TileType type;
	private TileStyle style;
	private String queryParams;
	
	public void setCustomLabel(String customLabel)
	{
		this.customLabel = customLabel;
	}
	
	public String getCaption()
	{
		String label = this.caption;
		if (application != null)
			label = this.application.getLabel();
		
		if (this.customLabel != null && !this.customLabel.isEmpty())
			label = this.customLabel;
		
		if (label.length() > 25)
			label = label.substring(0, 25);
		
		return label;
	}
	
	private void setCaption(String caption)
	{
		this.caption = caption;
	}
	
	public void setCustomIcon(Drawable customIcon)
	{
		this.customIcon = customIcon;
	}
	
	public Drawable getIcon()
	{
		if (this.customIcon != null)
			return this.customIcon;
		
		if (application != null)
			return this.application.getIcon();
		
		return this.icon;
	}
	
	private void setIcon(Drawable icon)
	{
		this.icon = icon;
	}
	
	public int getIconId()
	{
		return this.iconId;
	}
	
	private void setIconId(int id)
	{
		this.iconId = id;
	}
	
	public AppModel getApplication()
	{
		return this.application;
	}
	
	private void setApplication(AppModel app)
	{
		this.application = app;
	}
	
	public TileType getTileType()
	{
		return this.type;
	}
	
	public void setTileType(TileType type) throws Exception
	{
		if (type == TileType.Group)
			throw new Exception("Group yalnizca TileGroup class'i tarafindan kullanilablir.");
		
		this.type = type;
	}
	
	public TileStyle getTileStyle()
	{
		return style;
	}
	
	private void setTileStyle(TileStyle style)
	{
		this.style = style;
	}
	
	public ColorDrawable getTileColor()
	{
		return tileColor;
	}
	
	public void setTileColor(ColorDrawable tileColor)
	{
		this.tileColor = tileColor;
	}
	
	public String getQueryParams()
	{
		return queryParams;
	}
	
	public void setQueryParams(String queryParams)
	{
		this.queryParams = queryParams;
	}
	
	public Tile(AppModel appModel, TileType tileType, ColorDrawable color)
	{
		try
		{
			this.setApplication(appModel);
			this.setTileType(tileType);
			this.setTileColor(color);
			this.setTileStyle(TileStyle.Icon);
		}
		catch (Exception ex)
		{
		
		}
	}
	
	public Tile(AppModel appModel, TileType tileType, ColorDrawable color, TileStyle style)
	{
		try
		{
			this.setApplication(appModel);
			this.setTileType(tileType);
			this.setTileColor(color);
			this.setTileStyle(style);
		}
		catch (Exception ex)
		{
		
		}
	}
	
	public static Tile CreateFakeTile(TileType tileType)
	{
		Tile tile = new Tile(null, tileType, Colors.rgb("00000000"));
		tile.setCaption("");
		tile.setIcon(null);
		
		return tile;
	}
	
	public enum TileType
	{
		Small,
		Medium,
		MediumWide,
		Big,
		Group
	}
	
	public enum TileStyle
	{
		Icon,
		Image,
		LiveTile,
	}
}
