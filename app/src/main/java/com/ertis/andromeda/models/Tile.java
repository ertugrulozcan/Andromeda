package com.ertis.andromeda.models;

import android.graphics.drawable.Drawable;

/**
 * Created by ertugrulozcan on 18.04.2018.
 */

public class Tile
{
	private String caption;
	private Drawable icon;
	private int iconId;
	private AppModel application;
	private TileType type;
	
	public String getCaption()
	{
		String label = this.caption;
		if (application != null)
			label = this.application.getLabel();
		
		if (label.length() > 15)
			label = label.substring(0, 15);
		
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
	
	public Tile(String label, Drawable icon, TileType tileType)
	{
		this.setCaption(label);
		this.setIcon(icon);
		this.setTileType(tileType);
	}
	
	public Tile(String label, int iconId, TileType tileType)
	{
		this.setCaption(label);
		this.setIconId(iconId);
		this.setTileType(tileType);
	}
	
	public Tile(AppModel appModel, TileType tileType)
	{
		this.setApplication(appModel);
		this.setTileType(tileType);
	}
	
	public enum TileType
	{
		Small,
		Medium,
		MediumWide,
		Big
	}
}
