package com.ertis.andromeda.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ertis.andromeda.services.IAppService;
import com.ertis.andromeda.services.ServiceLocator;

public class ApplicationInstallationReceiver extends BroadcastReceiver
{
	private final IAppService appService;
	
	public ApplicationInstallationReceiver(IAppService appService)
	{
		this.appService = appService;
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (this.appService == null)
			return;
		
		String packageName = intent.getData().getEncodedSchemeSpecificPart();
		String actionStr = intent.getAction().toString();
		
		if (actionStr.equals("android.intent.action.PACKAGE_REMOVED"))
		{
			this.appService.OnPackageRemoved(packageName);
		}
		else
		{
			this.appService.OnPackageInstalled(packageName);
		}
	}
}
