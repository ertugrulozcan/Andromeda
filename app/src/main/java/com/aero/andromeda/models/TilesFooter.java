package com.aero.andromeda.models;

import android.graphics.drawable.ColorDrawable;

import com.aero.andromeda.MainActivity;
import com.aero.andromeda.helpers.Colors;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.ui.BaseTileViewHolder;

public class TilesFooter extends TileBase
{
	private static long FOOTER_SINGLE_ID = 899999;
	
	public TilesFooter()
	{
		super(FOOTER_SINGLE_ID, TileType.TilesFooter, null);
		
		this.setTileColor(Colors.rgb("#09FFFFFF"));
	}
	
	@Override
	public void OnClick(BaseTileViewHolder holder)
	{
		MainActivity mainActivity = ServiceLocator.Current().GetInstance(MainActivity.class);
		mainActivity.SwipeToAppList();
	}
	
	@Override
	public boolean OnLongClick(BaseTileViewHolder holder)
	{
		return false;
	}
}
