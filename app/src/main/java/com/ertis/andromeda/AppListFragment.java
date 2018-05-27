package com.ertis.andromeda;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.ertis.andromeda.adapters.AppMenuAdapter;
import com.ertis.andromeda.adapters.StickyHeadersLinearLayoutManager;
import com.ertis.andromeda.models.AppMenuItem;
import com.ertis.andromeda.models.AppModel;
import com.ertis.andromeda.models.Tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AppListFragment extends Fragment
{
	private FrameLayout baseLayout;
	private RecyclerView recyclerView;
	private AppMenuAdapter menuItemAdapter;
	private EditText searchTextBox;
	
	private boolean isEnabled = true;
	
	private static Typeface segoeTypeface;
	
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
		searchTextBox = (EditText) view.findViewById(R.id.searchTextBox);
		
		segoeTypeface = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/segoewp/segoe-wp.ttf");
		searchTextBox.setTypeface(segoeTypeface);
		
		/*
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
		recyclerView.setLayoutManager(linearLayoutManager);
		*/
		
		StickyHeadersLinearLayoutManager<AppMenuAdapter> layoutManager = new StickyHeadersLinearLayoutManager<>(view.getContext());
		recyclerView.setLayoutManager(layoutManager);
		
		this.menuItemAdapter.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (!isEnabled)
					return;

				AppMenuItem menuItem = menuItemAdapter.getDataContext(view);
				if (menuItem != null)
				{
					if (menuItem.isHeaderItem())
					{
						// Show jump letters fragment
						JumpLetterFragment jumpLetterFragment = JumpLetterFragment.newInstance(menuItemAdapter.getMenuItemList(), recyclerView);
						FragmentManager fm = getActivity().getSupportFragmentManager();
						fm.beginTransaction()
								.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
								.replace(R.id.contentLayout, jumpLetterFragment,"jumpLetterFragment")
								.addToBackStack("jumpLetterFragment")
								.commit();
					}
					else
					{
						AppModel app = menuItem.getApp();
						if (app != null)
						{
							startNewActivity(getActivity(), app.getApplicationPackageName());
						}
					}
				}
			}
		});
		
		//recyclerView.addItemDecoration(new AppListMenuItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
		recyclerView.setAdapter(this.menuItemAdapter);
		
		return view;
	}
	
	public void startNewActivity(Context context, String packageName)
	{
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		
		if (intent != null)
		{
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		else
		{
			// Bring user to the market or let them choose an app?
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=" + packageName));
		}
	}
	
	public void Enable()
	{
		this.isEnabled = true;
	}
	
	public void Disable()
	{
		this.isEnabled = false;
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
