package com.aero.andromeda.services;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aero.andromeda.AppDrawerFragment;
import com.aero.andromeda.R;
import com.aero.andromeda.adapters.AppListAdapter;
import com.aero.andromeda.adapters.TilesAdapter;
import com.aero.andromeda.managers.AppLoader;
import com.aero.andromeda.managers.TileListManager;
import com.aero.andromeda.models.AppListHeaderItem;
import com.aero.andromeda.models.AppMenuItem;
import com.aero.andromeda.models.AppModel;
import com.aero.andromeda.models.IAppMenuItem;
import com.aero.andromeda.models.tiles.IconTile;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.receivers.ApplicationInstallationReceiver;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.services.interfaces.INotificationService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AppService implements IAppService
{
	private final Context mainContext;
	private final AppLoader appLoader;
	
	private INotificationService notificationService;
	
	private TilesAdapter tilesAdapter;
	private AppListAdapter appListAdapter;
	
	private List<AppModel> appModelList;
	private List<IAppMenuItem> menuItemList;
	
	private List<TileBase> tileList = new ArrayList<>();
	
	private TileListManager tileListManager;
	
	public AppService(@NonNull Context context)
	{
		this.mainContext = context;
		this.appLoader = new AppLoader(context);
		
		this.appModelList = this.appLoader.loadInBackground();
		this.populateMenuItemList();
		
		this.RegisterPackageInstallReceiver();
		
		this.tileListManager = new TileListManager(this.mainContext, this.appModelList);
		
		this.tileList = this.tileListManager.GetTileList();
		this.tilesAdapter = new TilesAdapter(this.tileList, true, true);
		
		this.notificationService = new NotificationService();
		ServiceLocator.Current().RegisterInstance(this.notificationService);
	}
	
	public void StartApplication(AppModel appModel)
	{
		if (appModel == null)
			return;
		
		try
		{
			String packageName =  appModel.getApplicationPackageName();
			Intent intent = this.mainContext.getPackageManager().getLaunchIntentForPackage(packageName);
			
			if (intent != null)
			{
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				this.mainContext.startActivity(intent);
			}
			else
			{
				// Bring user to the market or let them choose an app?
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=" + packageName));
			}
		}
		catch (Exception ex)
		{
		
		}
	}
	
	public void UninstallPackage(AppMenuItem appMenuItem)
	{
		String packageName = appMenuItem.getAppModel().getApplicationPackageName();
		
		Intent intent = new Intent(Intent.ACTION_DELETE);
		intent.setData(Uri.parse("package:" + packageName));
		this.mainContext.startActivity(intent);
	}
	
	public void PinToHome(AppMenuItem appMenuItem)
	{
		for (int i = 0; i < this.tileList.size(); i++)
		{
			TileBase tile = this.tileList.get(i);
			AppModel app = tile.getApplication();
			if (app == null)
				continue;
			
			String appPackageName = app.getApplicationPackageName();
			
			AppModel app2 = appMenuItem.getAppModel();
			if (app2 == null)
				continue;
			
			String appPackageName2 = app2.getApplicationPackageName();
			
			if (appPackageName != null && appPackageName2 != null && appPackageName.equals(appPackageName2))
				return;
		}
		
		IconTile newTile = new IconTile(-9716, appMenuItem.getAppModel());
		newTile.setTileSize(TileBase.TileSize.Medium);
		newTile.setTileColor(new ColorDrawable(this.mainContext.getResources().getColor(R.color.colorTileBackground)));
		
		this.tileList.add(newTile);
		this.tilesAdapter.notifyItemInserted(this.tileList.size() - 1);
		
		AppDrawerFragment appDrawerFragment = ServiceLocator.Current().GetInstance(AppDrawerFragment.class);
		appDrawerFragment.ScrollToBottom();
	}
	
	public void UnpinTile(TileBase tile)
	{
		int index = this.tileList.indexOf(tile);
		this.tileList.remove(index);
		this.tilesAdapter.notifyItemRemoved(index);
	}
	
	public void UnpinTile(int index)
	{
		this.tileList.remove(index);
		this.tilesAdapter.notifyItemRemoved(index);
	}
	
	public void populateMenuItemList()
	{
		LinkedHashMap <Character, List<AppMenuItem>> groupedMenuItems = new LinkedHashMap<Character, List<AppMenuItem>>();
		
		for (AppModel appModel : this.appModelList)
		{
			char c = AppMenuItem.GetHeaderLetter(appModel);
			
			if (!groupedMenuItems.containsKey(c))
				groupedMenuItems.put(c, new ArrayList<AppMenuItem>());
			
			groupedMenuItems.get(c).add(new AppMenuItem(appModel));
		}
		
		ArrayList<IAppMenuItem> appMenuList = new ArrayList<>();
		
		List<Character> headerLetters = new ArrayList<>(groupedMenuItems.keySet());
		for (int i = 0; i < headerLetters.size(); i++)
		{
			char firstLetter = headerLetters.get(i);
			AppListHeaderItem headerItem = new AppListHeaderItem(firstLetter);
			appMenuList.add(headerItem);
			appMenuList.addAll(groupedMenuItems.get(firstLetter));
		}
		
		this.menuItemList = appMenuList;
	}
	
	public Context getMainContext()
	{
		return mainContext;
	}
	
	public void setTilesAdapter(TilesAdapter tilesAdapter)
	{
		this.tilesAdapter = tilesAdapter;
	}
	
	public TilesAdapter getTilesAdapter()
	{
		return this.tilesAdapter;
	}
	
	public TileBase getTile(long id)
	{
		for (TileBase tile : this.tileList)
		{
			if (tile.getId() == id)
				return tile;
		}
		
		return null;
	}
	
	public List<TileBase> getTileList()
	{
		return tileList;
	}
	
	@Override
	public AppLoader GetAppLoader()
	{
		return this.appLoader;
	}
	
	@Override
	public List<IAppMenuItem> getMenuItemList()
	{
		return this.menuItemList;
	}
	
	public AppListAdapter getAppListAdapter()
	{
		return this.appListAdapter;
	}
	
	public void setAppListAdapter(AppListAdapter appListAdapter)
	{
		this.appListAdapter = appListAdapter;
	}
	
	private void RegisterPackageInstallReceiver()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
		filter.addAction(Intent.ACTION_PACKAGE_INSTALL);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		filter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
		filter.addDataScheme("package");
		
		ApplicationInstallationReceiver applicationInstallationReceiver = new ApplicationInstallationReceiver(this);
		
		this.mainContext.registerReceiver(applicationInstallationReceiver, filter);
	}
	
	@Override
	public void OnPackageInstalled(String packageName)
	{
		try
		{
			// Add to AppModelList
			AppModel newApp = this.appLoader.GenerateAppModel(packageName);
			if (newApp == null)
				return;
			
			// Remove if any
			for (AppModel appModel : this.appModelList)
			{
				if (appModel.getApplicationPackageName().equalsIgnoreCase(newApp.getApplicationPackageName()))
				{
					this.appModelList.remove(appModel);
				}
			}
			
			int index = this.FindAlphabeticalIndex(this.appModelList, newApp);
			this.appModelList.add(index, newApp);
			
			// Add to MenuItemList
			int headerIndex =  this.findHeaderIndex(newApp);
			if (headerIndex >= 0)
			{
				boolean isInserted = false;
				int i = 0;
				for (i = headerIndex; i < this.menuItemList.size(); i++)
				{
					IAppMenuItem appMenuItem = this.menuItemList.get(i);
					if (appMenuItem instanceof AppMenuItem)
					{
						AppMenuItem menuItem = (AppMenuItem) appMenuItem;
						if (AppLoader.ALPHA_COMPARATOR.compare(newApp, menuItem.getAppModel()) < 0)
						{
							this.menuItemList.add(i, new AppMenuItem(newApp));
							
							synchronized(this.appListAdapter)
							{
								this.appListAdapter.notifyItemInserted(i);
							}
							
							isInserted = true;
							break;
						}
					}
				}
				
				if (!isInserted)
				{
					this.menuItemList.add(i, new AppMenuItem(newApp));
					
					synchronized(this.appListAdapter)
					{
						this.appListAdapter.notifyItemInserted(i);
					}
				}
			}
			else
			{
				for (int i = 0; i < this.menuItemList.size(); i++)
				{
					IAppMenuItem appMenuItem = this.menuItemList.get(i);
					char c = AppMenuItem.GetHeaderLetter(newApp);
					if (AppMenuItem.GetHeaderLetter(appMenuItem) > c)
					{
						this.menuItemList.add(i, new AppListHeaderItem(c));
						this.menuItemList.add(i + 1, new AppMenuItem(newApp));
						
						synchronized(this.appListAdapter)
						{
							this.appListAdapter.notifyItemRangeInserted(i, 2);
						}
						
						break;
					}
				}
			}
			
			synchronized(this.appListAdapter)
			{
				this.appListAdapter.notifyDataSetChanged();
			}
		}
		catch (Exception ex)
		{
			Log.e("OnPackageInstalled", ex.getStackTrace().toString());
		}
	}
	
	@Override
	public void OnPackageRemoved(String packageName)
	{
		AppModel appModel = this.findAppModelByPackageName(packageName);
		if (appModel != null)
		{
			int removedIndex = this.appModelList.indexOf(appModel);
			this.appModelList.remove(appModel);
			synchronized(this.appListAdapter)
			{
				this.appListAdapter.notifyItemRemoved(removedIndex);
			}
			
			AppMenuItem appMenuItem = findAppMenuItemByPackageName(packageName);
			if (appMenuItem != null)
			{
				removedIndex = this.menuItemList.indexOf(appMenuItem);
				this.menuItemList.remove(appMenuItem);
				synchronized(this.appListAdapter)
				{
					this.appListAdapter.notifyItemRemoved(removedIndex);
				}
				
				char c = AppMenuItem.GetHeaderLetter(appMenuItem);
				int remainedAppCount = this.getCountByLetter(c);
				if (remainedAppCount == 0)
				{
					AppListHeaderItem headerItem = this.findAppListHeaderItem(c);
					if (headerItem != null)
					{
						removedIndex = this.menuItemList.indexOf(headerItem);
						this.menuItemList.remove(headerItem);
						
						synchronized(this.appListAdapter)
						{
							this.appListAdapter.notifyItemRemoved(removedIndex);
						}
					}
				}
			}
			
			synchronized(this.appListAdapter)
			{
				this.appListAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private int getCountByLetter(char c)
	{
		int count = 0;
		for (IAppMenuItem appMenuItem : this.menuItemList)
		{
			if (appMenuItem instanceof AppMenuItem)
			{
				if (AppMenuItem.GetHeaderLetter(appMenuItem) == c)
					count++;
			}
		}
		
		return count;
	}
	
	private AppListHeaderItem findAppListHeaderItem(char c)
	{
		for (int i = 0; i < this.menuItemList.size(); i++)
		{
			IAppMenuItem appMenuItem = this.menuItemList.get(i);
			if (appMenuItem instanceof AppListHeaderItem)
			{
				AppListHeaderItem item = (AppListHeaderItem)appMenuItem;
				if (item.getHeader().equals(c + ""))
					return item;
			}
		}
		
		return null;
	}
	
	private AppModel findAppModelByPackageName(String packageName)
	{
		for (int i = 0; i < this.appModelList.size(); i++)
		{
			if (this.appModelList.get(i).getApplicationPackageName().equals(packageName))
				return this.appModelList.get(i);
		}
		
		return null;
	}
	
	private AppMenuItem findAppMenuItemByPackageName(String packageName)
	{
		for (int i = 0; i < this.menuItemList.size(); i++)
		{
			IAppMenuItem appMenuItem = this.menuItemList.get(i);
			if (appMenuItem instanceof AppMenuItem)
			{
				AppMenuItem item = (AppMenuItem)appMenuItem;
				if (item.getAppModel().getApplicationPackageName().equals(packageName))
					return item;
			}
		}
		
		return null;
	}
	
	private int findHeaderIndex(AppModel appModel)
	{
		char firstLetter = AppMenuItem.GetHeaderLetter(appModel);
		for (int i = 0; i < this.menuItemList.size(); i++)
		{
			IAppMenuItem appMenuItem = this.menuItemList.get(i);
			if (appMenuItem instanceof AppListHeaderItem)
			{
				if (appMenuItem.getHeader().equals(firstLetter + ""))
				{
					return i;
				}
			}
		}
		
		return -1;
	}
	
	private int FindAlphabeticalIndex(List<AppModel> appModelList, AppModel appModel)
	{
		for (int i = 0; i < appModelList.size(); i++)
		{
			AppModel pivotAppModel = appModelList.get(i);
			if (AppLoader.ALPHA_COMPARATOR.compare(appModel, pivotAppModel) < 0)
				return i;
		}
		
		return appModelList.size();
	}
	
	public void StartInstalledAppDetailsActivity(final Context context, String packageName)
	{
		if (context == null)
			return;
		
		final Intent i = new Intent();
		i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		i.setData(Uri.parse("package:" + packageName));
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(i);
	}
}
