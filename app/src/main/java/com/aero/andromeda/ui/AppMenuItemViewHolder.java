package com.aero.andromeda.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aero.andromeda.R;

public class AppMenuItemViewHolder extends RecyclerView.ViewHolder
{
	private LinearLayout tileLayout;
	private FrameLayout tileBox;
	private TextView tileLabel;
	private ImageView tileIconImageView;
	private TextView headerCaption;
	
	public LinearLayout getTileLayout()
	{
		return tileLayout;
	}
	
	public FrameLayout getTileBox()
	{
		return tileBox;
	}
	
	public TextView getTileLabel()
	{
		return tileLabel;
	}
	
	public ImageView getTileIconImageView()
	{
		return tileIconImageView;
	}
	
	public TextView getHeaderCaption()
	{
		return headerCaption;
	}
	
	public AppMenuItemViewHolder(View itemView)
	{
		super(itemView);
		
		this.tileLayout = itemView.findViewById(R.id.menu_item_layout);
		this.tileBox = itemView.findViewById(R.id.menu_item_box);
		this.tileLabel = itemView.findViewById(R.id.menu_item_label);
		this.tileIconImageView = itemView.findViewById(R.id.menu_item_icon);
		
		this.headerCaption = itemView.findViewById(R.id.section_label);
	}
}
