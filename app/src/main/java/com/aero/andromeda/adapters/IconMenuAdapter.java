package com.aero.andromeda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aero.andromeda.R;
import com.aero.andromeda.helpers.Colors;
import com.aero.andromeda.models.IconPowerMenuItem;
import com.skydoves.powermenu.MenuBaseAdapter;

public class IconMenuAdapter extends MenuBaseAdapter<IconPowerMenuItem>
{
	@Override
	public View getView(int index, View view, ViewGroup viewGroup)
	{
		final Context context = viewGroup.getContext();
		
		if (view == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_icon_menu, viewGroup, false);
		}
		
		IconPowerMenuItem item = (IconPowerMenuItem) getItem(index);
		
		LinearLayout menuItemLayoutBase = view.findViewById(R.id.powerMenuItemLayoutBase);
		if (menuItemLayoutBase != null)
		{
			if (this.getSelectedPosition() == index)
				menuItemLayoutBase.setBackground(viewGroup.getResources().getDrawable(R.color.popupSelectedItemBackground));
			else
				menuItemLayoutBase.setBackground(viewGroup.getResources().getDrawable(R.color.popupBackground));
		}
		
		final ImageView icon = view.findViewById(R.id.item_icon);
		icon.setImageDrawable(item.getIcon());
		
		final TextView title = view.findViewById(R.id.item_title);
		title.setText(item.getTitle());
		
		return super.getView(index, view, viewGroup);
	}
}
