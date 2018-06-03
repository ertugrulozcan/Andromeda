package com.ertis.andromeda.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ertis.andromeda.AppDrawerActivity;

public class WallpaperChangedReceiver extends BroadcastReceiver
{
	final private AppDrawerActivity appDrawerActivity;
	
	public WallpaperChangedReceiver(AppDrawerActivity appDrawerActivity)
	{
		this.appDrawerActivity = appDrawerActivity;
	}
	
	@Override
	public void onReceive(Context context, Intent ıntent)
	{
		this.appDrawerActivity.RefreshWallpaper();
	}
}
