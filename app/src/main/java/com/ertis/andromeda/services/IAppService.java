package com.ertis.andromeda.services;

import com.ertis.andromeda.adapters.AppMenuAdapter;
import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.managers.AppsLoader;
import com.ertis.andromeda.models.AppMenuItem;
import com.ertis.andromeda.models.AppModel;
import com.ertis.andromeda.models.Tile;

import java.util.List;

public interface IAppService
{
	AppsLoader GetAppLoader();
	
	List<AppModel> GetAppModelList();
	
	List<Tile> GetTileList();
	
	List<AppMenuItem> GetMenuItemList();
	
	TilesAdapter GetTilesAdapter();
	
	AppMenuAdapter GetMenuItemAdapter();
}
