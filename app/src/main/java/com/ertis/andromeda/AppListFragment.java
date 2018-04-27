package com.ertis.andromeda;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ertis.andromeda.adapters.AppMenuAdapter;

public class AppListFragment extends Fragment
{
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
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
		recyclerView.setLayoutManager(linearLayoutManager);
		
		//recyclerView.addItemDecoration(new AppListMenuItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
		recyclerView.setAdapter(menuItemAdapter);
		
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
}
