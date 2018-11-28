package com.aero.andromeda.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.aero.andromeda.R;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;

public class TilesFooterViewHolder extends BaseTileViewHolder
{
	protected View itemView;
	
	public TilesFooterViewHolder(@NonNull View itemView)
	{
		super(itemView);
		this.itemView = itemView;
		
		FrameLayout layoutBase = itemView.findViewById(R.id.layoutBase);
		
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		Context context = appService.getMainContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View allAppsButton = inflater.inflate(R.layout.all_apps_button, null);
		layoutBase.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		layoutBase.addView(allAppsButton);
	}
	
	@Override
	protected void setLayoutProperties(TileBase tile)
	{
	
	}
}
