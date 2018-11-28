package com.aero.andromeda.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aero.andromeda.R;
import com.aero.andromeda.models.AppMenuItem;
import com.aero.andromeda.models.IAppMenuItem;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.ISearchService;
import com.aero.andromeda.ui.AppMenuItemViewHolder;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<AppMenuItemViewHolder>
{
	private ISearchService searchService = null;
	
	private List<AppMenuItem> searchResultList;
	
	public SearchResultsAdapter()
	{
		this.searchService = ServiceLocator.Current().GetInstance(ISearchService.class);
		this.searchResultList = this.searchService.getSearchResultList();
	}
	
	@NonNull
	@Override
	public AppMenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
	{
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_menu_item, viewGroup, false);
		if (itemView == null)
			return null;
		
		//itemView.setOnClickListener(this.onClickListener);
		//itemView.setOnLongClickListener(this.onLongClickListener);
		
		return new AppMenuItemViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(@NonNull AppMenuItemViewHolder holder, int position)
	{
		if (holder == null)
			return;
		
		IAppMenuItem menuItem = this.searchResultList.get(position);
		menuItem.bindViewHolder(holder);
	}
	
	@Override
	public int getItemViewType(int position)
	{
		return super.getItemViewType(position);
	}
	
	@Override
	public int getItemCount()
	{
		return this.searchResultList.size();
	}
}
