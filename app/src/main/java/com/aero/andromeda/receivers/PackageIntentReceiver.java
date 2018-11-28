package com.aero.andromeda.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.aero.andromeda.managers.AppLoader;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;

public class PackageIntentReceiver extends BroadcastReceiver
{
	private AppLoader appLoader;
	
	public PackageIntentReceiver()
	{
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		if (appService == null)
			return;
		
		this.appLoader = appService.GetAppLoader();
		if (this.appLoader == null)
			return;
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		this.appLoader.getContext().registerReceiver(this, filter);
		
		// Register for events related to sdcard installation.
		IntentFilter sdFilter = new IntentFilter();
		sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
		sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
		this.appLoader.getContext().registerReceiver(this, sdFilter);
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		// Tell the loader about the change.
		this.appLoader.onContentChanged();
	}
}
