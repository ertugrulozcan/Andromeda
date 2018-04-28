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
	private Drawable icon;
	private ColorDrawable tileColor;
	private int iconId;
	private AppModel application;
	private TileType type;
	private TileStyle style;
	
	public String getCaption()
	{
		String label = this.caption;
		if (application != null)
			label = this.application.getLabel();
		
		if (label.length() > 25)
			label = label.substring(0, 25);
		
		return label;
	}
	
	private void setCaption(String caption)
	{
		this.caption = caption;
	}
	
	public Drawable getIcon()
	{
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
	
	public void setTileType(TileType type)
	{
		this.type = type;
	}
	
	public TileStyle getStyle()
	{
		return style;
	}
	
	private void setStyle(TileStyle style)
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
	
	public Tile(AppModel appModel, TileType tileType, ColorDrawable color)
	{
		this.setApplication(appModel);
		this.setTileType(tileType);
		this.setTileColor(color);
		this.setStyle(TileStyle.Icon);
	}
	
	public Tile(AppModel appModel, TileType tileType, ColorDrawable color, TileStyle style)
	{
		this.setApplication(appModel);
		this.setTileType(tileType);
		this.setTileColor(color);
		this.setStyle(style);
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
		Big
	}
	
	public enum TileStyle
	{
		Icon,
		Image,
		LiveTile,
	}
}
