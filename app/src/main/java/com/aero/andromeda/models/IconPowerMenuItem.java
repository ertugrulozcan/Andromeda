package com.aero.andromeda.models;

import android.graphics.drawable.Drawable;

public class IconPowerMenuItem
{
	private Drawable icon;
	private String title;
	
	public IconPowerMenuItem(Drawable icon, String title)
	{
		this.icon = icon;
		this.title = title;
	}
	
	public Drawable getIcon()
	{
		return icon;
	}
	
	public void setIcon(Drawable icon)
	{
		this.icon = icon;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
}
