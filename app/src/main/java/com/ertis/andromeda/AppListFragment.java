package com.ertis.andromeda;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.ertis.andromeda.adapters.AppMenuAdapter;
import com.ertis.andromeda.adapters.StickyHeadersLinearLayoutManager;
import com.ertis.andromeda.models.AppMenuItem;
import com.ertis.andromeda.models.AppModel;
import com.ertis.andromeda.services.AppService;
import com.ertis.andromeda.services.IAppService;
import com.ertis.andromeda.services.ServiceLocator;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

public class AppListFragment extends Fragment
{
	private static Typeface segoeTypeface;
	private FrameLayout baseLayout;
	private RecyclerView recyclerView;
	private AppMenuAdapter menuItemAdapter;
	private EditText searchTextBox;
	private boolean isEnabled = true;
	
	public AppListFragment()
	{
		// Required empty public constructor
	}
	
	public static AppListFragment newInstance(AppMenuAdapter menuItemAdapter)
	{
		AppListFragment fragment = new AppListFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		fragment.menuItemAdapter = menuItemAdapter;
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_app_list, container, false);
		baseLayout = (FrameLayout) view.findViewById(R.id.app_list_fragment_base_layout);
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		searchTextBox = (EditText) view.findViewById(R.id.searchTextBox);
		
		segoeTypeface = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/segoewp/segoe-wp.ttf");
		searchTextBox.setTypeface(segoeTypeface);
		
		/*
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
		recyclerView.setLayoutManager(linearLayoutManager);
		*/
		
		StickyHeadersLinearLayoutManager<AppMenuAdapter> layoutManager = new StickyHeadersLinearLayoutManager<>(view.getContext());
		recyclerView.setLayoutManager(layoutManager);
		
		this.menuItemAdapter.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (!isEnabled)
					return;

				try
				{
					AppMenuItem menuItem = menuItemAdapter.getDataContext(view);
					if (menuItem != null)
					{
						if (menuItem.isHeaderItem())
						{
							IAppService appService = ServiceLocator.Current().GetInstance(AppService.class);
							if (appService == null)
								return;
							
							// Show jump letters fragment
							JumpLetterFragment jumpLetterFragment = JumpLetterFragment.newInstance(appService.GetMenuItemList(), recyclerView);
							FragmentManager fm = getActivity().getSupportFragmentManager();
							fm.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.contentLayout, jumpLetterFragment, "jumpLetterFragment").addToBackStack("jumpLetterFragment").commit();
						}
						else
						{
							AppModel app = menuItem.getApp();
							if (app != null)
							{
								startNewActivity(getActivity(), app.getApplicationPackageName());
							}
						}
					}
				}
				catch (Exception ex)
				{

				}
			}
		});
		
		this.menuItemAdapter.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(final View view)
			{
				PowerMenu powerMenu = GivePopupMenu(view);
				powerMenu.showAsAnchorLeftTop(view);
				
				return true;
			}
		});
		
		//recyclerView.addItemDecoration(new AppListMenuItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
		recyclerView.setAdapter(this.menuItemAdapter);
		
		return view;
	}
	
	public void startNewActivity(Context context, String packageName)
	{
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		
		if (intent != null)
		{
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		else
		{
			// Bring user to the market or let them choose an app?
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=" + packageName));
		}
	}
	
	public void Enable()
	{
		this.isEnabled = true;
	}
	
	public void Disable()
	{
		this.isEnabled = false;
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
	
	private PowerMenu GivePopupMenu(final View view)
	{
		Context context = view.getContext();
		
		final String menuItemTitle1 = getResources().getString(R.string.pin_to_home);
		final String menuItemTitle2 = getResources().getString(R.string.uninstall);
		
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
	                     AppMenuItem appMenuItem = menuItemAdapter.getDataContext(view);
	                     if (appMenuItem != null)
	                     {
		                     FragmentActivity activity = getActivity();
		                     if (activity instanceof AppDrawerActivity)
		                     {
			                     AppDrawerActivity appDrawerActivity = (AppDrawerActivity)activity;
			                     appDrawerActivity.SwipeToHome();
		                     }
		
		                     appService.PinToHome(appMenuItem);
	                     }
                     }
                     else if (item.title.equals(menuItemTitle2))
	                 {
		                 AppMenuItem appMenuItem = menuItemAdapter.getDataContext(view);
		                 if (appMenuItem != null)
		                 {
			                 appService.UninstallPackage(appMenuItem);
		                 }
	                 }

                     powerMenu.dismiss();
                 }
             }
		);
		
		return powerMenu;
	}
	
	public void ScrollToTop()
	{
		this.recyclerView.smoothScrollToPosition(0);
	}
	
	public void ScrollToBottom()
	{
		this.recyclerView.smoothScrollToPosition(this.menuItemAdapter.getItemCount() - 1);
	}
}
