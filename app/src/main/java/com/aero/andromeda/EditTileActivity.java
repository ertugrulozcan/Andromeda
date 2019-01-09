package com.aero.andromeda;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;

public class EditTileActivity extends AppCompatActivity
{
	public static final String EDITING_TILE_ID = "editingTileID";
	
	private EditTileFragment editTileFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_tile);
		this.setTitle(R.string.edit_tile);
		
		setTheme(R.style.AppTheme);
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
		}
		
		Bundle extras = getIntent().getExtras();
		long tileID = extras.getLong(EDITING_TILE_ID);
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		TileBase tile = appService.getTile(tileID);
		if (tile != null)
		{
			this.editTileFragment = EditTileFragment.newInstance(tile);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.baseLayout, this.editTileFragment)
					.commit();
		}
		else
		{
		
		}
	}
}
