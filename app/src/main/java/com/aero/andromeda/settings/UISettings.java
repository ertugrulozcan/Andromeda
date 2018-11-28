package com.aero.andromeda.settings;

public class UISettings
{
	private boolean showMoreTiles = false;
	
	public int getLayoutWidth()
	{
		if (this.showMoreTiles)
			return 8;
		else
			return 6;
	}
	
	public boolean isCheckedShowMoreTiles()
	{
		return this.showMoreTiles;
	}
	
	public void setShowMoreTiles(boolean showMoreTiles)
	{
		this.showMoreTiles = showMoreTiles;
	}
}
