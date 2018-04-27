package com.ertis.andromeda;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.ertis.andromeda.adapters.AppListMenuItemDecoration;
import com.ertis.andromeda.adapters.AppMenuAdapter;
import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.managers.AppsLoader;
import com.ertis.andromeda.models.AppMenuItem;
import com.ertis.andromeda.models.AppModel;
import com.ertis.andromeda.models.Tile;
import com.ertis.andromeda.utilities.TypefaceUtil;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<ArrayList<AppModel>>
{
	private List<AppMenuItem> menuItemList = new ArrayList<>();
	private RecyclerView recyclerView;
	private AppMenuAdapter menuItemAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_list);
		
		TypefaceUtil.overrideFont(getApplicationContext(), "SANS", "fonts/segoeui.ttf");
		
		recyclerView = (RecyclerView) findViewById(R.id.app_list_recycler_view);
		
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(linearLayoutManager);
		
		menuItemAdapter = new AppMenuAdapter(this, menuItemList);
		//recyclerView.addItemDecoration(new AppListMenuItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
		recyclerView.setAdapter(menuItemAdapter);
		
		// create the loader to load the apps list in background
		getLoaderManager().initLoader(0, null, this);
		
		// FullScreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}
	
	private void loadMenuItemList(ArrayList<AppModel> appList)
	{
		if (appList == null)
		{
			this.menuItemList.clear();
			this.menuItemAdapter.notifyDataSetChanged();
			return;
		}
		
		// appList.size()
		for (int i = 0; i < appList.size(); i++)
		{
			AppModel application = appList.get(i);
			menuItemList.add(new AppMenuItem(application));
		}
		
		this.menuItemAdapter.notifyDataSetChanged();
	}
	
	@Override
	public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle args)
	{
		return new AppsLoader(this);
	}
	
	@Override
	public void onLoadFinished(Loader<ArrayList<AppModel>> loader, ArrayList<AppModel> data)
	{
		this.loadMenuItemList(data);
	}
	
	@Override
	public void onLoaderReset(Loader<ArrayList<AppModel>> loader)
	{
		this.loadMenuItemList(null);
	}
}
