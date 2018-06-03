package com.ertis.andromeda.listeners;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ertis.andromeda.AppDrawerFragment;
import com.ertis.andromeda.R;
import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.managers.TileFolderManager;
import com.ertis.andromeda.models.AppModel;
import com.ertis.andromeda.models.FolderTile;
import com.ertis.andromeda.models.Tile;

public class TileClickListener implements View.OnClickListener, View.OnLongClickListener
{
	private AppDrawerFragment fragment;
	private TilesAdapter tilesAdapter;
	
	public TileClickListener(AppDrawerFragment fragment, TilesAdapter tilesAdapter)
	{
		this.fragment = fragment;
		this.tilesAdapter = tilesAdapter;
	}
	
	@Override
	public void onClick(View view)
	{
		if (fragment == null || view == null)
			return;
		
		if (!fragment.isEnabled())
			return;
		
		//AnimateTileFlip(view);
		
		Tile tile = this.tilesAdapter.getDataContext(view);
		if (tile != null)
		{
			if (!(tile instanceof FolderTile))
			{
				AppModel app = tile.getApplication();
				if (app != null)
				{
					if (app.getApplicationPackageName().equals("com.samsung.android.contacts") && tile.getQueryParams().equals("phoneDialer"))
						startPhoneApp();
					else
						startNewActivity(fragment.getActivity(), app.getApplicationPackageName());
				}
			}
			else
			{
				FolderTile folderTile = (FolderTile) tile;
				TileFolderManager.Current.OnClickFolderTile(folderTile);
			}
		}
	}
	
	@Override
	public boolean onLongClick(View view)
	{
		Context wrapper = new ContextThemeWrapper(fragment.getActivity(), R.style.PopupMenuStyle);
		final PopupMenu popup = new PopupMenu(wrapper, view);
		MenuInflater menuInflater = popup.getMenuInflater();
		menuInflater.inflate(R.menu.tile_menu, popup.getMenu());
		
		Menu menu = popup.getMenu();
		MenuItem menuItem = menu.findItem(R.id.resizeTile);
		if (menuItem != null)
			menuItem.getSubMenu().clearHeader();
		
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem item)
			{
				return true;
			}
		});
		
		popup.show();
		
		return true;
	}
	
	public void startNewActivity(Context context, String packageName)
	{
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		
		if (intent == null)
		{
			// Bring user to the market or let them choose an app?
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=" + packageName));
		}
		
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	public void startPhoneApp()
	{
		Intent intent = new Intent(Intent.ACTION_DIAL);
		this.fragment.startActivity(intent);
	}
}
