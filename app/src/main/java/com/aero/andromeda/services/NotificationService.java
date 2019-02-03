package com.aero.andromeda.services;

import android.content.Intent;
import android.service.notification.StatusBarNotification;

import com.aero.andromeda.models.AppModel;
import com.aero.andromeda.models.NotificationGroup;
import com.aero.andromeda.models.NotificationInfo;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.services.interfaces.INotificationService;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class NotificationService implements INotificationService
{
	public static String NOTIFICATION_SERVICE_LISTENER_KEY = "com.aero.andromeda.services.TILE_NOTIFICATION_LISTENER_SERVICE";
	public static String NOTIFICATION_INTENT_ACTION_KEY = "com.aero.andromeda.services.TILE_NOTIFICATION_LISTENER";
	public static String NOTIFICATION_EVENT_INTENT_KEY = "notification_event";
	public static String NOTIFICATION_COMMAND_KEY = "command";
	public static String LIST_NOTIFICATIONS_KEY = "list";
	public static String CLEAR_NOTIFICATIONS_KEY = "clearAll";
	
	private HashMap<AppModel, NotificationGroup> NotificationGroups;
	
	public NotificationService()
	{
		this.NotificationGroups = new LinkedHashMap<>();
	}
	
	public NotificationGroup GetNotificationGroup(TileBase tile)
	{
		if (tile == null)
			return null;
		
		AppModel appModel = tile.getApplication();
		if (appModel == null)
			return null;
		
		if (this.NotificationGroups.containsKey(appModel))
			return this.NotificationGroups.get(appModel);
		
		return null;
	}
	
	public void AddNotification(StatusBarNotification notification)
	{
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		if (appService == null)
			return;
		
		String packageName = notification.getPackageName();
		
		NotificationInfo notificationInfo = new NotificationInfo(1, notification.getNotification().tickerText.toString(), notification.toString());
		
		AppModel appModel = appService.GetAppModel(packageName);
		if (appModel != null)
		{
			if (this.NotificationGroups.containsKey(appModel))
			{
				NotificationGroup notificationGroup = this.NotificationGroups.get(appModel);
				notificationGroup.Add(notificationInfo);
			}
			else
			{
				NotificationGroup notificationGroup = new NotificationGroup(appModel.getApplicationPackageName());
				this.NotificationGroups.put(appModel, notificationGroup);
				notificationGroup.Add(notificationInfo);
			}
		}
	}
}
