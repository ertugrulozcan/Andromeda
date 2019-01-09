package com.aero.andromeda.helpers;

import android.graphics.Color;
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
	
	public static int getAlpha(ColorDrawable drawable)
	{
		return getAlpha(ToArgbString(drawable));
	}
	
	public static int getAlpha(String argb)
	{
		if (!argb.startsWith("#"))
			argb = "#" + argb;
		
		if (argb.length() == 7)
			return 255;
		
		argb = argb.replaceFirst("^#", "");
		String alphaHex = argb.substring(0, 2);
		
		return Integer.parseInt(alphaHex, 16);
	}
	
	public static ColorDrawable clearAlpha(ColorDrawable drawable)
	{
		try
		{
			String hexColor = String.format("#55%06X", (0xFFFFFF & drawable.getColor()));
			
			return rgb(hexColor);
		}
		catch (Exception ex)
		{
			return drawable;
		}
	}
	
	public static ColorDrawable ManipulateOpacity(ColorDrawable drawable, int alpha)
	{
		int color = drawable.getColor();
		
		int red = Color.red(color);
		int blue = Color.blue(color);
		int green = Color.green(color);
		
		String a = Integer.toHexString(alpha);
		if (a.length() == 1)
			a = "0" + a;
		
		String r = Integer.toHexString(red);
		if (r.length() == 1)
			r = "0" + r;
		
		String g = Integer.toHexString(green);
		if (g.length() == 1)
			g = "0" + g;
		
		String b = Integer.toHexString(blue);
		if (b.length() == 1)
			b = "0" + b;
		
		return rgb("#" + a + r + g + b);
	}
	
	public static String ToArgbString(ColorDrawable drawable)
	{
		int color = drawable.getColor();
		
		int red = Color.red(color);
		int blue = Color.blue(color);
		int green = Color.green(color);
		int alpha = Color.alpha(color);
		
		String a = Integer.toHexString(alpha);
		if (a.length() == 1)
			a = "0" + a;
		
		String r = Integer.toHexString(red);
		if (r.length() == 1)
			r = "0" + r;
		
		String g = Integer.toHexString(green);
		if (g.length() == 1)
			g = "0" + g;
		
		String b = Integer.toHexString(blue);
		if (b.length() == 1)
			b = "0" + b;
		
		return "#" + a + r + g + b;
	}
}
