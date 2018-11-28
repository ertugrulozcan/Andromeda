package com.aero.andromeda.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.aero.andromeda.MainActivity;
import com.aero.andromeda.R;
import com.aero.andromeda.adapters.AppListAdapter;
import com.aero.andromeda.adapters.IconMenuAdapter;
import com.aero.andromeda.popup.ErtPowerMenu;
import com.aero.andromeda.popup.PowerMenuBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.ui.AppMenuItemViewHolder;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenuItem;

public class AppMenuItem implements IAppMenuItem
{
	private final AppModel appModel;
	
	public AppMenuItem(AppModel appModel)
	{
		this.appModel = appModel;
	}
	
	@Override
	public void bindViewHolder(AppMenuItemViewHolder viewHolder)
	{
		viewHolder.getTileLabel().setText(this.getHeader());
		//viewHolder.tileLabel.setTypeface(segoeTypeface);
		
		Drawable icon = this.getIcon();
		if (icon != null)
			viewHolder.getTileIconImageView().setImageDrawable(icon);
		
		viewHolder.getTileIconImageView().requestLayout();
		
		viewHolder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
				if (appService == null)
					return;
				
				appService.StartApplication(appModel);
			}
		});
		
		viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(final View view)
			{
				PowerMenuBase powerMenu = GivePopupMenu(view);
				powerMenu.showAsAnchorLeftTop(view);
				
				return true;
			}
		});
	}
	
	public AppModel getAppModel()
	{
		return appModel;
	}
	
	@Override
	public String getHeader()
	{
		return this.appModel.getAppLabel();
	}
	
	public Drawable getIcon()
	{
		if (this.appModel != null)
			return this.appModel.getIcon();
		
		return null;
	}
	
	public static char GetHeaderLetter(AppModel appModel)
	{
		Character firstLetter = appModel.getAppLabel().charAt(0);
		if (!Character.isLetter(firstLetter))
			firstLetter = AppListHeaderItem.ALPHANUMERIC_HEADER;
		
		firstLetter = Character.toUpperCase(firstLetter);
		
		return firstLetter;
	}
	
	public static char GetHeaderLetter(IAppMenuItem appMenuItem)
	{
		if (appMenuItem instanceof AppMenuItem)
		{
			return GetHeaderLetter(((AppMenuItem)appMenuItem).getAppModel());
		}
		else
		{
			return appMenuItem.getHeader().charAt(0);
		}
	}
	
	private PowerMenuBase GivePopupMenu(final View view)
	{
		final IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		if (appService == null)
			return null;
		
		final AppListAdapter menuItemAdapter = appService.getAppListAdapter();
		final Context context = appService.getMainContext();
		
		final String menuItemTitle1 = context.getResources().getString(R.string.pin_to_home);
		final String menuItemTitle2 = context.getResources().getString(R.string.uninstall);
		final String menuItemTitle3 = context.getResources().getString(R.string.info);
		
		final ErtPowerMenu powerMenu = new ErtPowerMenu.Builder<>(context, new IconMenuAdapter())
				.addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.pin), menuItemTitle1))
				.addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.delete), menuItemTitle2))
				.addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.info), menuItemTitle3))
				.setAnimation(MenuAnimation.DROP_DOWN)
				.setMenuShadow(10f)
				.setMenuRadius(0f)
				/*
				.setTextColor(context.getResources().getColor(R.color.popupForeground))
				.setSelectedTextColor(context.getResources().getColor(R.color.colorForeground))
				.setMenuColor(context.getResources().getColor(R.color.popupBackground))
				.setSelectedMenuColor(context.getResources().getColor(R.color.popupSelectedItemBackground))
				*/
				.setWidth(700)
				.build();
		
		/*
		final PowerMenu powerMenu = new PowerMenu.Builder(context)
				.addItem(new PowerMenuItem(menuItemTitle1, false))
				.addItem(new PowerMenuItem(menuItemTitle2, false))
				.setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
				.setMenuShadow(10f)
				.setMenuRadius(0f)
				.setTextColor(context.getResources().getColor(R.color.popupForeground))
				.setSelectedTextColor(context.getResources().getColor(R.color.colorForeground))
				.setMenuColor(context.getResources().getColor(R.color.popupBackground))
				.setSelectedMenuColor(context.getResources().getColor(R.color.popupSelectedItemBackground))
				.setWidth(700)
				.build();
		*/
		
		powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<IconPowerMenuItem>()
         {
                 @Override
                 public void onItemClick(int position, IconPowerMenuItem item)
                 {
                     if (item.getTitle().equals(menuItemTitle1))
                     {
                         AppMenuItem appMenuItem = menuItemAdapter.getDataContext(view);
                         if (appMenuItem != null)
                         {
	                         MainActivity appDrawerActivity = ServiceLocator.Current().GetInstance(MainActivity.class);
	                         appDrawerActivity.SwipeToHome();
                             appService.PinToHome(appMenuItem);
                         }
                     }
                     else if (item.getTitle().equals(menuItemTitle2))
                     {
                         AppMenuItem appMenuItem = menuItemAdapter.getDataContext(view);
                         if (appMenuItem != null)
                         {
                             appService.UninstallPackage(appMenuItem);
                         }
                     }
                     else if (item.getTitle().equals(menuItemTitle3))
                     {
	                     AppMenuItem appMenuItem = menuItemAdapter.getDataContext(view);
	                     if (appMenuItem != null)
	                     {
		                     MainActivity mainActivity = ServiceLocator.Current().GetInstance(MainActivity.class);
		                     appService.StartInstalledAppDetailsActivity(mainActivity, appMenuItem.getAppModel().getApplicationPackageName());
	                     }
                     }

                     powerMenu.dismiss();
                 }
             }
		);
		
		return powerMenu;
	}
}
