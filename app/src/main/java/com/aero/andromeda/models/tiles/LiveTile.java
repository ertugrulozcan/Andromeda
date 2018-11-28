package com.aero.andromeda.models.tiles;

import com.aero.andromeda.models.AppModel;

public class LiveTile extends Tile
{
	public LiveTile(final long id, AppModel app)
	{
		super(id, TileType.LiveTile, app);
	}
}
