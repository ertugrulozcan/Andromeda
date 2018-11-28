package com.aero.andromeda.services;

import com.aero.andromeda.models.AppMenuItem;
import com.aero.andromeda.models.IAppMenuItem;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.services.interfaces.ISearchService;

import java.util.ArrayList;
import java.util.List;

public class SearchService implements ISearchService
{
	private final IAppService appService;
	
	private final List<IAppMenuItem> appMenuItemList;
	private final List<AppMenuItem> searchResultList;
	
	public SearchService(IAppService appService)
	{
		this.appService = appService;
		
		this.appMenuItemList = this.appService.getMenuItemList();
		this.searchResultList = new ArrayList<>();
	}
	
	public void search(String text)
	{
		this.searchResultList.clear();
	
		if (text.trim().equals(""))
			return;
		
		for (IAppMenuItem appMenuItem : this.appMenuItemList)
		{
			if (appMenuItem instanceof AppMenuItem)
			{
				if (appMenuItem.getHeader().toLowerCase().contains(text.toLowerCase()))
					this.searchResultList.add((AppMenuItem)appMenuItem);
			}
		}
	}
	
	public List<AppMenuItem> getSearchResultList()
	{
		return this.searchResultList;
	}
}
