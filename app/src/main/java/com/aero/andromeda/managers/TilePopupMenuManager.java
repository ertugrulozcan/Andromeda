package com.aero.andromeda.managers;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.aero.andromeda.EditTileActivity;
import com.aero.andromeda.MainActivity;
import com.aero.andromeda.NavigationDrawerFragment;
import com.aero.andromeda.R;
import com.aero.andromeda.SettingsActivity;
import com.aero.andromeda.adapters.IconMenuAdapter;
import com.aero.andromeda.models.IconPowerMenuItem;
import com.aero.andromeda.models.tiles.Tile;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.popup.ErtPowerMenu;
import com.aero.andromeda.popup.PowerMenuBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnDismissedListener;
import com.skydoves.powermenu.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TilePopupMenuManager
{
	private static TilePopupMenuManager self;
	
	public static TilePopupMenuManager Current()
	{
		if (self == null)
			self = new TilePopupMenuManager();
		
		return self;
	}
	
	private Stack<PowerMenuBase> menuStack;
	
	private TilePopupMenuManager()
	{
		this.menuStack = new Stack<>();
	}
	
	public PowerMenuBase GivePopupMenu(final View view, final Tile tile)
	{
		final IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		if (appService == null)
			return null;
		
		final Context context = appService.getMainContext();
		
		final String editMenuItemTitle = context.getResources().getString(R.string.edit);
		final String resizeMenuItemTitle = context.getResources().getString(R.string.resize);
		final String unpinMenuItemTitle = context.getResources().getString(R.string.unpin);
		
		final ErtPowerMenu powerMenu = new ErtPowerMenu.Builder<>(context, new IconMenuAdapter())
				.addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.edit), editMenuItemTitle))
				.addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.resize), resizeMenuItemTitle))
				.addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.unpin), unpinMenuItemTitle))
				.setAnimation(MenuAnimation.DROP_DOWN)
				.setMenuShadow(10f)
				.setMenuRadius(0f)
				.setBackgroundAlpha(0.8f)
				.setWidth(700)
				.build();
		
		powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<IconPowerMenuItem>()
	         {
	             @Override
	             public void onItemClick(int position, IconPowerMenuItem item)
	             {
		             if (item.getTitle().equals(editMenuItemTitle))
		             {
			             final Intent intent = new Intent(context, EditTileActivity.class);
			             intent.putExtra(EditTileActivity.EDITING_TILE_ID, tile.getId());
			             context.startActivity(intent);
		             }
	                 else if (item.getTitle().equals(resizeMenuItemTitle))
	                 {
	                     PowerMenuBase resizePowerMenu = GiveResizePopupMenu(powerMenu, tile);
	                     resizePowerMenu.showAsAnchorLeftTop(view);
	                 }
	                 else if (item.getTitle().equals(unpinMenuItemTitle))
	                 {
	                     appService.UnpinTile(tile);
	                     powerMenu.dismiss();
	                 }
	             }
	         }
		);
		
		powerMenu.setOnDismissedListener(new OnDismissedListener()
		{
			@Override
			public void onDismissed()
			{
				TilePopupMenuManager.this.RemoveMenuFromStack(powerMenu);
			}
		});
		
		this.menuStack.push(powerMenu);
		
		return powerMenu;
	}
	
	private PowerMenuBase GiveResizePopupMenu(final ErtPowerMenu parentMenu, final Tile tile)
	{
		final IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		if (appService == null)
			return null;
		
		final Context context = appService.getMainContext();
		
		final String smallMenuItemTitle = context.getResources().getString(R.string.small);
		final String mediumMenuItemTitle = context.getResources().getString(R.string.medium);
		final String wideMenuItemTitle = context.getResources().getString(R.string.wide);
		final String largeMenuItemTitle = context.getResources().getString(R.string.large);
		
		final ErtPowerMenu powerMenu = new ErtPowerMenu.Builder<>(context, new IconMenuAdapter())
				.addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.small_tile), smallMenuItemTitle))
				.addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.medium_tile), mediumMenuItemTitle))
				.addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.wide_tile), wideMenuItemTitle))
				.addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.large_tile), largeMenuItemTitle))
				.setAnimation(MenuAnimation.DROP_DOWN)
				.setMenuShadow(0f)
				.setMenuRadius(0f)
				.setBackgroundAlpha(0f)
				.setTextColor(context.getResources().getColor(R.color.popupForeground))
				.setSelectedTextColor(context.getResources().getColor(R.color.colorForeground))
				.setMenuColor(context.getResources().getColor(R.color.popupBackground))
				.setSelectedMenuColor(context.getResources().getColor(R.color.popupSelectedItemBackground))
				.setWidth(600)
				.build();
		
		switch (tile.getTileSize())
		{
			case Small: powerMenu.setSelectedPosition(0);
				break;
			case Medium: powerMenu.setSelectedPosition(1);
				break;
			case MediumWide: powerMenu.setSelectedPosition(2);
				break;
			case Large: powerMenu.setSelectedPosition(3);
				break;
		}
		
		powerMenu.setOnMenuItemClickListener(
				new OnMenuItemClickListener<IconPowerMenuItem>()
				{
					@Override
					public void onItemClick(int position, IconPowerMenuItem item)
					{
						if (item.getTitle().equals(smallMenuItemTitle))
						{
							tile.setTileSize(TileBase.TileSize.Small);
						}
						else if (item.getTitle().equals(mediumMenuItemTitle))
						{
							tile.setTileSize(TileBase.TileSize.Medium);
						}
						if (item.getTitle().equals(wideMenuItemTitle))
						{
							tile.setTileSize(TileBase.TileSize.MediumWide);
						}
						else if (item.getTitle().equals(largeMenuItemTitle))
						{
							tile.setTileSize(TileBase.TileSize.Large);
						}
						
						synchronized (appService.getTilesAdapter())
						{
							appService.getTilesAdapter().notifyDataSetChanged();
						}
						
						powerMenu.dismiss();
					}
				});
		
		powerMenu.setOnDismissedListener(new OnDismissedListener()
		{
			@Override
			public void onDismissed()
			{
				TilePopupMenuManager.this.RemoveMenuFromStack(powerMenu);
				parentMenu.dismiss();
			}
		});
		
		this.menuStack.push(powerMenu);
		
		return powerMenu;
	}
	
	private void RemoveMenuFromStack(PowerMenuBase removeMenu)
	{
		if (removeMenu == null)
			return;
		
		List<PowerMenuBase> menuBaseList = new ArrayList<>();
		PowerMenuBase menu = null;
		while (menu != removeMenu && !TilePopupMenuManager.this.menuStack.isEmpty())
		{
			menu = TilePopupMenuManager.this.menuStack.pop();
			if (menu != removeMenu)
				menuBaseList.add(menu);
		}
		
		for (PowerMenuBase item : menuBaseList)
		{
			TilePopupMenuManager.this.menuStack.push(item);
		}
	}
	
	public void CloseMenu()
	{
		if (this.menuStack.empty())
			return;
		
		PowerMenuBase menu = this.menuStack.pop();
		menu.dismiss();
	}
	
	public void CloseAllMenus()
	{
		while (!this.menuStack.empty())
			this.CloseMenu();
	}
}
