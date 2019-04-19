package com.aero.andromeda.ui;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aero.andromeda.R;
import com.aero.andromeda.helpers.Colors;
import com.aero.andromeda.helpers.SizeConverter;
import com.aero.andromeda.models.NotificationGroup;
import com.aero.andromeda.models.NotificationInfo;
import com.aero.andromeda.models.tiles.FakeTile;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.INotificationService;

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
	
	@Override
	protected void setSecondViewProperties(TileBase tile)
	{
		if (this.tileSecondViewLayout != null)
		{
			this.tileSecondViewLayout.setBackground(Colors.clearAlpha(tile.getTileColor()));
			
			ImageView tileSecondViewIcon = this.tileSecondViewLayout.findViewById(R.id.live_tile_icon);
			Drawable icon = tile.getIcon();
			if (icon != null && tileSecondViewIcon != null)
			{
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.TOP | Gravity.RIGHT;
				params.setMargins(0, 8, 8, 0);
				int defaultIconSize = SizeConverter.Current.GetDefaultIconSize();
				params.width = defaultIconSize / 2;
				params.height = defaultIconSize / 2;
				tileSecondViewIcon.setLayoutParams(params);
				
				tileSecondViewIcon.setImageDrawable(icon);
			}
			
			TextView tileSecondViewName = this.tileSecondViewLayout.findViewById(R.id.live_tile_name);
			if (tileSecondViewName != null)
				tileSecondViewName.setText(tile.getCaption());
		}
	}
	
	public void UpdateTileNotification()
	{
		TileBase tile = this.getTile();
		if (tile == null)
			return;
		
		INotificationService notificationService = ServiceLocator.Current().GetInstance(INotificationService.class);
		NotificationGroup notificationGroup = notificationService.GetNotificationGroup(tile);
		if (notificationGroup != null && notificationGroup.GetCount() > 0)
		{
            this.itemView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            this.tileSecondViewLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null);

			if (this.tileCountBadge != null && this.tileCountBadgeTextView != null)
			{
			    int count = notificationGroup.GetCount();
				this.tileCountBadgeTextView.setText("" + count);
			}
			
			NotificationInfo notificationInfo = notificationGroup.GetNext();
			
			TextView tileSecondViewTitle = this.tileSecondViewLayout.findViewById(R.id.live_tile_title);
			if (tileSecondViewTitle != null)
			{
                String notifTitle = notificationInfo.getTitle();
				tileSecondViewTitle.setText(notifTitle);

				if (notifTitle.length() > 30)
				    tileSecondViewTitle.setTextSize(SizeConverter.Current.GetDefaultFontSize() - 4);
				else
                    tileSecondViewTitle.setTextSize(SizeConverter.Current.GetDefaultFontSize());
			}
			
			TextView tileSecondViewDetail = this.tileSecondViewLayout.findViewById(R.id.live_tile_detail);
			if (tileSecondViewDetail != null)
			{
				tileSecondViewDetail.setText(notificationInfo.getMessage());
				
				if (notificationInfo.getMessage().length() <= 16)
					tileSecondViewDetail.setTextSize(SizeConverter.Current.GetDefaultFontSize() + 6);
				else if (notificationInfo.getMessage().length() <= 30)
					tileSecondViewDetail.setTextSize(SizeConverter.Current.GetDefaultFontSize());
				else
					tileSecondViewDetail.setTextSize(SizeConverter.Current.GetDefaultFontSize() - 3.5f);
			}
		}
		else
		{
			if (this.tileCountBadge != null)
				this.tileCountBadge.setVisibility(View.INVISIBLE);
			if (this.tileCountBadgeTextView != null)
				this.tileCountBadgeTextView.setText("");
		}

        this.itemView.invalidate();
        this.itemView.requestLayout();

        this.tileSecondViewLayout.invalidate();
        this.tileSecondViewLayout.requestLayout();

        this.itemView.setLayerType(View.LAYER_TYPE_NONE, null);
        this.tileSecondViewLayout.setLayerType(View.LAYER_TYPE_NONE, null);
	}
}
