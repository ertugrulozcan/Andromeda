package com.aero.andromeda.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aero.andromeda.Andromeda;
import com.aero.andromeda.AppDrawerFragment;
import com.aero.andromeda.R;
import com.aero.andromeda.helpers.Colors;
import com.aero.andromeda.helpers.OnStartDragListener;
import com.aero.andromeda.helpers.SizeConverter;
import com.aero.andromeda.managers.TileOrderManager;
import com.aero.andromeda.managers.TilesLayoutManager;
import com.aero.andromeda.managers.TilesLayoutManager.TileSpanLayoutParams;
import com.aero.andromeda.managers.TilesLayoutManager.SpanSize;
import com.aero.andromeda.models.tiles.FakeTile;
import com.aero.andromeda.models.tiles.Folder;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.services.interfaces.ISettingsService;
import com.aero.andromeda.settings.UISettings;

public abstract class BaseTileViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder
{
	private static long ID_COUNTER = 1;
	private long id = -1;
	
	private final float UNSELECTED_ALPHA = 0.5f;
	
	private TileBase bindedTile = null;
	
	protected View itemView;
	protected LinearLayout tileLayout;
	protected FrameLayout tileBox;
	protected FrameLayout tileInnerBox;
	protected FrameLayout tileContentLayout;
	protected FrameLayout tileBackgroundCover;
	protected RelativeLayout tileCountBadge;
	protected TextView tileCountBadgeTextView;
	protected ImageButton tileMenuButton;
	protected ImageButton tileMenuUnpinButton;
	
	public BaseTileViewHolder(@NonNull View itemView)
	{
		super(itemView);
		this.id = ID_COUNTER++;
		
		this.itemView = itemView;
		
		this.tileLayout = itemView.findViewById(R.id.tile_layout);
		this.tileBox = itemView.findViewById(R.id.tile_box);
		this.tileInnerBox = itemView.findViewById(R.id.tileInnerBox);
		this.tileContentLayout = itemView.findViewById(R.id.tileContentLayout);
		this.tileBackgroundCover = itemView.findViewById(R.id.tileBackgroundCover);
		this.tileCountBadge = itemView.findViewById(R.id.tileCountBadge);
		this.tileCountBadgeTextView = itemView.findViewById(R.id.tileCountBadgeTextView);
		this.tileMenuButton = itemView.findViewById(R.id.tileMenuButton);
		this.tileMenuUnpinButton = itemView.findViewById(R.id.tileMenuUnpinButton);
		
		if (this.tileMenuButton != null)
		{
			this.tileMenuButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (BaseTileViewHolder.this.bindedTile != null)
						BaseTileViewHolder.this.bindedTile.OnLongClick(BaseTileViewHolder.this);
				}
			});
		}
		
		if (this.tileMenuUnpinButton != null)
		{
			this.tileMenuUnpinButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (BaseTileViewHolder.this.bindedTile != null)
					{
						IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
						appService.UnpinTile(BaseTileViewHolder.this.bindedTile);
					}
				}
			});
		}
		
		if (this.tileCountBadge != null)
			this.tileCountBadge.setVisibility(View.INVISIBLE);
	}
	
	public long getId()
	{
		return this.id;
	}
	
	protected abstract void setLayoutProperties(final TileBase tile);
	
	public void bindViewHolder(final TileBase tile, int index, final OnStartDragListener dragStartListener)
	{
		this.bindedTile = tile;
		tile.setParentViewHolder(this);
		
		if (this.tileInnerBox != null)
			this.tileInnerBox.setBackground(tile.getTileColor());
		
		this.setLayoutProperties(tile);
		this.setTileSizes(tile);
		this.setClickListener(tile, dragStartListener);
		
		if (this.tileCountBadgeTextView != null)
			this.tileCountBadgeTextView.setText(index + "");
		
		this.setEditModeProperties(tile);
		TileOrderManager.Current().RefreshSelectedTileHolder(this);
	}
	
	private void setTileSizes(final TileBase tile)
	{
		this.tileInnerBox.setLayoutParams(this.calculateTileBoxLayoutParams(tile));
		this.tileLayout.setLayoutParams(this.calculateTileBoxSpanLayoutParams(tile));
		
		int tileCornerButtonSize = SizeConverter.Current.GetTileCornerButtonSize();
		if (this.tileMenuUnpinButton != null)
		{
			FrameLayout.LayoutParams tileCornerButtonLayoutParams = new FrameLayout.LayoutParams(tileCornerButtonSize, tileCornerButtonSize);
			tileCornerButtonLayoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
			this.tileMenuUnpinButton.setLayoutParams(tileCornerButtonLayoutParams);
		}
		
		if (this.tileMenuButton != null)
		{
			FrameLayout.LayoutParams tileCornerButtonLayoutParams = new FrameLayout.LayoutParams(tileCornerButtonSize, tileCornerButtonSize);
			tileCornerButtonLayoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
			this.tileMenuButton.setLayoutParams(tileCornerButtonLayoutParams);
		}
		
		this.tileLayout.requestLayout();
	}
	
	private void setClickListener(final TileBase tile, final OnStartDragListener dragStartListener)
	{
		final BaseTileViewHolder self = this;
		this.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (tile instanceof FakeTile)
					return;
				
				TileOrderManager.Current().SelectTile(self);
				tile.OnClick(self);
			}
		});
		
		this.itemView.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				if (tile.getTileType() == TileBase.TileType.TilesHeader || tile.getTileType() == TileBase.TileType.TilesFooter || tile instanceof FakeTile)
				{
					return false;
				}
				
				TileOrderManager.Current().SelectTile(self);
				
				if (Andromeda.isEditMode)
				{
					if (dragStartListener != null)
						dragStartListener.onStartDrag(BaseTileViewHolder.this);
					
					return false;
				}
				else
				{
					TileOrderManager.Current().EnterEditMode(BaseTileViewHolder.this);
					
					return true;
				}
			}
		});
	}
	
	public View getItemView()
	{
		return itemView;
	}
	
	public TileBase getTile()
	{
		return this.bindedTile;
	}
	
	@Override
	public void onItemSelected()
	{
		this.setEditModeProperties(this.bindedTile, true);
	}
	
	@Override
	public void onItemUnselected()
	{
		this.setEditModeProperties(this.bindedTile, false);
	}
	
	@Override
	public void onItemClear()
	{
		this.setEditModeProperties(this.bindedTile);
	}
	
	private void setEditModeProperties(final TileBase tile)
	{
		boolean isSelectedTile = TileOrderManager.Current().IsSelectedTile(this);
		this.setEditModeProperties(tile, isSelectedTile);
	}
	
	private void setEditModeProperties(final TileBase tile, boolean isSelectedTile)
	{
		if (tile == null)
			return;
		
		this.setTileMenuButtons(isSelectedTile);
		this.setTileScale(tile, isSelectedTile);
		this.setTileOpacity(tile, isSelectedTile);
		this.setZIndexForSelectedTileHolder(isSelectedTile);
	}
	
	private void setZIndexForSelectedTileHolder(boolean isSelectedTile)
	{
		if (isSelectedTile)
		{
			AppDrawerFragment appDrawerFragment = ServiceLocator.Current().GetInstance(AppDrawerFragment.class);
			View container = appDrawerFragment.GetContainer(this.itemView);
			if (container != null)
			{
				container.setZ(10000);
				container.bringToFront();
				container.invalidate();
			}
		}
	}
	
	private void setTileMenuButtons(boolean isSelectedTile)
	{
		if (Andromeda.isEditMode)
		{
			if (this.tileMenuButton != null)
			{
				if (isSelectedTile)
					this.tileMenuButton.setVisibility(View.VISIBLE);
				else
					this.tileMenuButton.setVisibility(View.INVISIBLE);
			}
			
			if (this.tileMenuUnpinButton != null)
			{
				if (isSelectedTile)
					this.tileMenuUnpinButton.setVisibility(View.VISIBLE);
				else
					this.tileMenuUnpinButton.setVisibility(View.INVISIBLE);
			}
		}
		else
		{
			if (this.tileMenuButton != null)
				this.tileMenuButton.setVisibility(View.INVISIBLE);
			
			if (this.tileMenuUnpinButton != null)
				this.tileMenuUnpinButton.setVisibility(View.INVISIBLE);
		}
	}
	
	private void setTileOpacity(final TileBase tile, boolean isSelectedTile)
	{
		if (tile == null)
			return;
		
		if (Andromeda.isEditMode)
		{
			if (this.tileInnerBox != null)
			{
				if (isSelectedTile)
				{
					this.tileInnerBox.setAlpha(1.0f);
					
					if (this.tileBackgroundCover != null && this.bindedTile != null)
						this.tileBackgroundCover.setBackground(Colors.clearAlpha(this.bindedTile.getTileColor()));
				}
				else
				{
					this.tileInnerBox.setAlpha(UNSELECTED_ALPHA);
					
					if (this.tileBackgroundCover != null && this.bindedTile != null)
						this.tileBackgroundCover.setBackground(Colors.TRANSPARENT);
				}
			}
		}
		else
		{
			if (this.tileInnerBox != null)
				this.tileInnerBox.setAlpha(1.0f);
			
			if (this.tileBackgroundCover != null && this.bindedTile != null)
				this.tileBackgroundCover.setBackground(Colors.TRANSPARENT);
		}
	}
	
	private void setTileScale(final TileBase tile, boolean isSelectedTile)
	{
		if (tile == null || this.tileInnerBox == null)
			return;
		
		float ratio = SizeConverter.Current.GetTileSizeProportion();
		
		if (Andromeda.isEditMode)
		{
			if (tile.getTileType() == TileBase.TileType.TilesHeader || tile.getTileType() == TileBase.TileType.TilesFooter)
			{
				return;
			}
			
			if (isSelectedTile)
			{
				switch (tile.getTileSize())
				{
					case Small:
					{
						this.tileInnerBox.setPivotX(0.0f * ratio);
						this.tileInnerBox.setPivotY(72.0f * ratio);
						this.tileInnerBox.setScaleX(0.82f);
						this.tileInnerBox.setScaleY(0.82f);
					}
					break;
					case Medium:
					{
						this.tileInnerBox.setPivotX(145.0f * ratio);
						this.tileInnerBox.setPivotY(155.0f * ratio);
						this.tileInnerBox.setScaleX(0.87f);
						this.tileInnerBox.setScaleY(0.87f);
					}
					break;
					case MediumWide:
					{
						this.tileInnerBox.setPivotX(330.0f * ratio);
						this.tileInnerBox.setPivotY(168.0f * ratio);
						this.tileInnerBox.setScaleX(0.92f);
						this.tileInnerBox.setScaleY(0.87f);
					}
					break;
					case Large:
					{
						this.tileInnerBox.setPivotX(330.0f * ratio);
						this.tileInnerBox.setPivotY(330.0f * ratio);
						this.tileInnerBox.setScaleX(0.92f);
						this.tileInnerBox.setScaleY(0.92f);
					}
					break;
				}
			}
			else
			{
				switch (tile.getTileSize())
				{
					case Small:
					{
						this.tileInnerBox.setPivotX(0.0f * ratio);
						this.tileInnerBox.setPivotY(72.0f * ratio);
						this.tileInnerBox.setScaleX(0.95f);
						this.tileInnerBox.setScaleY(0.95f);
					}
					break;
					case Medium:
					{
						this.tileInnerBox.setPivotX(145.0f * ratio);
						this.tileInnerBox.setPivotY(155.0f * ratio);
						this.tileInnerBox.setScaleX(0.95f);
						this.tileInnerBox.setScaleY(0.95f);
					}
					break;
					case MediumWide:
					{
						this.tileInnerBox.setPivotX(330.0f * ratio);
						this.tileInnerBox.setPivotY(168.0f * ratio);
						this.tileInnerBox.setScaleX(0.95f);
						this.tileInnerBox.setScaleY(0.95f);
					}
					break;
					case Large:
					{
						this.tileInnerBox.setPivotX(330.0f * ratio);
						this.tileInnerBox.setPivotY(330.0f * ratio);
						this.tileInnerBox.setScaleX(0.95f);
						this.tileInnerBox.setScaleY(0.95f);
					}
					break;
				}
			}
		}
		else
		{
			this.tileInnerBox.setPivotX(0.0f);
			this.tileInnerBox.setPivotY(0.0f);
			this.tileInnerBox.setScaleX(1.0f);
			this.tileInnerBox.setScaleY(1.0f);
		}
	}
	
	private FrameLayout.LayoutParams calculateTileBoxLayoutParams(TileBase tile)
	{
		int SMALL_TILE_SIZE = SizeConverter.Current.GetTileWidth(TileBase.TileSize.Small);
		int MEDIUM_TILE_SIZE = SizeConverter.Current.GetTileWidth(TileBase.TileSize.Medium);
		int WIDE_TILE_SIZE = SizeConverter.Current.GetTileWidth(TileBase.TileSize.MediumWide);
		int FULL_TILE_SIZE = SizeConverter.Current.GetTilePanelFullWidth();
		int TILE_MARGIN = SizeConverter.Current.GetTileMargin();
		
		if (tile.getTileType() == TileBase.TileType.TilesHeader)
		{
			FrameLayout.LayoutParams tileBoxLayoutParams = new FrameLayout.LayoutParams(FULL_TILE_SIZE, LinearLayout.LayoutParams.MATCH_PARENT);
			int margin = TILE_MARGIN;
			tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
			
			return tileBoxLayoutParams;
		}
		else if (tile.getTileType() == TileBase.TileType.TilesFooter)
		{
			FrameLayout.LayoutParams tileBoxLayoutParams = new FrameLayout.LayoutParams(FULL_TILE_SIZE, LinearLayout.LayoutParams.WRAP_CONTENT);
			int margin = TILE_MARGIN;
			int topMargin = SizeConverter.Current.GetDeviceResolution().heightPixels / 60 + 20;
			tileBoxLayoutParams.setMargins(margin, topMargin, margin, margin);
			tileBoxLayoutParams.gravity = Gravity.CENTER_VERTICAL;
			
			return tileBoxLayoutParams;
		}
		
		if (tile.getTileType() != TileBase.TileType.Folder)
		{
			switch (tile.getTileSize())
			{
				case Small:
				{
					FrameLayout.LayoutParams tileBoxLayoutParams = new FrameLayout.LayoutParams(SMALL_TILE_SIZE, SMALL_TILE_SIZE);
					int margin = TILE_MARGIN;
					tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
					tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
					
					return tileBoxLayoutParams;
				}
				case Medium:
				{
					FrameLayout.LayoutParams tileBoxLayoutParams = new FrameLayout.LayoutParams(MEDIUM_TILE_SIZE, MEDIUM_TILE_SIZE);
					int margin = TILE_MARGIN;
					tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
					tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
					
					return tileBoxLayoutParams;
				}
				case MediumWide:
				{
					FrameLayout.LayoutParams tileBoxLayoutParams = new FrameLayout.LayoutParams(WIDE_TILE_SIZE, MEDIUM_TILE_SIZE);
					int margin = TILE_MARGIN;
					tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
					tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
					
					return tileBoxLayoutParams;
				}
				case Large:
				{
					FrameLayout.LayoutParams tileBoxLayoutParams = new FrameLayout.LayoutParams(WIDE_TILE_SIZE, WIDE_TILE_SIZE);
					int margin = TILE_MARGIN;
					tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
					tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
					
					return tileBoxLayoutParams;
				}
			}
		}
		else
		{
			Folder folderTile = (Folder) tile;
			int height = this.calculateTotalRowHeight(folderTile.GetTotalRowCount());
			FrameLayout.LayoutParams tileBoxLayoutParams = new FrameLayout.LayoutParams(FULL_TILE_SIZE, height);
			int margin = TILE_MARGIN;
			tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
			tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
			
			return tileBoxLayoutParams;
		}
		
		return null;
	}
	
	private TilesLayoutManager.TileSpanLayoutParams calculateTileBoxSpanLayoutParams(TileBase tile)
	{
		ISettingsService settingsService = ServiceLocator.Current().GetInstance(ISettingsService.class);
		UISettings uiSettings = settingsService.getUISettings();
		
		SpanSize spanSize = new SpanSize(1, 1);
		
		if (tile.getTileType() == TileBase.TileType.TilesHeader)
		{
			spanSize = new SpanSize(uiSettings.getLayoutWidth(), 2);
		}
		else if (tile.getTileType() == TileBase.TileType.TilesFooter)
		{
			spanSize = new SpanSize(uiSettings.getLayoutWidth(), 2);
		}
		else if (tile.getTileType() != TileBase.TileType.Folder)
		{
			switch (tile.getTileSize())
			{
				case Small:
				{
					spanSize = new SpanSize(1, 1);
				}
				break;
				case Medium:
				{
					spanSize = new SpanSize(2, 2);
				}
				break;
				case MediumWide:
				{
					spanSize = new SpanSize(4, 2);
				}
				break;
				case Large:
				{
					spanSize = new SpanSize(4, 4);
				}
				break;
			}
		}
		else
		{
			Folder folderTile = (Folder) tile;
			spanSize = new SpanSize(uiSettings.getLayoutWidth(), folderTile.GetTotalRowCount());
		}
		
		TileSpanLayoutParams tileBoxLayoutParams = new TileSpanLayoutParams(spanSize);
		
		tileBoxLayoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
		tileBoxLayoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;
		
		int TILE_MARGIN = SizeConverter.Current.GetTileMargin();
		tileBoxLayoutParams.setMargins(TILE_MARGIN, TILE_MARGIN, TILE_MARGIN, TILE_MARGIN);
		
		return tileBoxLayoutParams;
	}
	
	private int calculateTotalRowHeight(int rowCount)
	{
		if (rowCount <= 0)
			return 0;
		
		int TILE_MARGIN = SizeConverter.Current.GetTileMargin();
		int SMALL_TILE_SIZE = SizeConverter.Current.GetTileWidth(TileBase.TileSize.Small);
		
		return rowCount * SMALL_TILE_SIZE + (rowCount - 1) * (TILE_MARGIN * 2);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof BaseTileViewHolder))
			return false;
		
		BaseTileViewHolder other = (BaseTileViewHolder)obj;
		
		if (this.bindedTile == null || other.bindedTile == null)
			return other.getId() == this.getId();
		
		return this.bindedTile.equals(other.bindedTile);
	}
	
	@Override
	public int hashCode()
	{
		if (this.bindedTile != null)
			return (int)(this.bindedTile.hashCode() * this.getId());
		else
			return (int)this.getId();
	}
}
