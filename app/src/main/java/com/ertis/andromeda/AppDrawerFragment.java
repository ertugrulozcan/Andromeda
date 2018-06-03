package com.ertis.andromeda;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ertis.andromeda.adapters.TileTouchHelperCallback;
import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.listeners.OnStartDragListener;
import com.ertis.andromeda.listeners.TileClickListener;
import com.ertis.andromeda.managers.SpannedGridLayoutManager;
import com.ertis.andromeda.managers.TileAnimationManager;
import com.ertis.andromeda.managers.TileFolderManager;

public class AppDrawerFragment extends Fragment implements OnStartDragListener
{
	public static AppDrawerFragment Current;
	
	private RecyclerView recyclerView;
	private TilesAdapter tilesAdapter;
	private TileAnimationManager tileAnimationManager;
	private ItemTouchHelper itemTouchHelper;
	
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
		//this.tilesAdapter.setOnLongClickListener(tileClickListener);
		
		recyclerView.setAdapter(this.tilesAdapter);
		
		ItemTouchHelper.Callback callback = new TileTouchHelperCallback(tilesAdapter);
		this.itemTouchHelper = new ItemTouchHelper(callback);
		this.itemTouchHelper.attachToRecyclerView(recyclerView);
		
		this.tileAnimationManager = new TileAnimationManager(this.getActivity(), this.tilesAdapter);
		this.tileAnimationManager.Start();
		
		return view;
	}
	
	@Override
	public void onStartDrag(RecyclerView.ViewHolder viewHolder)
	{
		this.itemTouchHelper.startDrag(viewHolder);
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
