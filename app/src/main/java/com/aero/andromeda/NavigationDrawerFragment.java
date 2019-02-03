package com.aero.andromeda;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aero.andromeda.helpers.AppListTester;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;

public class NavigationDrawerFragment extends Fragment
{
	private Button appSettingsButton;
	
	public NavigationDrawerFragment()
	{
	
	}
	
	public static NavigationDrawerFragment newInstance(String param1, String param2)
	{
		NavigationDrawerFragment fragment = new NavigationDrawerFragment();
		Bundle args = new Bundle();
		
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
		
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
		
		this.appSettingsButton = view.findViewById(R.id.nav_settings);
		this.appSettingsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MainActivity mainActivity = (MainActivity)NavigationDrawerFragment.this.getActivity();
				final Intent i = new Intent(mainActivity, SettingsActivity.class);
				startActivity(i);
				mainActivity.FlagAsNeedRestart();
				mainActivity.CloseNavigationDrawer();
			}
		});
		
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
