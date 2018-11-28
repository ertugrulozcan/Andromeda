package com.aero.andromeda;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.aero.andromeda.adapters.AppListAdapter;
import com.aero.andromeda.adapters.SearchResultsAdapter;
import com.aero.andromeda.adapters.StickyHeadersLinearLayoutManager;
import com.aero.andromeda.adapters.TilesAdapter;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.services.interfaces.ISearchService;

public class AppListFragment extends Fragment
{
	private boolean isEnabled = true;
	
	private AppListAdapter appListAdapter;
	private SearchResultsAdapter searchResultsAdapter;
	
	private FrameLayout baseLayout;
	private RecyclerView recyclerView;
	private EditText searchTextBox;
	
	public AppListFragment()
	{
		// Required empty public constructor
	}
	
	public static AppListFragment newInstance()
	{
		AppListFragment fragment = new AppListFragment();
		
		Bundle args = new Bundle();
		//args.putString(ARG_PARAM1, param1);
		//args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		
		ServiceLocator.Current().RegisterInstance(fragment);
		
		fragment.appListAdapter = new AppListAdapter();
		fragment.searchResultsAdapter = new SearchResultsAdapter();
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			//mParam1 = getArguments().getString(ARG_PARAM1);
			//mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_app_list, container, false);
		
		this.baseLayout = (FrameLayout) view.findViewById(R.id.app_list_fragment_base_layout);
		this.recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		this.searchTextBox = (EditText) view.findViewById(R.id.searchTextBox);
		
		StickyHeadersLinearLayoutManager<AppListAdapter> layoutManager = new StickyHeadersLinearLayoutManager<>(view.getContext());
		this.recyclerView.setLayoutManager(layoutManager);
		this.recyclerView.setAdapter(this.appListAdapter);
		
		this.setSearchBox();
		
		synchronized (this.appListAdapter)
		{
			this.appListAdapter.notifyAll();
			this.ScrollToBottom();
			this.ScrollToTop();
		}
		
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
	
	public void Enable()
	{
		this.isEnabled = true;
	}
	
	public void Disable()
	{
		this.isEnabled = false;
	}
	
	public boolean isEnabled()
	{
		return this.isEnabled;
	}
	
	public RecyclerView getRecyclerView()
	{
		return recyclerView;
	}
	
	private void setSearchBox()
	{
		this.searchTextBox.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				ISearchService searchService = ServiceLocator.Current().GetInstance(ISearchService.class);
				searchService.search(s.toString());
				assignListAdapter(true);
			}
		});
		
		this.searchTextBox.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				assignListAdapter(hasFocus);
			}
		});
	}
	
	private void assignListAdapter(boolean searchBoxHasFocused)
	{
		if (searchBoxHasFocused && !searchTextBox.getText().toString().trim().equals(""))
		{
			recyclerView.setAdapter(searchResultsAdapter);
		}
		else
		{
			recyclerView.setAdapter(appListAdapter);
		}
	}
	
	public void ScrollToTop()
	{
		this.recyclerView.smoothScrollToPosition(0);
	}
	
	public void ScrollToBottom()
	{
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		AppListAdapter appListAdapter = appService.getAppListAdapter();
		this.recyclerView.smoothScrollToPosition(appListAdapter.getItemCount() - 1);
	}
}
