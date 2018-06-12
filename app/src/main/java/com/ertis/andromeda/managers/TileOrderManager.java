package com.ertis.andromeda.managers;

import com.ertis.andromeda.AppDrawerActivity;
import com.ertis.andromeda.AppDrawerFragment;
import com.ertis.andromeda.adapters.TilesAdapter;

public class TileOrderManager
{
	private final AppDrawerActivity appDrawerActivity;
	private final AppDrawerFragment appDrawerFragment;
	private final TilesAdapter tilesAdapter;
	
	private boolean isEditMode;
	
	public TileOrderManager(AppDrawerActivity appDrawerActivity, AppDrawerFragment appDrawerFragment, TilesAdapter tilesAdapter)
	{
		this.appDrawerActivity = appDrawerActivity;
		this.appDrawerFragment = appDrawerFragment;
		this.tilesAdapter = tilesAdapter;
	}
	
	public boolean isEditMode()
	{
		return isEditMode;
	}
	
	public void setEditMode(boolean editMode)
	{
		if (this.tilesAdapter == null)
			return;
		
		isEditMode = editMode;
		
		if (this.isEditMode && this.appDrawerFragment != null)
		{
			tilesAdapter.setDragStartListener(this.appDrawerFragment);
		}
		else
		{
			tilesAdapter.setDragStartListener(null);
		}
	}
}
