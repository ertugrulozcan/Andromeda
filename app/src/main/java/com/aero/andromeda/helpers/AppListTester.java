package com.aero.andromeda.helpers;

import com.aero.andromeda.adapters.AppListAdapter;
import com.aero.andromeda.managers.AppLoader;
import com.aero.andromeda.models.AppListHeaderItem;
import com.aero.andromeda.models.AppMenuItem;
import com.aero.andromeda.models.IAppMenuItem;
import com.aero.andromeda.models.Result;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AppListTester
{
	public static Result<Object> RunTest()
	{
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		List<IAppMenuItem> appMenuItemList = appService.getMenuItemList();
		
		Result isValideted = Validate(appMenuItemList);
		boolean isDuplicate = isDuplicate(appMenuItemList);
		
		if (!isValideted.isSuccess())
			return isValideted;
		else if (isDuplicate)
			return new Result<>(false, "Duplicate items!");
		else
			return new Result<>(true);
	}
	
	public static Result<Object> Validate(final List<IAppMenuItem> menuItemList)
	{
		char pivotHeader = '?';
		final LinkedHashMap<Character, List<AppMenuItem>> groupedMenuItems = new LinkedHashMap<Character, List<AppMenuItem>>();
		
		for (IAppMenuItem menuItem : menuItemList)
		{
			if (menuItem instanceof AppListHeaderItem)
			{
				pivotHeader = AppMenuItem.GetHeaderLetter(menuItem);
				groupedMenuItems.put(pivotHeader, new ArrayList<AppMenuItem>());
			}
			else
			{
				// Is have to pivot header?
				if (AppMenuItem.GetHeaderLetter(menuItem) != pivotHeader)
					return new Result<>(false, "Header uyumsuz! (App : " + menuItem.getHeader() + ", Header : " + pivotHeader + ")");
				
				groupedMenuItems.get(pivotHeader).add((AppMenuItem)menuItem);
			}
		}
		
		return new Result<>(true);
	}
	
	public static boolean isOrdered(final LinkedHashMap<Character, List<AppMenuItem>> groupedMenuItems)
	{
		// Headers control
		Set<String> alphabeticalHeaderControlSet = new TreeSet<>();
		for (char header : groupedMenuItems.keySet())
		{
			alphabeticalHeaderControlSet.add(header + "");
		}
		
		List<Character> headerList = new ArrayList<>(groupedMenuItems.keySet());
		Object[] alphabeticalHeaderArray = alphabeticalHeaderControlSet.toArray();
		
		for (int i = 0; i < groupedMenuItems.size(); i++)
		{
			if (!headerList.get(i).toString().equals(alphabeticalHeaderArray[i].toString()))
				return false;
		}
		
		// Apps control
		for (Character c : headerList)
		{
			List<AppMenuItem> subList = groupedMenuItems.get(c);
			boolean isSubListOrdered = isOrdered(subList);
			if (!isSubListOrdered)
				return false;
		}
		
		return true;
	}
	
	public static boolean isOrdered(final List<AppMenuItem> appList)
	{
		if (appList.size() <= 1)
			return true;
		
		for (int i = 0; i < appList.size() - 1; i++)
		{
			AppMenuItem app1 = appList.get(i);
			AppMenuItem app2 = appList.get(i + 1);
			
			if (AppLoader.ALPHA_COMPARATOR.compare(app1.getAppModel(), app2.getAppModel()) > 0)
				return false;
		}
		
		return true;
	}
	
	public static boolean isDuplicate(final List<IAppMenuItem> appMenuItemList)
	{
		for (int i = 0; i < appMenuItemList.size(); i++)
		{
			IAppMenuItem appMenuItem = appMenuItemList.get(i);
			
			String packageName;
			if (appMenuItem instanceof AppListHeaderItem)
			{
				packageName = appMenuItem.getHeader();
			}
			else
			{
				packageName = ((AppMenuItem)appMenuItem).getAppModel().getApplicationPackageName();
			}
			
			for (int j = i + 1; j < appMenuItemList.size(); j++)
			{
				IAppMenuItem appMenuItem2 = appMenuItemList.get(j);
				
				String packageName2;
				if (appMenuItem2 instanceof AppListHeaderItem)
				{
					packageName2 = appMenuItem2.getHeader();
				}
				else
				{
					packageName2 = ((AppMenuItem)appMenuItem2).getAppModel().getApplicationPackageName();
				}
				
				if (packageName.equals(packageName2))
					return true;
			}
		}
		
		return false;
	}
}
