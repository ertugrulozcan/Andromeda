package com.ertis.andromeda.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class AppMenuAdapter extends RecyclerView.Adapter<AppMenuAdapter.AppMenuItemViewHolder> implements StickyHeaders
{
	private Context parentView;
	private List<AppMenuItem> menuItemList;
	private HashMap<View, AppMenuItem> menuItemViewDictionary;
	
	private static final int HEADER_ITEM = 123;
	
	private static Typeface segoeTypeface;
	
	private View.OnClickListener onClickListener;
	
	public AppMenuAdapter(Context context, List<AppMenuItem> menuItemList)
	{
		this.parentView = context;
		this.menuItemList = menuItemList;
		this.menuItemViewDictionary = new LinkedHashMap<>();
		
		segoeTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/segoewp/segoe-wp-light.ttf");
	}
	
	@Override
	public AppMenuAdapter.AppMenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		if (viewType == HEADER_ITEM)
		{
			View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header, parent, false);
			itemView.setOnClickListener(this.onClickListener);
			return new AppMenuAdapter.AppMenuItemViewHolder(itemView);
		}
		else
		{
			View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_menu_item, parent, false);
			itemView.setOnClickListener(this.onClickListener);
			return new AppMenuAdapter.AppMenuItemViewHolder(itemView);
		}
	}
	
	@Override
	public void onBindViewHolder(AppMenuAdapter.AppMenuItemViewHolder holder, int position)
	{
		int viewType = getItemViewType(position);
		AppMenuItem menuItem = this.menuItemList.get(position);
		
		if (holder.itemView != null)
		{
			this.menuItemViewDictionary.put(holder.itemView, menuItem);
		}
		
		if (viewType == HEADER_ITEM)
		{
			holder.headerCaption.setText(menuItem.getHeader());
			holder.headerCaption.setTypeface(segoeTypeface);
		}
		else
		{
			holder.tileLabel.setText(menuItem.getLabel());
			holder.tileLabel.setTypeface(segoeTypeface);
			
			Drawable icon = menuItem.getIcon();
			if (icon != null)
				holder.tileIconImageView.setImageDrawable(icon);
			
			holder.tileIconImageView.requestLayout();
		}
	}
	
	public void setOnClickListener(View.OnClickListener onClickListener)
	{
		this.onClickListener = onClickListener;
	}
	
	public List<AppMenuItem> getMenuItemList()
	{
		return menuItemList;
	}
	
	public AppMenuItem getDataContext(View view)
	{
		if (this.menuItemViewDictionary.containsKey(view))
			return this.menuItemViewDictionary.get(view);
		
		return null;
	}
	
	@Override
	public int getItemCount()
	{
		return this.menuItemList.size();
	}
	
	@Override
	public int getItemViewType(int position)
	{
		AppMenuItem menuItem = this.menuItemList.get(position);
		return menuItem.isHeaderItem() ? HEADER_ITEM : super.getItemViewType(position);
	}
	
	@Override
	public boolean isStickyHeader(int position)
	{
		AppMenuItem menuItem = this.menuItemList.get(position);
		return menuItem.isHeaderItem();
	}
	
	public class AppMenuItemViewHolder extends RecyclerView.ViewHolder
	{
		private LinearLayout tileLayout;
		private FrameLayout tileBox;
		private TextView tileLabel;
		private ImageView tileIconImageView;
		
		private TextView headerCaption;
		
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
}
