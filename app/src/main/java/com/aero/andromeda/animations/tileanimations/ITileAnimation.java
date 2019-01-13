package com.aero.andromeda.animations.tileanimations;

import com.aero.andromeda.models.tiles.TileBase;

public interface ITileAnimation
{
	boolean IsEnabled();
	
	void Start(TileBase tile);
	
	void Stop();
}
