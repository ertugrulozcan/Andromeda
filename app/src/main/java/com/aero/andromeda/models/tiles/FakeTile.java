package com.aero.andromeda.models.tiles;

import com.aero.andromeda.helpers.Colors;

public class FakeTile extends Tile
{
	public FakeTile(final long id)
	{
		super(id, TileType.Icon, null);
		this.setTileSize(TileSize.Small);
		this.setTileColor(Colors.TRANSPARENT);
	}
}
