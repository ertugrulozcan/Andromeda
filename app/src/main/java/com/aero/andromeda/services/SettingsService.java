package com.aero.andromeda.services;

import com.aero.andromeda.AppDrawerFragment;
import com.aero.andromeda.MainActivity;
import com.aero.andromeda.helpers.SizeConverter;
import com.aero.andromeda.services.interfaces.ISettingsService;
import com.aero.andromeda.settings.UISettings;

public class SettingsService implements ISettingsService
{
	private UISettings uiSettings;
	
	public SettingsService()
	{
		this.uiSettings = new UISettings();
	}
	
	public UISettings getUISettings()
	{
		return this.uiSettings;
	}
	
	public void SetShowMoreTilesSwitch(boolean isChecked)
	{
		this.getUISettings().setShowMoreTiles(isChecked);
		
		SizeConverter.Init(ServiceLocator.Current().GetInstance(MainActivity.class));
		
		AppDrawerFragment appDrawerFragment = ServiceLocator.Current().GetInstance(AppDrawerFragment.class);
		appDrawerFragment.RefreshLayout(true);
	}
}
