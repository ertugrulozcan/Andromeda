package com.aero.andromeda.services.interfaces;

import com.aero.andromeda.models.NotificationInfo;
import com.aero.andromeda.models.tiles.TileBase;

public interface INotificationService
{
	NotificationInfo GetNotificationInfo(TileBase tile);
}
