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
import com.aero.andromeda.managers.TilePopupMenuManager;
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
		
		PowerMenuBase powerMenu = TilePopupMenuManager.Current().GivePopupMenu(holdedView, this);
		powerMenu.showAsAnchorRightTop(holdedView);
		
		return true;
	}
}
