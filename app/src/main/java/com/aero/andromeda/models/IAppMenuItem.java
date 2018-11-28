package com.aero.andromeda.models;

import com.aero.andromeda.ui.AppMenuItemViewHolder;

public interface IAppMenuItem
{
	void bindViewHolder(AppMenuItemViewHolder viewHolder);
	
	String getHeader();
}
