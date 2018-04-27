package com.ertis.andromeda.models;

import android.graphics.drawable.Drawable;

public class AppMenuItem
{
	private AppModel app;
	
	public AppModel getApp()
	{
		return app;
	}
	
	private void setApp(AppModel app)
	{
		this.app = app;
	}
	
	public String getLabel()
	{
		return app.getLabel();
	}
	
	public Drawable getIcon()
	{
		if (app != null)
			return this.app.getIcon();
		
		return null;
	}
	
	public AppMenuItem(AppModel appModel)
	{
		this.setApp(appModel);
	}
}
