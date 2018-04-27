package com.ertis.andromeda.helpers;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by ertugrulozcan on 19.04.2018.
 */

public class SizeConverter
{
	public static double ConvertToDP(double pixel, Context view)
	{
		//return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, view.getResources().getDisplayMetrics());
		return pixel;
	}
}
