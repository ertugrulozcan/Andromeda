package com.ertis.andromeda;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AppDrawerBaseActivity extends FragmentActivity
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_drawer_base);
		
		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		// viewPager.setAdapter(new FragmentPagerAdapter(this));
	}
}
