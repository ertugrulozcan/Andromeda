package com.aero.andromeda.models;

import java.util.ArrayList;
import java.util.List;

public class NotificationGroup
{
	private String packageName;
	private List<NotificationInfo> notificationList;
	
	private int getCounter = 0;
	
	public NotificationGroup(String packageName)
	{
		this.packageName = packageName;
		this.notificationList = new ArrayList<>();
	}
	
	public NotificationGroup(String packageName, List<NotificationInfo> notifications)
	{
		this.packageName = packageName;
		this.notificationList = new ArrayList<>();
		this.notificationList.addAll(notifications);
	}
	
	public int GetCount()
	{
		return this.notificationList.size();
	}
	
	public void Add(NotificationInfo notificationInfo)
	{
		this.notificationList.add(notificationInfo);
	}
	
	public void Remove(NotificationInfo notificationInfo)
	{
		for (NotificationInfo notification : this.notificationList)
		{
			if (notification.getId() == notificationInfo.getId())
				this.notificationList.remove(notification);
		}
	}
	
	public NotificationInfo GetNext()
	{
		if (this.notificationList.size() == 0)
			return null;
		
		this.getCounter++;
		this.getCounter = this.getCounter % this.notificationList.size();
		
		if (this.getCounter < this.notificationList.size())
		{
			return this.notificationList.get(this.getCounter);
		}
		
		this.getCounter = 0;
		return null;
	}
}
