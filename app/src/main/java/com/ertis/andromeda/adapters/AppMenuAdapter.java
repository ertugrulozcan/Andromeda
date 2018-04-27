package com.ertis.andromeda.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ertis.andromeda.R;
import com.ertis.andromeda.models.AppMenuItem;
import com.ertis.andromeda.models.Tile;

import java.util.List;

public class AppMenuAdapter extends RecyclerView.Adapter<AppMenuAdapter.AppMenuItemViewHolder>
{
	private Context parentView;
	private List<AppMenuItem> menuItemList;
	
	public AppMenuAdapter(Context context, List<AppMenuItem> menuItemList)
	{
		this.parentView = context;
		this.menuItemList = menuItemList;
	}
	
	@Override
	public AppMenuAdapter.AppMenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_menu_item, parent, false);
		return new AppMenuAdapter.AppMenuItemViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(AppMenuAdapter.AppMenuItemViewHolder holder, int position)
	{
		AppMenuItem menuItem = this.menuItemList.get(position);
		
		holder.tileLabel.setText(menuItem.getLabel());
		
		Drawable icon = menuItem.getIcon();
		if (icon != null)
			holder.tileIconImageView.setImageDrawable(icon);
		
		holder.tileIconImageView.requestLayout();
	}
	
	@Override
	public int getItemCount()
	{
		return this.menuItemList.size();
	}
	
	public class AppMenuItemViewHolder extends RecyclerView.ViewHolder
	{
		private LinearLayout tileLayout;
		private FrameLayout tileBox;
		private TextView tileLabel;
		private ImageView tileIconImageView;
		
		public AppMenuItemViewHolder(View itemView)
		{
			super(itemView);
			
			this.tileLayout = itemView.findViewById(R.id.menu_item_layout);
			this.tileBox = itemView.findViewById(R.id.menu_item_box);
			this.tileLabel = itemView.findViewById(R.id.menu_item_label);
			this.tileIconImageView = itemView.findViewById(R.id.menu_item_icon);
		}
	}
}
