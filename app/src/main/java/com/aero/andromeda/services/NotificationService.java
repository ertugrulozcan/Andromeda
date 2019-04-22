package com.aero.andromeda.services;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearSmoothScroller;
import android.widget.Toast;

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
import java.util.Set;

public class NotificationService implements INotificationService
{
	public static String NOTIFICATION_SERVICE_LISTENER_KEY = "com.aero.andromeda.services.TILE_NOTIFICATION_LISTENER_SERVICE";
	public static String NOTIFICATION_INTENT_ACTION_KEY = "com.aero.andromeda.services.TILE_NOTIFICATION_LISTENER";
	public static String NOTIFICATION_EVENT_INTENT_KEY = "notification_event";
	public static String NOTIFICATION_COMMAND_KEY = "command";
	public static String LIST_NOTIFICATIONS_KEY = "list";
	public static String CLEAR_NOTIFICATIONS_KEY = "clearAll";

    private final int REQUEST_PERMISSION_PHONE_STATE = 1;
	
	private HashMap<AppModel, NotificationGroup> NotificationGroups;

    private List<TileBase> TilesWithNotification;
	
	public NotificationService()
	{
		this.NotificationGroups = new LinkedHashMap<>();
		this.TilesWithNotification = new ArrayList<>();

		this.CheckNotificationAccessPermission();
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
        NotificationListener notificationListener = NotificationListener.Current();
        if (notificationListener != null)
            notificationListener.CheckForNotifications();

        return this.TilesWithNotification;
    }

    // Check permissions

    private void CheckNotificationAccessPermission()
    {
        IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
        Context mainActivityContext = appService.getMainContext();

        Set<String> enabledListenerPackages = NotificationManagerCompat.getEnabledListenerPackages(mainActivityContext);
        boolean isAuthorizedForNotificationAccess = enabledListenerPackages.contains(mainActivityContext.getPackageName());

        if (!isAuthorizedForNotificationAccess)
        {
            this.RequestPermissionForNotificationAccess((Activity)mainActivityContext, "İzin gerekli", "Canlı kutucukları ve bildirimleri kullanabilmeniz için Ayarlar>Bildirim Erişimi>Andromeda'ya gidip izin vermeniz gerekli.", Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE, REQUEST_PERMISSION_PHONE_STATE);
        }
    }

    private void RequestPermissionForNotificationAccess(final Activity activity, String title, String message, final String permission, final int permissionRequestCode)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(title).setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                ActivityCompat.requestPermissions(activity, new String[] { permission }, permissionRequestCode);
                activity.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        })
        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {

            }
        });

        builder.create().show();
    }
}
