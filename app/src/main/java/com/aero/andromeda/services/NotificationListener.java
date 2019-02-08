package com.aero.andromeda.services;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.aero.andromeda.services.interfaces.INotificationService;

public class NotificationListener extends NotificationListenerService
{
	private final IBinder binder = new ServiceBinder();
	private String TAG = this.getClass().getSimpleName();
	private boolean isBound = false;
	
	private NotificationReceiver notificationReceiver;
	
	public NotificationListener()
	{
	
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		isBound = true;
		String action = intent.getAction();

		// action.equals(NotificationService.NOTIFICATION_INTENT_ACTION_KEY)
		if (SERVICE_INTERFACE.equals(action))
		{
			return super.onBind(intent);
		}
		else
		{
			return binder;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid)
	{
		return START_STICKY;
	}
	
	public class ServiceBinder extends Binder
	{
		public NotificationListener getService()
		{
			return NotificationListener.this;
		}
	}

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void onListenerConnected()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            this.AllActiveNotifications();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void onListenerDisconnected()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            // Notification listener disconnected - requesting rebind
            requestRebind(new ComponentName(this, NotificationListenerService.class));
        }
    }
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		this.notificationReceiver = new NotificationReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(NotificationService.NOTIFICATION_SERVICE_LISTENER_KEY);
		registerReceiver(notificationReceiver, filter);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(notificationReceiver);
	}
	
	@Override
	public void onNotificationPosted(StatusBarNotification notification)
	{
		String packageName = notification.getPackageName();
		
		Intent i = new Intent(NotificationService.NOTIFICATION_INTENT_ACTION_KEY);
		i.putExtra(NotificationService.NOTIFICATION_EVENT_INTENT_KEY, "onNotificationPosted :" + packageName + "\n");
		sendBroadcast(i);
		
		INotificationService notificationService = ServiceLocator.Current().GetInstance(INotificationService.class);
		notificationService.AddNotification(notification);
	}
	
	@Override
	public void onNotificationRemoved(StatusBarNotification notification)
	{
	    Intent i = new Intent(NotificationService.NOTIFICATION_INTENT_ACTION_KEY);
		i.putExtra(NotificationService.NOTIFICATION_EVENT_INTENT_KEY, "onNotificationRemoved :" + notification.getPackageName() + "\n");
		
		sendBroadcast(i);

        INotificationService notificationService = ServiceLocator.Current().GetInstance(INotificationService.class);
        notificationService.RemoveNotification(notification);
	}

	private void AllActiveNotifications()
    {
        INotificationService notificationService = ServiceLocator.Current().GetInstance(INotificationService.class);

        StatusBarNotification[] notifications = NotificationListener.this.getActiveNotifications();
        if (notifications != null)
        {
            for (StatusBarNotification otherNotification : notifications)
            {
                notificationService.AddNotification(otherNotification);
            }
        }
    }
	
	class NotificationReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getStringExtra(NotificationService.NOTIFICATION_COMMAND_KEY).equals(NotificationService.CLEAR_NOTIFICATIONS_KEY))
			{
				NotificationListener.this.cancelAllNotifications();
			}
			else if (intent.getStringExtra(NotificationService.NOTIFICATION_COMMAND_KEY).equals(NotificationService.LIST_NOTIFICATIONS_KEY))
			{
				Intent i1 = new Intent(NotificationService.NOTIFICATION_INTENT_ACTION_KEY);
				i1.putExtra(NotificationService.NOTIFICATION_EVENT_INTENT_KEY, "=====================");
				sendBroadcast(i1);
				
				int i = 1;
                StatusBarNotification[] allActiveNotifications = NotificationListener.this.getActiveNotifications();
                if (allActiveNotifications != null)
                {
                    for (StatusBarNotification sbn : allActiveNotifications)
                    {
                        Intent i2 = new Intent(NotificationService.NOTIFICATION_INTENT_ACTION_KEY);
                        i2.putExtra(NotificationService.NOTIFICATION_EVENT_INTENT_KEY, i + " " + sbn.getPackageName() + "\n");
                        sendBroadcast(i2);

                        i++;
                    }
                }
				
				Intent i3 = new Intent(NotificationService.NOTIFICATION_INTENT_ACTION_KEY);
				i3.putExtra(NotificationService.NOTIFICATION_EVENT_INTENT_KEY, "===== Notification List ====");
				sendBroadcast(i3);
			}
		}
	}
}
