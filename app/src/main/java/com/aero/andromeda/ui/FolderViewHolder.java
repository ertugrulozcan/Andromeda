package com.aero.andromeda.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aero.andromeda.R;
import com.aero.andromeda.helpers.SizeConverter;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.ISettingsService;
import com.aero.andromeda.settings.UISettings;

public class FolderViewHolder extends BaseTileViewHolder
{
	private TextView tileLabel;
	private LinearLayout folderBorder;
	private RecyclerView recyclerView;
	
	public FolderViewHolder(@NonNull View itemView)
	{
		super(itemView);
		
		this.tileLabel = itemView.findViewById(R.id.tileLabel);
		this.folderBorder = itemView.findViewById(R.id.folderBorder);
		this.recyclerView = itemView.findViewById(R.id.folder_recycler_view);
	}
	
	@Override
	protected void setLayoutProperties(TileBase tile)
	{
		ISettingsService settingsService = ServiceLocator.Current().GetInstance(ISettingsService.class);
		UISettings uiSettings = settingsService.getUISettings();
		
		int deviceHeight = SizeConverter.Current.GetDeviceResolution().heightPixels;
		int borderLayoutBottomMargin = (int)(deviceHeight * 0.0166f);
		int recyclerTopMargin = (int)(deviceHeight * 0.0231f);
		
		if (uiSettings.isCheckedShowMoreTiles())
		{
			borderLayoutBottomMargin = (int)(deviceHeight * 0.0111f);
			recyclerTopMargin = (int)(deviceHeight * 0.0148f);
		}
		
		FrameLayout.LayoutParams borderLayoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		borderLayoutParams.setMargins(0, 0, 0, borderLayoutBottomMargin);
		this.folderBorder.setLayoutParams(borderLayoutParams);
		
		FrameLayout.LayoutParams recyclerLayoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		recyclerLayoutParams.setMargins(0, recyclerTopMargin, 0, 0);
		this.recyclerView.setLayoutParams(recyclerLayoutParams);
		
		this.tileLabel.clearAnimation();
		this.tileLabel.setText(tile.getCaption());
	}
	
	public RecyclerView getRecyclerView()
	{
		return recyclerView;
	}
}
