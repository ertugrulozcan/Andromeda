package com.aero.andromeda.models;

import android.support.v4.app.FragmentManager;
import android.view.View;

import com.aero.andromeda.AppListFragment;
import com.aero.andromeda.JumpLetterFragment;
import com.aero.andromeda.R;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.ui.AppMenuItemViewHolder;

public class AppListHeaderItem implements IAppMenuItem
{
	public final static char ALPHANUMERIC_HEADER = '#';
	
	private String headerLabel;
	
	public AppListHeaderItem(String label)
	{
		this.headerLabel = label;
	}
	
	public AppListHeaderItem(char label)
	{
		this.headerLabel = label + "";
	}
	
	@Override
	public void bindViewHolder(AppMenuItemViewHolder viewHolder)
	{
		viewHolder.getHeaderCaption().setText(this.getHeader());
		
		viewHolder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
					IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
					if (appService == null)
						return;
					
					AppListFragment appListFragment = ServiceLocator.Current().GetInstance(AppListFragment.class);
					if (!appListFragment.isEnabled())
						return;
					
					// Show jump letters fragment
					JumpLetterFragment jumpLetterFragment = JumpLetterFragment.newInstance(appService.getMenuItemList(), appListFragment.getRecyclerView());
					FragmentManager fm = appListFragment.getActivity().getSupportFragmentManager();
					fm.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.contentLayout, jumpLetterFragment, "jumpLetterFragment").addToBackStack("jumpLetterFragment").commit();
				}
				catch (Exception ex)
				{
				
				}
			}
		});
	}
	
	@Override
	public String getHeader()
	{
		return this.headerLabel;
	}
}
