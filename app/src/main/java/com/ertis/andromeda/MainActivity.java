package com.ertis.andromeda;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends FragmentActivity
{
	private ImageButton homeButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		this.setStatusBarTranslucent(true);
		
		/*
		this.homeButton = (ImageButton)findViewById(R.id.homeButton);
		this.homeButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent homeNavigateIntent = new Intent(MainActivity.this, AppDrawerActivity.class);
				// homeNavigateIntent.putExtra("key", value); //Optional parameters
				MainActivity.this.startActivity(homeNavigateIntent);
				overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
			}
		});
		*/
		
		this.setClockFonts();
		
		// FullScreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}
	
	private void setClockFonts()
	{
		Typeface segoeTypeface = Typeface.createFromAsset(getAssets(), "fonts/segoewp/segoe-wp-light.ttf");
		
		TextView hourTextView = (TextView) findViewById(R.id.hour_text);
		hourTextView.setTypeface(segoeTypeface);
		
		TextView dayOfWeeokTextView = (TextView) findViewById(R.id.dayofweek_text);
		dayOfWeeokTextView.setTypeface(segoeTypeface);
		
		TextView dateTextView = (TextView) findViewById(R.id.date_text);
		dateTextView.setTypeface(segoeTypeface);
	}
	
	protected void setStatusBarTranslucent(boolean makeTranslucent)
	{
		if (makeTranslucent)
		{
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		else
		{
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}
}
