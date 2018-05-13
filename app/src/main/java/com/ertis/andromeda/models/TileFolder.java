package com.ertis.andromeda.models;

import com.ertis.andromeda.helpers.Colors;

public class TileFolder extends Tile
{
	public TileFolder(String folderName)
	{
		super(null, null, Colors.rgb(0x00000000));
		this.setCustomLabel(folderName);
	}
}
