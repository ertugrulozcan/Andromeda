package com.aero.andromeda.adapters;

import android.app.Notification;

import com.aero.andromeda.models.NotificationInfo;

public class NotificationInfoAdapter
{
    public static NotificationInfo GenerateNotificationInfo(int id, Notification notification, String packageName)
    {
        if (notification == null)
            return null;

        String title = "";
        if (notification.tickerText != null)
            title = notification.tickerText.toString();

        String extraTitle = ExtractExtraTitle(notification);
        if (extraTitle != null)
            title = extraTitle;

        String extraText = ExtractExtraText(notification);
        if (extraText == null)
            extraText = "";

        NotificationInfo notificationInfo = new NotificationInfo(id, title, extraText);
        notificationInfo = SpecifiedNotificationInfoByApp(notificationInfo, packageName);

        return notificationInfo;
    }

    private static NotificationInfo SpecifiedNotificationInfoByApp(NotificationInfo notificationInfo, String packageName)
    {
        if (packageName.equals("com.spotify.music"))
        {
            return new NotificationInfo(notificationInfo.getId(), "Şu an çalınan", notificationInfo.getTitle() + " - " + notificationInfo.getMessage());
        }

        return notificationInfo;
    }

    private static String ExtractExtraTitle(Notification notification)
    {
        try
        {
            String extraTitle = notification.extras.getCharSequence(Notification.EXTRA_TITLE).toString();
            return extraTitle;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private static String ExtractExtraText(Notification notification)
    {
        try
        {
            String extraText = notification.extras.getCharSequence(Notification.EXTRA_TEXT).toString();
            return extraText;
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
