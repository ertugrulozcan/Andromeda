package com.aero.andromeda.helpers;

import android.support.v7.widget.RecyclerView;

import com.aero.andromeda.managers.TileOrderManager;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;

public class TilesDrawingOrderCallback implements RecyclerView.ChildDrawingOrderCallback
{
	@Override
	public int onGetChildDrawingOrder(int index, int i1)
	{
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		TileBase tile = appService.getTilesAdapter().getItem(index);
		if (tile != null)
		{
		
		}
		
		return 0;
	}
}
