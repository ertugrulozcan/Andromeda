package com.ertis.andromeda.models;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * Created by ertugrulozcan on 18.04.2018.
 */

public class AppModel
{
	private final Context mContext;
	private final ApplicationInfo mInfo;
	private final File mApkFile;
	private String mAppLabel;
	private Drawable mIcon;
	private boolean mMounted;
	
	public AppModel(Context context, ApplicationInfo info)
	{
		mContext = context;
		mInfo = info;
		
		mApkFile = new File(info.sourceDir);
	}
	
	public ApplicationInfo getAppInfo()
	{
		return mInfo;
	}
	
	public String getApplicationPackageName()
	{
		ApplicationInfo appInfo = getAppInfo();
		if (appInfo == null)
			return null;
		
		return appInfo.packageName;
	}
	
	public String getApplicationClassName()
	{
		ApplicationInfo appInfo = getAppInfo();
		if (appInfo == null)
			return null;
		
		return appInfo.className;
	}
	
	public String getLabel()
	{
		return mAppLabel;
	}
	
	public Drawable getIcon()
	{
		if (mIcon == null)
		{
			if (mApkFile.exists())
			{
				mIcon = mInfo.loadIcon(mContext.getPackageManager());
				return mIcon;
			}
			else
			{
				mMounted = false;
			}
		}
		else if (!mMounted)
		{
			// If the app wasn't mounted but is now mounted, reload
			// its icon.
			if (mApkFile.exists())
			{
				mMounted = true;
				mIcon = mInfo.loadIcon(mContext.getPackageManager());
				return mIcon;
			}
		}
		else
		{
			return mIcon;
		}
		
		return mContext.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
	}
	
	
	public void loadLabel(Context context)
	{
		if (mAppLabel == null || !mMounted)
		{
			if (!mApkFile.exists())
			{
				mMounted = false;
				mAppLabel = mInfo.packageName;
			}
			else
			{
				mMounted = true;
				CharSequence label = mInfo.loadLabel(context.getPackageManager());
				mAppLabel = label != null ? label.toString() : mInfo.packageName;
			}
		}
	}
}
