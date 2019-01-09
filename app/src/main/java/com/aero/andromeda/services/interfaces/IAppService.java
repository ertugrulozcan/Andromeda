package com.aero.andromeda.services.interfaces;

import android.content.Context;

import com.aero.andromeda.adapters.AppListAdapter;
import com.aero.andromeda.adapters.TilesAdapter;
import com.aero.andromeda.managers.AppLoader;
import com.aero.andromeda.models.AppMenuItem;
import com.aero.andromeda.models.AppModel;
import com.aero.andromeda.models.IAppMenuItem;
import com.aero.andromeda.models.tiles.TileBase;

import java.util.List;

public interface IAppService
{
	AppLoader GetAppLoader();
	
	void StartApplication(AppModel appModel);
	
	void UninstallPackage(AppMenuItem appMenuItem);
	
	void PinToHome(AppMenuItem appMenuItem);
	
	void UnpinTile(TileBase tile);
	
	void UnpinTile(int index);
	
	Context getMainContext();
	
	List<IAppMenuItem> getMenuItemList();
	
	TileBase getTile(long id);
	
	List<TileBase> getTileList();
	
	AppListAdapter getAppListAdapter();
	
	void setAppListAdapter(AppListAdapter appListAdapter);
	
	TilesAdapter getTilesAdapter();
	
	void OnPackageRemoved(String packageName);
	
	void OnPackageInstalled(String packageName);
	
	void StartInstalledAppDetailsActivity(final Context context, String packageName);
}
