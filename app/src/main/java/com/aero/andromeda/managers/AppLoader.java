package com.aero.andromeda.managers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.aero.andromeda.models.AppModel;
import com.aero.andromeda.receivers.PackageIntentReceiver;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppLoader extends AsyncTaskLoader<ArrayList<AppModel>>
{
	private final PackageManager packageManager;
	
	private ArrayList<AppModel> installedApps;
	private PackageIntentReceiver packageIntentReceiver;
	
	public AppLoader(@NonNull Context context)
	{
		super(context);
		
		this.packageManager = context.getPackageManager();
	}
	
	@Nullable
	@Override
	public ArrayList<AppModel> loadInBackground()
	{
		Intent indent = new Intent(Intent.ACTION_MAIN, null);
		indent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		final Context context = getContext();
		return this.loadApplications(context);
	}
	
	public static final Comparator<AppModel> ALPHA_COMPARATOR = new Comparator<AppModel>()
	{
		// a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
		private final Collator sCollator = Collator.getInstance();
		
		@Override
		public int compare(AppModel object1, AppModel object2)
		{
			if (object1 == null || object2 == null)
				return 0;
			
			return sCollator.compare(object1.getAppLabel(), object2.getAppLabel());
		}
	};
	
	private ArrayList<AppModel> loadApplications(Context context)
	{
		// retrieve the list of installed applications
		List<ApplicationInfo> apps = this.packageManager.getInstalledApplications(0);
		
		if (apps == null)
		{
			apps = new ArrayList<ApplicationInfo>();
		}
		
		// create corresponding apps and load their labels
		ArrayList<AppModel> items = new ArrayList<AppModel>(apps.size());
		for (int i = 0; i < apps.size(); i++)
		{
			String pkg = apps.get(i).packageName;
			
			// only apps which are launchable
			if (context.getPackageManager().getLaunchIntentForPackage(pkg) != null)
			{
				AppModel app = new AppModel(context, apps.get(i));
				app.loadLabel(context);
				items.add(app);
			}
		}
		
		// sort the list
		Collections.sort(items, ALPHA_COMPARATOR);
		
		return items;
	}
	
	public AppModel GenerateAppModel(String packageName)
	{
		try
		{
			ApplicationInfo applicationInfo = this.packageManager.getApplicationInfo(packageName, 0);
			final Context context = getContext();
			
			if (context.getPackageManager().getLaunchIntentForPackage(packageName) != null)
			{
				AppModel app = new AppModel(context, applicationInfo);
				app.loadLabel(context);
				
				return app;
			}
		}
		catch (PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public void deliverResult(ArrayList<AppModel> apps)
	{
		if (isReset())
		{
			// An async query came in while the loader is stopped.  We
			// don't need the result.
			if (apps != null)
			{
				onReleaseResources(apps);
			}
		}
		
		ArrayList<AppModel> oldApps = apps;
		this.installedApps = apps;
		
		if (isStarted())
		{
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(apps);
		}
		
		// At this point we can release the resources associated with
		// 'oldApps' if needed; now that the new result is delivered we
		// know that it is no longer in use.
		if (oldApps != null)
		{
			onReleaseResources(oldApps);
		}
	}
	
	@Override
	protected void onStartLoading()
	{
		if (this.installedApps != null)
		{
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(this.installedApps);
		}
		
		// watch for changes in app install and uninstall operation
		if (this.packageIntentReceiver == null)
		{
			this.packageIntentReceiver = new PackageIntentReceiver();
		}
		
		if (takeContentChanged() || this.installedApps == null)
		{
			// If the data has changed since the last time it was loaded
			// or is not currently available, start a load.
			forceLoad();
		}
	}
	
	@Override
	protected void onStopLoading()
	{
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}
	
	@Override
	public void onCanceled(ArrayList<AppModel> apps)
	{
		super.onCanceled(apps);
		
		// At this point we can release the resources associated with 'apps'
		// if needed.
		onReleaseResources(apps);
	}
	
	@Override
	protected void onReset()
	{
		try
		{
			// Ensure the loader is stopped
			onStopLoading();
			
			// At this point we can release the resources associated with 'apps'
			// if needed.
			if (this.installedApps != null)
			{
				onReleaseResources(this.installedApps);
				this.installedApps = null;
			}
			
			// Stop monitoring for changes.
			if (this.packageIntentReceiver != null)
			{
				getContext().unregisterReceiver(this.packageIntentReceiver);
				this.packageIntentReceiver = null;
			}
		}
		catch (Exception ex)
		{
			Log.e("AppsLoader.onReset", ex.getMessage());
		}
	}
	
	/**
	 * Helper method to do the cleanup work if needed, for example if we're
	 * using Cursor, then we should be closing it here
	 *
	 * @param apps
	 */
	protected void onReleaseResources(ArrayList<AppModel> apps)
	{
		// do nothing
	}
}
