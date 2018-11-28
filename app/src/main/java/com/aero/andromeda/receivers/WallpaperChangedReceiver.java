package com.aero.andromeda.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aero.andromeda.MainActivity;

public class WallpaperChangedReceiver extends BroadcastReceiver
{
	final private MainActivity appDrawerActivity;
	
	public WallpaperChangedReceiver(MainActivity appDrawerActivity)
	{
		this.appDrawerActivity = appDrawerActivity;
	}
	
	@Override
	public void onReceive(Context context, Intent ıntent)
	{
		this.appDrawerActivity.RefreshWallpaper();
	}
}
