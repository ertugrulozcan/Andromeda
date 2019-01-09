package com.aero.andromeda.ui;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aero.andromeda.R;
import com.aero.andromeda.helpers.Colors;
import com.aero.andromeda.models.tiles.TileBase;

public class PreviewTileView
{
	private View layoutBase;
	private TileBase tile;
	
	private FrameLayout editingTileBackgroundLayout;
	private ImageView tilePreviewIcon;
	private TextView tileCaptionTextView;
	private SeekBar tileOpacitySeekBar;
	private TextView tileColorHexTextView;
	
	private String backgroundArgbColor;
	
	public PreviewTileView(final View fragmentContainer, final TileBase tile)
	{
		this.layoutBase = fragmentContainer;
		this.tile = tile;
		
		this.editingTileBackgroundLayout = fragmentContainer.findViewById(R.id.editingTileBackgroundLayout);
		this.tilePreviewIcon = fragmentContainer.findViewById(R.id.tilePreviewIcon);
		this.tileCaptionTextView = fragmentContainer.findViewById(R.id.tilePreviewLabel);
		this.tileOpacitySeekBar = fragmentContainer.findViewById(R.id.tileOpacitySeekBar);
		this.tileColorHexTextView = fragmentContainer.findViewById(R.id.tileColorHexTextView);
		
		this.tileOpacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				PreviewTileView.this.SetOpacity(progress);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
			
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
			
			}
		});
		
		this.SetBackground(tile.getTileColor());
		this.tilePreviewIcon.setImageDrawable(tile.getIcon());
		this.tileCaptionTextView.setText(tile.getCaption());
		this.tileOpacitySeekBar.setProgress(Colors.getAlpha(this.tile.getTileColor()));
	}
	
	public void SetBackground(ColorDrawable colorDrawable)
	{
		this.editingTileBackgroundLayout.setBackground(colorDrawable);
		this.backgroundArgbColor = Colors.ToArgbString(colorDrawable);
		
		this.tileColorHexTextView.setText(this.backgroundArgbColor);
	}
	
	public void SetBackground(String argbColor)
	{
		this.editingTileBackgroundLayout.setBackground(Colors.rgb(argbColor));
		this.backgroundArgbColor = argbColor;
	}
	
	public void SetOpacity(int alpha)
	{
		if (alpha < 0 || alpha > 255)
			return;
		
		ColorDrawable drawable = Colors.ManipulateOpacity(Colors.rgb(this.backgroundArgbColor), alpha);
		this.SetBackground(drawable);
	}
}
