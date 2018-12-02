package com.aero.andromeda.models;

import android.view.View;

import com.aero.andromeda.helpers.Colors;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.ui.BaseTileViewHolder;

public class TilesHeader extends TileBase
{
	private static long HEADTER_SINGLE_ID = 899999;
	
	public TilesHeader()
	{
		super(HEADTER_SINGLE_ID, TileType.TilesHeader, null);
		
		this.setTileColor(Colors.rgb("#01FFFFFF"));
	}
	
	@Override
	public void OnClick(BaseTileViewHolder holder)
	{
	
	}
	
	@Override
	public boolean OnLongClick(BaseTileViewHolder holder, View holdedView)
	{
		return false;
	}
}
