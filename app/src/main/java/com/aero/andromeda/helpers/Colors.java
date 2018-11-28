package com.aero.andromeda.helpers;

import android.graphics.drawable.ColorDrawable;

public class Colors
{
	public static ColorDrawable TRANSPARENT = rgb("#00000000");
	
	public static ColorDrawable rgb(String rgb)
	{
		try
		{
			if (!rgb.startsWith("#"))
				rgb = "#" + rgb;
			
			if (rgb.length() == 7)
				rgb = "#FF" + rgb.substring(1);
			
			rgb = rgb.replaceFirst("^#", "");
			
			int color = (int) Long.parseLong(rgb, 16);
			return new ColorDrawable(color);
		}
		catch (Exception ex)
		{
			return new ColorDrawable(0x00000000);
		}
	}
	
	public static ColorDrawable rgb(int color)
	{
		try
		{
			return new ColorDrawable(color);
		}
		catch (Exception ex)
		{
			return new ColorDrawable(0x00000000);
		}
	}
}
