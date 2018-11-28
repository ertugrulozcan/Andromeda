package com.aero.andromeda.ui;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aero.andromeda.R;
import com.aero.andromeda.helpers.SizeConverter;
import com.aero.andromeda.models.tiles.FakeTile;
import com.aero.andromeda.models.tiles.TileBase;

public class TileViewHolder extends BaseTileViewHolder
{
	private TextView tileLabel;
	private ImageView tileIconImageView;
	
	public TileViewHolder(@NonNull View itemView)
	{
		super(itemView);
		
		this.tileLabel = itemView.findViewById(R.id.tileLabel);
		this.tileIconImageView = itemView.findViewById(R.id.tile_icon);
	}
	
	@Override
	protected void setLayoutProperties(TileBase tile)
	{
		int iconSize = SizeConverter.Current.GetDefaultIconSize();
		int smallIconSize = iconSize * 8 / 13 + 10;
		
		if (tile.getTileSize() == TileBase.TileSize.Small)
		{
			this.tileIconImageView.getLayoutParams().width = smallIconSize;
			this.tileIconImageView.getLayoutParams().height = smallIconSize;
			this.tileLabel.clearAnimation();
			this.tileLabel.setVisibility(View.INVISIBLE);
		}
		else
		{
			this.tileLabel.setText(tile.getCaption());
			this.tileIconImageView.getLayoutParams().width = iconSize;
			this.tileIconImageView.getLayoutParams().height = iconSize;
			this.tileLabel.clearAnimation();
			this.tileLabel.setVisibility(View.VISIBLE);
		}
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		params.width = this.tileIconImageView.getLayoutParams().width;
		params.height = this.tileIconImageView.getLayoutParams().height;
		this.tileIconImageView.setLayoutParams(params);
		
		Drawable icon = tile.getIcon();
		if (icon != null)
			this.tileIconImageView.setImageDrawable(icon);
		
		this.tileIconImageView.requestLayout();
		
		if (tile.getTileSize() == TileBase.TileSize.Small || tile instanceof FakeTile)
			this.tileCountBadge.setVisibility(View.INVISIBLE);
	}
}
