package com.aero.andromeda;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.ui.PreviewTileView;

public class EditTileFragment extends Fragment
{
	private TileBase editingTile;
	private PreviewTileView previewTileView;
	
	public EditTileFragment()
	{
	
	}
	
	public static EditTileFragment newInstance(TileBase editingTile)
	{
		EditTileFragment fragment = new EditTileFragment();
		fragment.editingTile = editingTile;
		
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
		View fragmentContainer = inflater.inflate(R.layout.fragment_edit_tile, container, false);
		
		if (this.editingTile != null)
		{
			TextView tileNameTextView = fragmentContainer.findViewById(R.id.editingTileNameTextView);
			tileNameTextView.setText(this.editingTile.getCaption());
			
			this.previewTileView = new PreviewTileView(fragmentContainer, this.editingTile);
		}
		
		return fragmentContainer;
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
