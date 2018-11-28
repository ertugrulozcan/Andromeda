package com.aero.andromeda;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Window;
import android.widget.CompoundButton;

import com.aero.andromeda.helpers.SizeConverter;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.services.interfaces.ISettingsService;

public class SettingsActivity extends AppCompatActivity
{
	private ISettingsService settingsService;
	
	private SwitchCompat showMoreTilesSwitch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.settingsService = ServiceLocator.Current().GetInstance(ISettingsService.class);
		
		setContentView(R.layout.activity_settings);
		
		setTheme(R.style.AppTheme);
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
		}
		
		this.showMoreTilesSwitch = findViewById(R.id.showMoreTilesSwitch);
		this.showMoreTilesSwitch.setChecked(this.settingsService.getUISettings().isCheckedShowMoreTiles());
		this.showMoreTilesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				settingsService.SetShowMoreTilesSwitch(isChecked);
			}
		});
	}
	
	private void OpenAppSettingsPage()
	{
		// Kritik bir hata durumunda app settings'i acmak icin
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		MainActivity mainActivity = ServiceLocator.Current().GetInstance(MainActivity.class);
		appService.StartInstalledAppDetailsActivity(mainActivity, mainActivity.getApplicationContext().getPackageName());
	}
}
