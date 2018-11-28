package com.aero.andromeda;

import android.app.Service;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aero.andromeda.helpers.AppListTester;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;

public class TestFragment extends Fragment
{
	private Button testButton;
	private Button appSettingsButton;
	
	public TestFragment()
	{
		// Required empty public constructor
	}
	
	public static TestFragment newInstance()
	{
		TestFragment fragment = new TestFragment();
		
		Bundle args = new Bundle();
		//args.putString(ARG_PARAM1, param1);
		//args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		
		ServiceLocator.Current().RegisterInstance(fragment);
		
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_test, container, false);
		
		this.testButton = view.findViewById(R.id.testButton);
		this.testButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppListTester.RunTest();
			}
		});
		
		this.appSettingsButton = view.findViewById(R.id.appSettingsButton);
		this.appSettingsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Kritik bir hata durumunda app settings'i acmak icin
				IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
				MainActivity mainActivity = ServiceLocator.Current().GetInstance(MainActivity.class);
				appService.StartInstalledAppDetailsActivity(mainActivity, mainActivity.getApplicationContext().getPackageName());
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
