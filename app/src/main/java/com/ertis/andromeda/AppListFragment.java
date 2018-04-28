package com.ertis.andromeda;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ertis.andromeda.adapters.AppMenuAdapter;
import com.ertis.andromeda.adapters.StickyHeadersLinearLayoutManager;

public class AppListFragment extends Fragment
{
	private FrameLayout baseLayout;
	private RecyclerView recyclerView;
	private AppMenuAdapter menuItemAdapter;
	
	public AppListFragment()
	{
		// Required empty public constructor
	}
	
	public static AppListFragment newInstance(AppMenuAdapter menuItemAdapter)
	{
		AppListFragment fragment = new AppListFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		fragment.menuItemAdapter = menuItemAdapter;
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_app_list, container, false);
		baseLayout = (FrameLayout) view.findViewById(R.id.app_list_fragment_base_layout);
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		
		/*
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
		recyclerView.setLayoutManager(linearLayoutManager);
		*/
		
		StickyHeadersLinearLayoutManager<AppMenuAdapter> layoutManager = new StickyHeadersLinearLayoutManager<>(view.getContext());
		recyclerView.setLayoutManager(layoutManager);
		
		//recyclerView.addItemDecoration(new AppListMenuItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
		recyclerView.setAdapter(this.menuItemAdapter);
		
		return view;
	}
	
	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
	}
	
	public void setBackgroundColor(int color)
	{
		if (this.baseLayout != null)
			this.baseLayout.setBackgroundColor(color);
	}
}
