package com.aero.andromeda.models.tiles;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.aero.andromeda.helpers.Colors;
import com.aero.andromeda.models.AppModel;
import com.aero.andromeda.ui.BaseTileViewHolder;

public abstract class TileBase
{
	private long id;
	
	public static final ColorDrawable DefaultTileColor = Colors.rgb("#A3194084");
	
	private TileSize tileSize = TileSize.Medium;
	private TileType tileType = TileType.Icon;
	
	private AppModel application;
	private String caption;
	private Drawable icon;
	private ColorDrawable tileColor;
	
	private BaseTileViewHolder parentViewHolder;
	
	protected TileBase(final long id, TileType tileType, AppModel app)
	{
		this.id = id;
		this.setTileType(tileType);
		this.setApplication(app);
		
		if (application != null)
			this.setCaption(this.application.getAppLabel());
		
		if (application != null)
			this.setIcon(this.application.getIcon());
		
		this.setTileColor(DefaultTileColor);
	}
	
	public long getId()
	{
		return this.id;
	}
	
	public abstract void OnClick(BaseTileViewHolder holder);
	
	public abstract boolean OnLongClick(BaseTileViewHolder holder);
	
	public TileSize getTileSize()
	{
		return tileSize;
	}
	
	public void setTileSize(TileSize tileSize)
	{
		this.tileSize = tileSize;
	}
	
	public TileType getTileType()
	{
		return tileType;
	}
	
	private void setTileType(TileType tileType)
	{
		this.tileType = tileType;
	}
	
	public AppModel getApplication()
	{
		return application;
	}
	
	private void setApplication(AppModel application)
	{
		this.application = application;
	}
	
	public String getCaption()
	{
		String label = this.caption;
		
		if (this.caption != null && !this.caption.isEmpty())
			label = this.caption;
		
		if (application != null)
			label = this.application.getAppLabel();
		
		if (label != null && label.length() > 25)
			label = label.substring(0, 25);
		
		if (label == null)
			label = "";
		
		return label;
	}
	
	public void setCaption(String caption)
	{
		this.caption = caption;
	}
	
	public Drawable getIcon()
	{
		if (this instanceof IconTile)
		{
			Drawable customIcon = ((IconTile)this).getCustomIcon();
			if (customIcon != null)
				return customIcon;
		}
		
		if (this.icon != null)
			return this.icon;
		
		if (application != null)
			return this.application.getIcon();
		
		return this.icon;
	}
	
	private void setIcon(Drawable icon)
	{
		this.icon = icon;
	}
	
	public ColorDrawable getTileColor()
	{
		return tileColor;
	}
	
	public void setTileColor(ColorDrawable tileColor)
	{
		this.tileColor = tileColor;
	}
	
	public BaseTileViewHolder getParentViewHolder()
	{
		return parentViewHolder;
	}
	
	public void setParentViewHolder(BaseTileViewHolder parentViewHolder)
	{
		this.parentViewHolder = parentViewHolder;
	}
	
	public String toString()
	{
		return this.getCaption();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		
		if (obj instanceof TileBase)
		{
			TileBase other = (TileBase)obj;
			return other.hashCode() == this.hashCode();
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return (int)this.getId();
		
		/*
		AppModel appModel = this.getApplication();
		TileType tileType = this.getTileType();
		
		int tileTypeInt = 1;
		switch (tileType)
		{
			case Icon:  tileTypeInt = 2;
				break;
			case Image:  tileTypeInt = 3;
				break;
			case LiveTile:  tileTypeInt = 5;
				break;
			case FolderTile:  tileTypeInt = 7;
				break;
			case Folder:  tileTypeInt = 11;
				break;
			case TilesHeader:  tileTypeInt = 13;
				break;
			case TilesFooter:  tileTypeInt = 17;
				break;
		}
		
		if (appModel != null)
			return appModel.hashCode() * tileTypeInt;
		else
			return super.hashCode() * tileTypeInt;
		*/
	}
	
	public enum TileSize
	{
		Small,
		Medium,
		MediumWide,
		Large
	}
	
	public enum TileType
	{
		Icon,
		Image,
		LiveTile,
		FolderTile,
		Folder,
		TilesHeader,
		TilesFooter
	}
}
