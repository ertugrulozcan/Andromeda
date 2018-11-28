package com.aero.andromeda;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import com.aero.andromeda.models.AppListHeaderItem;
import com.aero.andromeda.models.IAppMenuItem;

import java.util.List;

public class JumpLetterFragment extends Fragment
{
	private static JumpLetterFragment self;
	private List<IAppMenuItem> appMenuList;
	private RecyclerView appListRecyclerView;
	
	public JumpLetterFragment()
	{
		self = this;
	}
	
	public static JumpLetterFragment newInstance(List<IAppMenuItem> appMenuList, RecyclerView recyclerView)
	{
		JumpLetterFragment fragment = new JumpLetterFragment();
		Bundle args = new Bundle();
		
		fragment.appMenuList = appMenuList;
		fragment.appListRecyclerView = recyclerView;
		
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
		
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View fragment = inflater.inflate(R.layout.fragment_jump_letter, container, false);
		FrameLayout jumplistBaseLayout = fragment.findViewById(R.id.jumplistBaseLayout);
		jumplistBaseLayout.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent)
			{
				close();
				
				return true;
			}
		});
		
		Typeface segoeTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/segoewp/segoe-wp-light.ttf");
		
		GridLayout keypadLayout = fragment.findViewById(R.id.keypadLayout);
		for (int index = 0; index < ((ViewGroup) keypadLayout).getChildCount(); ++index)
		{
			View nextChild = ((ViewGroup) keypadLayout).getChildAt(index);
			
			if (nextChild instanceof Button)
			{
				Button button = (Button) nextChild;
				button.setTypeface(segoeTypeface);
				
				CharSequence c = button.getText();
				boolean isFound = false;
				for (IAppMenuItem menuItem : appMenuList)
				{
					if (menuItem instanceof AppListHeaderItem)
					{
						if (Character.toUpperCase(menuItem.getHeader().charAt(0)) == Character.toUpperCase(c.charAt(0)))
						{
							isFound = true;
						}
					}
				}
				
				if (!isFound)
					button.setEnabled(false);
				else
					button.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							Button button = (Button) view;
							CharSequence c = button.getText();
							
							int pos = 0;
							for (IAppMenuItem menuItem : appMenuList)
							{
								pos++;
								if (menuItem instanceof AppListHeaderItem)
								{
									if (Character.toUpperCase(menuItem.getHeader().charAt(0)) == Character.toUpperCase(c.charAt(0)))
									{
										break;
									}
								}
							}
							
							appListRecyclerView.smoothScrollToPosition(pos);
							close();
						}
					});
			}
		}
		
		return fragment;
	}
	
	private void close()
	{
		getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).remove(self).commit();
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
}
