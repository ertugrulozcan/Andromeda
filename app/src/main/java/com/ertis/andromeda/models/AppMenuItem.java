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
		if (this.isHeaderItem)
			return this.getHeader();
		
		return app.getLabel();
	}
	
	public Drawable getIcon()
	{
		if (app != null)
			return this.app.getIcon();
		
		return null;
	}
	
	private boolean isHeaderItem = false;
	public boolean isHeaderItem()
	{
		return this.isHeaderItem;
	}
	
	private String headerCaption;
	public void setHeader(String header)
	{
		headerCaption = header;
	}
	public String getHeader()
	{
		return headerCaption;
	}
	
	public AppMenuItem(AppModel appModel)
	{
		this.isHeaderItem = false;
		this.setApp(appModel);
	}
	
	private AppMenuItem()
	{
		this.isHeaderItem = false;
		this.setApp(null);
	}
	
	public static AppMenuItem CreateHeaderMenuItem(String header)
	{
		AppMenuItem headerItem = new AppMenuItem();
		headerItem.isHeaderItem = true;
		headerItem.setHeader(header);
		
		return headerItem;
	}
}
