package com.aero.andromeda.services;

import android.app.Notification;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.support.v7.widget.LinearSmoothScroller;

import com.aero.andromeda.adapters.NotificationInfoAdapter;
import com.aero.andromeda.animations.TileAnimationManager;
import com.aero.andromeda.models.AppModel;
import com.aero.andromeda.models.NotificationGroup;
import com.aero.andromeda.models.NotificationInfo;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.services.interfaces.INotificationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class NotificationService implements INotificationService
{
	public static String NOTIFICATION_SERVICE_LISTENER_KEY = "com.aero.andromeda.services.TILE_NOTIFICATION_LISTENER_SERVICE";
	public static String NOTIFICATION_INTENT_ACTION_KEY = "com.aero.andromeda.services.TILE_NOTIFICATION_LISTENER";
	public static String NOTIFICATION_EVENT_INTENT_KEY = "notification_event";
	public static String NOTIFICATION_COMMAND_KEY = "command";
	public static String LIST_NOTIFICATIONS_KEY = "list";
	public static String CLEAR_NOTIFICATIONS_KEY = "clearAll";
	
	private HashMap<AppModel, NotificationGroup> NotificationGroups;

    private List<TileBase> TilesWithNotification;
	
	public NotificationService()
	{
		this.NotificationGroups = new LinkedHashMap<>();
		this.TilesWithNotification = new ArrayList<>();
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
	
	public void AddNotification(StatusBarNotification statusBarNotification)
	{
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		if (appService == null)
			return;
		
		String packageName = statusBarNotification.getPackageName();
		AppModel appModel = appService.GetAppModel(packageName);
		if (appModel != null)
		{
			Notification notification = statusBarNotification.getNotification();
			if (notification != null)
			{
                NotificationInfo notificationInfo = NotificationInfoAdapter.GenerateNotificationInfo(statusBarNotification.getId(), notification, packageName);

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

				TileBase tile = appService.getTile(packageName);
				if (tile != null)
                {
                    this.TilesWithNotification.add(tile);
                    TileAnimationManager.Current().ImmediatelySlideAnimation(tile);
                }
			}
		}
	}

    public void RemoveNotification(StatusBarNotification statusBarNotification)
    {
        IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
        if (appService == null)
            return;

        String packageName = statusBarNotification.getPackageName();
        AppModel appModel = appService.GetAppModel(packageName);
        if (appModel != null)
        {
            Notification notification = statusBarNotification.getNotification();
            if (notification != null)
            {
                if (this.NotificationGroups.containsKey(appModel))
                {
                    NotificationGroup notificationGroup = this.NotificationGroups.get(appModel);
                    notificationGroup.Remove(statusBarNotification.getId());

                    if (notificationGroup.GetCount() == 0)
                        this.NotificationGroups.remove(notificationGroup);
                }

                TileBase tile = appService.getTile(packageName);
                if (tile != null)
                {
                    this.TilesWithNotification.remove(tile);
                    TileAnimationManager.Current().ImmediatelySlideAnimation(tile);
                }
            }
        }
    }

    public List<TileBase> GetTilesWithNotification()
    {
        return this.TilesWithNotification;
    }
}
