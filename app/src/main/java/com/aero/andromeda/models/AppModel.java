package com.aero.andromeda.models;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.ui.BaseTileViewHolder;

import java.io.File;

public class AppModel
{
	private final Context appContext;
	private final ApplicationInfo applicationInfo;
	private final File apkFile;
	private String appLabel;
	private Drawable icon;
	private boolean isMounted;
	
	public AppModel(Context context, ApplicationInfo info)
	{
		this.appContext = context;
		this.applicationInfo = info;
		
		this.apkFile = new File(info.sourceDir);
	}
	
	public String getApplicationPackageName()
	{
		if (this.applicationInfo == null)
			return null;
		
		return this.applicationInfo.packageName;
	}
	
	public String getApplicationClassName()
	{
		if (this.applicationInfo == null)
			return null;
		
		return this.applicationInfo.className;
	}
	
	public Context getAppContext()
	{
		return appContext;
	}
	
	public ApplicationInfo getApplicationInfo()
	{
		return applicationInfo;
	}
	
	public File getApkFile()
	{
		return apkFile;
	}
	
	public String getAppLabel()
	{
		return appLabel;
	}
	
	public void setAppLabel(String appLabel)
	{
		this.appLabel = appLabel;
	}
	
	public Drawable getIcon()
	{
		if (this.icon == null)
		{
			if (this.apkFile.exists())
			{
				this.icon = this.applicationInfo.loadIcon(this.appContext.getPackageManager());
				return this.icon;
			}
			else
			{
				this.isMounted = false;
			}
		}
		else if (!this.isMounted)
		{
			// If the app wasn't mounted but is now mounted, reload
			// its icon.
			if (this.apkFile.exists())
			{
				this.isMounted = true;
				this.icon = this.applicationInfo.loadIcon(this.appContext.getPackageManager());
				return this.icon;
			}
		}
		else
		{
			return this.icon;
		}
		
		return this.appContext.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
	}
	
	public void setIcon(Drawable icon)
	{
		this.icon = icon;
	}
	
	public boolean isMounted()
	{
		return isMounted;
	}
	
	public void setMounted(boolean mounted)
	{
		isMounted = mounted;
	}
	
	public void loadLabel(Context context)
	{
		if (this.appLabel == null || !this.isMounted)
		{
			if (!this.apkFile.exists())
			{
				this.isMounted = false;
				this.appLabel = this.applicationInfo.packageName;
			}
			else
			{
				this.isMounted = true;
				CharSequence label = this.applicationInfo.loadLabel(context.getPackageManager());
				this.appLabel = label != null ? label.toString() : this.applicationInfo.packageName;
			}
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		
		if (obj instanceof AppModel)
		{
			AppModel other = (AppModel)obj;
			if (other.applicationInfo != null && this.applicationInfo != null)
			{
				return other.applicationInfo.equals(this.applicationInfo);
			}
			else
			{
				String packageName = this.getApplicationPackageName();
				String otherPackageName = other.getApplicationPackageName();
				
				if (packageName != null && otherPackageName != null && !packageName.isEmpty() && !otherPackageName.isEmpty())
					return packageName.equals(otherPackageName);
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		if (this.applicationInfo != null)
		{
			return this.applicationInfo.hashCode() * 7;
		}
		else
		{
			String packageName = this.getApplicationPackageName();
			
			if (packageName != null && !packageName.isEmpty())
				return packageName.hashCode() * 7;
		}
		
		return super.hashCode();
	}
}
