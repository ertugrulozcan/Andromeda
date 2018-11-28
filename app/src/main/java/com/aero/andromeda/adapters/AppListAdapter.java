package com.aero.andromeda.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aero.andromeda.R;
import com.aero.andromeda.models.AppListHeaderItem;
import com.aero.andromeda.models.AppMenuItem;
import com.aero.andromeda.models.IAppMenuItem;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.ui.AppMenuItemViewHolder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppMenuItemViewHolder> implements StickyHeaders
{
	private static final int HEADER_ITEM_VIEWCODE = 123456;
	private IAppService appService = null;
	
	private List<IAppMenuItem> menuItemList;
	private HashMap<View, AppMenuItem> menuItemViewDictionary;
	
	public AppListAdapter()
	{
		this.appService = ServiceLocator.Current().GetInstance(IAppService.class);
		this.appService.setAppListAdapter(this);
		
		this.menuItemList = this.appService.getMenuItemList();
		this.menuItemViewDictionary = new LinkedHashMap<>();
	}
	
	@NonNull
	@Override
	public AppMenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
	{
		if (viewType == HEADER_ITEM_VIEWCODE)
		{
			View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_header, viewGroup, false);
			if (itemView == null)
				return null;
			
			return new AppMenuItemViewHolder(itemView);
		}
		else
		{
			View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_menu_item, viewGroup, false);
			if (itemView == null)
				return null;
			
			//itemView.setOnClickListener(this.onClickListener);
			//itemView.setOnLongClickListener(this.onLongClickListener);
			
			return new AppMenuItemViewHolder(itemView);
		}
	}
	
	@Override
	public void onBindViewHolder(@NonNull AppMenuItemViewHolder holder, int position)
	{
		if (holder == null)
			return;
		
		IAppMenuItem menuItem = this.menuItemList.get(position);
		
		if (holder.itemView != null && menuItem instanceof AppMenuItem)
			this.menuItemViewDictionary.put(holder.itemView, (AppMenuItem)menuItem);
		
		menuItem.bindViewHolder(holder);
	}
	
	@Override
	public int getItemViewType(int position)
	{
		if (this.isStickyHeader(position))
			return HEADER_ITEM_VIEWCODE;
		
		return super.getItemViewType(position);
	}
	
	@Override
	public int getItemCount()
	{
		return this.menuItemList.size();
	}
	
	@Override
	public boolean isStickyHeader(int position)
	{
		IAppMenuItem menuItem = this.menuItemList.get(position);
		boolean isHeaderItem = menuItem instanceof AppListHeaderItem;
		
		return isHeaderItem;
	}
	
	public AppMenuItem getDataContext(View view)
	{
		if (this.menuItemViewDictionary.containsKey(view))
			return this.menuItemViewDictionary.get(view);
		
		return null;
	}
	
	public AppMenuItem getDataContext(int position)
	{
		if (this.menuItemViewDictionary.size() < position && position >= 0)
			return this.menuItemViewDictionary.get(position);
		
		return null;
	}
}
