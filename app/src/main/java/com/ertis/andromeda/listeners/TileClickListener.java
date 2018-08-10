package com.ertis.andromeda.listeners;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.ertis.andromeda.AppDrawerActivity;
import com.ertis.andromeda.AppDrawerFragment;
import com.ertis.andromeda.R;
import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.managers.TileFolderManager;
import com.ertis.andromeda.managers.TileOrderManager;
import com.ertis.andromeda.models.AppMenuItem;
import com.ertis.andromeda.models.AppModel;
import com.ertis.andromeda.models.FolderTile;
import com.ertis.andromeda.models.Tile;
import com.ertis.andromeda.models.TileFolder;
import com.ertis.andromeda.services.AppService;
import com.ertis.andromeda.services.IAppService;
import com.ertis.andromeda.services.ServiceLocator;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

public class TileClickListener implements View.OnClickListener, View.OnLongClickListener
{
	private final AppDrawerFragment fragment;
	private final TilesAdapter tilesAdapter;
	private final TileOrderManager tileOrderManager;
	
	private PowerMenu tilePopupMenu = null;
	
	public TileClickListener(AppDrawerFragment fragment, TilesAdapter tilesAdapter)
	{
		this.fragment = fragment;
		this.tilesAdapter = tilesAdapter;
		
		this.tileOrderManager = ServiceLocator.Current().GetInstance(TileOrderManager.class);
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
		Tile tile = this.tilesAdapter.getDataContext(view);
		if (tile != null)
		{
			if (!(tile instanceof TileFolder))
			{
				if (this.tileOrderManager != null)
					this.tileOrderManager.setEditMode(true);
				
				this.tilePopupMenu = GivePopupMenu(view);
				this.tilePopupMenu.showAsAnchorLeftTop(view);
				
				return true;
			}
		}
		
		return false;
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
	
	private PowerMenu GivePopupMenu(final View view)
	{
		Context context = view.getContext();
		
		final String menuItemTitle1 = context.getResources().getString(R.string.unpin);
		final String menuItemTitle2 = context.getResources().getString(R.string.resize);
		
		final PowerMenuItem unpinMenuItem = new PowerMenuItem(menuItemTitle1, false);
		final PowerMenuItem resizeMenuItem = new PowerMenuItem(menuItemTitle2, false);
		
		final PowerMenu powerMenu = new PowerMenu.Builder(context)
				.addItem(unpinMenuItem)
				.addItem(resizeMenuItem)
				.setAnimation(MenuAnimation.DROP_DOWN)
				.setMenuShadow(0f)
				.setMenuRadius(0f)
				.setTextColor(context.getResources().getColor(R.color.popupForeground))
				.setSelectedTextColor(context.getResources().getColor(R.color.colorForeground))
				.setMenuColor(context.getResources().getColor(R.color.popupBackground))
				.setSelectedMenuColor(context.getResources().getColor(R.color.popupSelectedItemBackground))
				.setWidth(700)
				.build();
		
		powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>()
             {
                 @Override
                 public void onItemClick(int position, PowerMenuItem item)
                 {
                     IAppService appService = ServiceLocator.Current().GetInstance(AppService.class);
                     if (appService == null)
                         return;

                     if (item.title.equals(menuItemTitle1))
                     {
                         Tile tile = tilesAdapter.getDataContext(view);
                         if (tile != null)
                         {
                             appService.UnpinTile(tile);
                         }
	
	                     powerMenu.dismiss();
                     }
	                 else if (item.title.equals(menuItemTitle2))
	                 {
		                 PowerMenu resizePowerMenu = GiveResizePopupMenu(view);
		                 resizePowerMenu.showAsAnchorLeftTop(view);
	                 }
                 }
             }
		);
		
		return powerMenu;
	}
	
	private PowerMenu GiveResizePopupMenu(final View view)
	{
		final Tile tile = tilesAdapter.getDataContext(view);
		if (tile == null)
			return null;
		
		final TilesAdapter.BaseTileViewHolder viewHolder = tile.getViewHolder();
		if (viewHolder == null)
			return null;
		
		Context context = view.getContext();
		Resources resources = context.getResources();
		
		final PowerMenuItem resizeSmallMenuItem = new PowerMenuItem(resources.getString(R.string.small), tile.getTileSize() == Tile.TileSize.Small);
		final PowerMenuItem resizeMediumMenuItem = new PowerMenuItem(resources.getString(R.string.medium), tile.getTileSize() == Tile.TileSize.Medium);
		final PowerMenuItem resizeWideMenuItem = new PowerMenuItem(resources.getString(R.string.wide), tile.getTileSize() == Tile.TileSize.MediumWide);
		final PowerMenuItem resizeLargeMenuItem = new PowerMenuItem(resources.getString(R.string.large), tile.getTileSize() == Tile.TileSize.Large);
		
		final PowerMenu powerMenu = new PowerMenu.Builder(context)
				.addItem(resizeSmallMenuItem)
				.addItem(resizeMediumMenuItem)
				.addItem(resizeWideMenuItem)
				.addItem(resizeLargeMenuItem)
				.setAnimation(MenuAnimation.DROP_DOWN)
				.setMenuShadow(0f)
				.setMenuRadius(0f)
				.setBackgroundColor(R.color.transparent)
				.setBackgroundAlpha(0f)
				.setTextColor(resources.getColor(R.color.popupForeground))
				.setSelectedTextColor(resources.getColor(R.color.colorForeground))
				.setMenuColor(resources.getColor(R.color.popupBackground))
				.setSelectedMenuColor(resources.getColor(R.color.popupSelectedItemBackground))
				.setWidth(700)
				.build();
		
		powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>()
        {
        	@Override
	        public void onItemClick(int position, PowerMenuItem item)
	        {
	        	IAppService appService = ServiceLocator.Current().GetInstance(AppService.class);
	        	if (appService == null)
	        		return;

	        	if (item.title.equals(resizeSmallMenuItem.title))
	        	{
	        		tile.setTileSize(Tile.TileSize.Small);
		        }
		        else if (item.title.equals(resizeMediumMenuItem.title))
		        {
			        tile.setTileSize(Tile.TileSize.Medium);
		        }
		        else if (item.title.equals(resizeWideMenuItem.title))
		        {
			        tile.setTileSize(Tile.TileSize.MediumWide);
		        }
		        else if (item.title.equals(resizeLargeMenuItem.title))
		        {
			        tile.setTileSize(Tile.TileSize.Large);
		        }
		
		        synchronized (tilesAdapter)
		        {
		        	int tileIndex = tilesAdapter.getItemIndex(tile);
			        //tilesAdapter.onBindViewHolder(viewHolder, tileIndex);
		        	
			        tilesAdapter.notifyItemChanged(tileIndex);
			        //tilesAdapter.notifyAll();
		        }
	        	
		        powerMenu.dismiss();
		        
		        if (tilePopupMenu != null)
			        tilePopupMenu.dismiss();
	        }
        });
		
		powerMenu.setOnBackgroundClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				powerMenu.dismiss();
				
				if (tilePopupMenu != null)
					tilePopupMenu.dismiss();
				
				powerMenu.setOnBackgroundClickListener(null);
			}
		});
		
		return powerMenu;
	}
}
