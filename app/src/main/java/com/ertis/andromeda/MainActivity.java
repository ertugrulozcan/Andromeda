package com.ertis.andromeda;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

public class MainActivity extends FragmentActivity
{
	private ImageButton homeButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		this.setStatusBarTranslucent(true);
		
		this.homeButton = (ImageButton)findViewById(R.id.homeButton);
		this.homeButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent homeNavigateIntent = new Intent(MainActivity.this, AppDrawerActivity.class);
				// homeNavigateIntent.putExtra("key", value); //Optional parameters
				MainActivity.this.startActivity(homeNavigateIntent);
			}
		});
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
