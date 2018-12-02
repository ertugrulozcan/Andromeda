package com.aero.andromeda.models.tiles;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.aero.andromeda.Andromeda;
import com.aero.andromeda.MainActivity;
import com.aero.andromeda.R;
import com.aero.andromeda.adapters.AppListAdapter;
import com.aero.andromeda.adapters.IconMenuAdapter;
import com.aero.andromeda.adapters.TilesAdapter;
import com.aero.andromeda.models.AppMenuItem;
import com.aero.andromeda.models.AppModel;
import com.aero.andromeda.models.IconPowerMenuItem;
import com.aero.andromeda.popup.ErtPowerMenu;
import com.aero.andromeda.popup.PowerMenuBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.ui.BaseTileViewHolder;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnDismissedListener;
import com.skydoves.powermenu.OnMenuItemClickListener;

public abstract class Tile extends TileBase
{
	private String iconName;
	
	protected Tile(final long id, TileType tileType, final AppModel app)
	{
		super(id, tileType, app);
	}
	
	public String getIconName()
	{
		return iconName;
	}
	
	public void setIconName(String iconName)
	{
		this.iconName = iconName;
	}
	
	public Drawable getCustomIcon()
	{
		if (this.iconName == null || this.iconName.isEmpty())
			return null;
		
		return this.TryGetIcon();
	}
	
	private Drawable TryGetIcon()
	{
		try
		{
			MainActivity mainActivity = ServiceLocator.Current().GetInstance(MainActivity.class);
			if (mainActivity == null)
				return null;
			
			Context context = mainActivity.getApplicationContext();
			if (context == null)
				return null;
			
			Resources resources = context.getResources();
			final int resourceId = resources.getIdentifier(this.iconName, "drawable", context.getPackageName());
			return resources.getDrawable(resourceId);
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	@Override
	public void OnClick(BaseTileViewHolder holder)
	{
		if (Andromeda.isEditMode)
			return;
		
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		if (appService == null)
			return;
		
		appService.StartApplication(this.getApplication());
	}
	
	@Override
	public boolean OnLongClick(BaseTileViewHolder holder, View holdedView)
	{
		if (holdedView == null)
			return false;
		
		PowerMenuBase powerMenu = this.GivePopupMenu(holdedView);
		powerMenu.showAsAnchorLeftTop(holdedView);
		
		return true;
	}
	
	private PowerMenuBase GivePopupMenu(final View view)
	{
		final IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		if (appService == null)
			return null;
		
		final Context context = appService.getMainContext();
		
		final String resizeMenuItemTitle = context.getResources().getString(R.string.resize);
		final String unpinMenuItemTitle = context.getResources().getString(R.string.unpin);
		
		final ErtPowerMenu powerMenu = new ErtPowerMenu.Builder<>(context, new IconMenuAdapter())
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
                     if (item.getTitle().equals(resizeMenuItemTitle))
                     {
	                     PowerMenuBase resizePowerMenu = GiveResizePopupMenu(powerMenu);
	                     resizePowerMenu.showAsAnchorLeftTop(view);
                     }
                     else if (item.getTitle().equals(unpinMenuItemTitle))
                     {
	                     appService.UnpinTile(Tile.this);
	                     powerMenu.dismiss();
                     }
                 }
             }
		);
		
		return powerMenu;
	}
	
	private PowerMenuBase GiveResizePopupMenu(final ErtPowerMenu parentMenu)
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
		
		switch (this.getTileSize())
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
			        		Tile.this.setTileSize(TileSize.Small);
				        }
				        else if (item.getTitle().equals(mediumMenuItemTitle))
				        {
					        Tile.this.setTileSize(TileSize.Medium);
				        }
				        if (item.getTitle().equals(wideMenuItemTitle))
				        {
					        Tile.this.setTileSize(TileSize.MediumWide);
				        }
				        else if (item.getTitle().equals(largeMenuItemTitle))
				        {
					        Tile.this.setTileSize(TileSize.Large);
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
				parentMenu.dismiss();
			}
		});
		
		return powerMenu;
	}
}
