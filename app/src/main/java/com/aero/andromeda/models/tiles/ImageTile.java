package com.aero.andromeda.models.tiles;

import com.aero.andromeda.models.AppModel;

public class ImageTile extends Tile
{
	public ImageTile(final long id, AppModel app)
	{
		super(id, TileType.Image, app);
	}
}
