package com.aero.andromeda.models;

public class NotificationInfo
{
	private int id;
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
	
	public NotificationInfo(int id, String title, String message)
	{
		this.id = id;
		this.title = title;
		this.message = message;
	}
	
	public int getId()
	{
		return this.id;
	}
}
