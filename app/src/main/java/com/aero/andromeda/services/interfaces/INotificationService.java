package com.aero.andromeda.services.interfaces;

import android.service.notification.StatusBarNotification;

import com.aero.andromeda.models.NotificationGroup;
import com.aero.andromeda.models.tiles.TileBase;

public interface INotificationService
{
	void AddNotification(StatusBarNotification notification);

    void RemoveNotification(StatusBarNotification notification);
	
	NotificationGroup GetNotificationGroup(TileBase tile);
}
