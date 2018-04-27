package com.ertis.andromeda;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.managers.SpannedGridLayoutManager;

public class AppDrawerFragment extends Fragment
{
	private RecyclerView recyclerView;
	private TilesAdapter tilesAdapter;
	
	public AppDrawerFragment()
	{
		// Required empty public constructor
	}
	
	public static AppDrawerFragment newInstance(TilesAdapter tilesAdapter)
	{
		AppDrawerFragment fragment = new AppDrawerFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		fragment.tilesAdapter = tilesAdapter;
		
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
		View view = inflater.inflate(R.layout.fragment_app_drawer, container, false);
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		
		SpannedGridLayoutManager spannedGridLayoutManager = new SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL, 6);
		spannedGridLayoutManager.setItemOrderIsStable(true);
		recyclerView.setLayoutManager(spannedGridLayoutManager);
		
		recyclerView.setAdapter(tilesAdapter);
		
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
