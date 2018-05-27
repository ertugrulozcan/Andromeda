package com.ertis.andromeda;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.listeners.TileClickListener;
import com.ertis.andromeda.managers.SpannedGridLayoutManager;
import com.ertis.andromeda.managers.TileAnimationManager;
import com.ertis.andromeda.managers.TileFolderManager;
import com.ertis.andromeda.models.AppModel;
import com.ertis.andromeda.models.FolderTile;
import com.ertis.andromeda.models.Tile;
import com.ertis.andromeda.models.TileFolder;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AppDrawerFragment extends Fragment
{
	public static AppDrawerFragment Current;
	
	private RecyclerView recyclerView;
	private TilesAdapter tilesAdapter;
	private TileAnimationManager tileAnimationManager;
	
	private boolean isEnabled = true;
	
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
		TileFolderManager.Current.setTilesAdapter(tilesAdapter);
		
		Current = fragment;
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
		if (this.tilesAdapter == null)
			return view;
		
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		
		SpannedGridLayoutManager spannedGridLayoutManager = new SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL, 6);
		spannedGridLayoutManager.setItemOrderIsStable(true);
		recyclerView.setLayoutManager(spannedGridLayoutManager);
		
		TileClickListener tileClickListener = GenerateTileClickListener(this.tilesAdapter);
		this.tilesAdapter.setOnClickListener(tileClickListener);
		this.tilesAdapter.setOnLongClickListener(tileClickListener);
		
		recyclerView.setAdapter(this.tilesAdapter);
		
		this.tileAnimationManager = new TileAnimationManager(this.getActivity(), this.tilesAdapter);
		this.tileAnimationManager.Start();
		
		return view;
	}
	
	public TileClickListener GenerateTileClickListener(TilesAdapter tilesAdapter)
	{
		return new TileClickListener(this, tilesAdapter);
	}
	
	public void Enable()
	{
		this.isEnabled = true;
		
		if (this.tileAnimationManager != null)
			this.tileAnimationManager.Start();
	}
	
	public void Disable()
	{
		this.isEnabled = false;
		
		if (this.tileAnimationManager != null)
			this.tileAnimationManager.Stop();
	}
	
	public boolean isEnabled()
	{
		return this.isEnabled;
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
