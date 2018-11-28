package com.aero.andromeda.services;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.aero.andromeda.MainActivity;
import com.aero.andromeda.R;
import com.aero.andromeda.services.interfaces.INotificationService;

public class NotificationService extends NotificationListenerService implements INotificationService
{
	private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
	private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
	
	private static final class ApplicationPackageNames
	{
		public static final String FACEBOOK_PACK_NAME = "com.facebook.katana";
		public static final String FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca";
		public static final String WHATSAPP_PACK_NAME = "com.whatsapp";
		public static final String INSTAGRAM_PACK_NAME = "com.instagram.android";
	}
	
	public static final class InterceptedNotificationCode
	{
		public static final int FACEBOOK_CODE = 1;
		public static final int WHATSAPP_CODE = 2;
		public static final int INSTAGRAM_CODE = 3;
		public static final int OTHER_NOTIFICATIONS_CODE = 4; // We ignore all notification with code == 4
	}
	
	private Context parentContext;
	private AlertDialog enableNotificationListenerAlertDialog;
	
	public NotificationService()
	{
		this.parentContext = ServiceLocator.Current().GetInstance(MainActivity.class);
		
		if (!isNotificationServiceEnabled())
		{
			enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
			enableNotificationListenerAlertDialog.show();
		}
		
		StatusBarNotification[] activeNotifications = this.getActiveNotifications();
	}
	
	private boolean isNotificationServiceEnabled()
	{
		String pkgName = this.parentContext.getPackageName();
		
		final String flat = Settings.Secure.getString(this.parentContext.getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
		
		if (!TextUtils.isEmpty(flat))
		{
			final String[] names = flat.split(":");
			for (int i = 0; i < names.length; i++)
			{
				final ComponentName cn = ComponentName.unflattenFromString(names[i]);
				if (cn != null)
				{
					if (TextUtils.equals(pkgName, cn.getPackageName()))
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return super.onBind(intent);
	}
	
	@Override
	public void onNotificationPosted(StatusBarNotification sbn)
	{
		int notificationCode = matchNotificationCode(sbn);
		
		if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE)
		{
		
		}
	}
	
	@Override
	public void onNotificationRemoved(StatusBarNotification sbn)
	{
		int notificationCode = matchNotificationCode(sbn);
		
		if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE)
		{
			StatusBarNotification[] activeNotifications = this.getActiveNotifications();
			
			if (activeNotifications != null && activeNotifications.length > 0)
			{
				for (int i = 0; i < activeNotifications.length; i++)
				{
					if (notificationCode == matchNotificationCode(activeNotifications[i]))
					{
					
					}
				}
			}
		}
	}
	
	private int matchNotificationCode(StatusBarNotification sbn)
	{
		String packageName = sbn.getPackageName();
		
		if (packageName.equals(ApplicationPackageNames.FACEBOOK_PACK_NAME) || packageName.equals(ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME))
		{
			return (InterceptedNotificationCode.FACEBOOK_CODE);
		}
		else if (packageName.equals(ApplicationPackageNames.INSTAGRAM_PACK_NAME))
		{
			return (InterceptedNotificationCode.INSTAGRAM_CODE);
		}
		else if (packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME))
		{
			return (InterceptedNotificationCode.WHATSAPP_CODE);
		}
		else
		{
			return (InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
		}
	}
	
	private AlertDialog buildNotificationServiceAlertDialog()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.parentContext);
		alertDialogBuilder.setTitle(R.string.notification_listener_service);
		alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
		alertDialogBuilder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						parentContext.startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
					}
				});
		
		alertDialogBuilder.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						// If you choose to not enable the notification listener
						// the app. will not work as expected
					}
				});
		
		return(alertDialogBuilder.create());
	}
}
