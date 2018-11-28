package com.aero.andromeda.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aero.andromeda.slideup.SlideUp;

public class ScreenLockReceiver extends BroadcastReceiver
{
	private final SlideUp slideUp;
	
	public ScreenLockReceiver(SlideUp slideUp)
	{
		this.slideUp = slideUp;
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String actionStr = intent.getAction().toString();
		
		if ("android.intent.action.SCREEN_ON".equals(intent.getAction()))
		{
		
		}
		else if ("android.intent.action.SCREEN_OFF".equals(intent.getAction()))
		{
			this.slideUp.show();
		}
	}
}
