package com.ertis.andromeda.models;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.ertis.andromeda.helpers.Colors;

/**
 * Created by ertugrulozcan on 18.04.2018.
 */

public class Tile
{
	protected TileType type;
	private String caption;
	private String customLabel;
	private Drawable icon;
	private Drawable customIcon;
	private ColorDrawable tileColor;
	private int iconId;
	private AppModel application;
	private TileSize size;
	private TileStyle style;
	private String queryParams;
	
	public Tile(AppModel appModel, TileSize tileSize, ColorDrawable color)
	{
		this.type = TileType.AppTile;
		this.setApplication(appModel);
		this.setTileSize(tileSize);
		this.setTileColor(color);
		this.setTileStyle(TileStyle.Icon);
	}
	
	public Tile(AppModel appModel, TileSize tileSize, ColorDrawable color, TileStyle style)
	{
		this.type = TileType.AppTile;
		this.setApplication(appModel);
		this.setTileSize(tileSize);
		this.setTileColor(color);
		this.setTileStyle(style);
	}
	
	public static Tile CreateFakeTile(TileSize tileSize)
	{
		Tile tile = new Tile(null, tileSize, Colors.rgb("00000000"));
		tile.setCaption("");
		tile.setIcon(null);
		
		return tile;
	}
	
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
		
		if (label != null && label.length() > 25)
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
	
	public TileSize getTileSize()
	{
		return this.size;
	}
	
	public void setTileSize(TileSize type)
	{
		this.size = type;
	}
	
	public TileStyle getTileStyle()
	{
		return style;
	}
	
	private void setTileStyle(TileStyle style)
	{
		this.style = style;
	}
	
	public TileType getTileType()
	{
		return type;
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
	
	public enum TileSize
	{
		Small, Medium, MediumWide, Large
	}
	
	public enum TileType
	{
		AppTile, FolderTile
	}
	
	public enum TileStyle
	{
		Icon, Image, LiveTile,
	}
}
