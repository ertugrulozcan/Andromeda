package com.aero.andromeda.services.interfaces;

import com.aero.andromeda.settings.UISettings;

public interface ISettingsService
{
	UISettings getUISettings();
	
	void SetShowMoreTilesSwitch(boolean isChecked);
}
