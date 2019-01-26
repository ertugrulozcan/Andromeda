package com.aero.andromeda.models;

public class NotificationInfo
{
	private String title;
	private String message;
	
	public String getTitle()
	{
		return title;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public NotificationInfo(String title, String message)
	{
		this.title = title;
		this.message = message;
	}
}
