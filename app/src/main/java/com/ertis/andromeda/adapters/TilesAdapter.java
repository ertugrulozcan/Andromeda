package com.ertis.andromeda.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ertis.andromeda.R;
import com.ertis.andromeda.helpers.SizeConverter;
import com.ertis.andromeda.listeners.OnStartDragListener;
import com.ertis.andromeda.managers.SpanLayoutParams;
import com.ertis.andromeda.managers.SpanSize;
import com.ertis.andromeda.managers.TileFolderManager;
import com.ertis.andromeda.managers.TileOrderManager;
import com.ertis.andromeda.models.FolderTile;
import com.ertis.andromeda.models.Tile;
import com.ertis.andromeda.models.TileFolder;
import com.ertis.andromeda.services.ServiceLocator;
import com.ertis.andromeda.utilities.GridLineView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ertugrulozcan on 18.04.2018.
 */

public class TilesAdapter extends RecyclerView.Adapter<TilesAdapter.BaseTileViewHolder> implements ItemTouchHelperAdapter
{
	private static Typeface segoeTypeface;
	
	private int TILE_CODE = 124;
	private int FOLDER_TILE_CODE = 336;
	private int TILE_FOLDER_CODE = 666;
	
	private int SMALL_TILE_SIZE = 221;
	private int MEDIUM_TILE_SIZE = 458;
	private int WIDE_TILE_SIZE = 932;
	private int FULL_TILE_SIZE = 1406;
	private int TILE_MARGIN = 8;
	
	private TileOrderManager tileOrderManager;
	
	private Context parentView;
	private List<Tile> tileList;
	private HashMap<View, Tile> tileViewDictionary;
	
	private View.OnClickListener onClickListener;
	private View.OnLongClickListener onLongClickListener;
	private OnStartDragListener dragStartListener;
	
	public TilesAdapter(Context context, List<Tile> tileList)
	{
		this.parentView = context;
		this.tileViewDictionary = new LinkedHashMap<>();
		this.tileList = tileList;
		
		this.SetTileSizes(context);
		
		segoeTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/segoewp/segoe-wp.ttf");
	}
	
	public void setDragStartListener(OnStartDragListener dragStartListener)
	{
		this.dragStartListener = dragStartListener;
	}
	
	private void SetTileSizes(Context context)
	{
		this.SMALL_TILE_SIZE = SizeConverter.GetTileWidth(context, Tile.TileSize.Small);
		this.MEDIUM_TILE_SIZE = SizeConverter.GetTileWidth(context, Tile.TileSize.Medium);
		this.WIDE_TILE_SIZE = SizeConverter.GetTileWidth(context, Tile.TileSize.MediumWide);
		this.FULL_TILE_SIZE = SizeConverter.GetTilePanelFullWidth(context);
		this.TILE_MARGIN = SizeConverter.GetTileMargin(context);
	}
	
	public TileOrderManager getTileOrderManager()
	{
		if (this.tileOrderManager == null)
			this.tileOrderManager = ServiceLocator.Current().GetInstance(TileOrderManager.class);
		
		return tileOrderManager;
	}
	
	@Override
	public BaseTileViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		int layoutFileId = R.layout.tile;
		
		if (viewType == FOLDER_TILE_CODE)
			layoutFileId = R.layout.folder_tile;
		if (viewType == TILE_FOLDER_CODE)
			layoutFileId = R.layout.tile_folder;
		
		View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutFileId, parent, false);
		if (itemView == null)
			return null;
		
		if (viewType != TILE_FOLDER_CODE)
		{
			itemView.setOnClickListener(this.onClickListener);
			itemView.setOnLongClickListener(this.onLongClickListener);
			
			if (viewType == TILE_CODE)
			{
				return new TileViewHolder(itemView);
			}
			else
			{
				return new FolderTileViewHolder(itemView);
			}
		}
		else
		{
			TileFolderViewHolder holder = new TileFolderViewHolder(itemView);
			TileFolderManager.Current.Construct(holder);
			return holder;
		}
	}
	
	@Override
	public void onBindViewHolder(final BaseTileViewHolder baseHolder, int position)
	{
		if (baseHolder == null)
			return;
		
		Tile tile = this.tileList.get(position);
		if (tile == null)
			return;
		
		tile.setViewHolder(baseHolder);
		
		if (baseHolder.itemView != null && !this.tileViewDictionary.containsKey(baseHolder.itemView))
		{
			this.tileViewDictionary.put(baseHolder.itemView, tile);
		}
		
		if (!(tile instanceof TileFolder))
		{
			if (tile.getTileSize() != Tile.TileSize.Small)
				baseHolder.tileLabel.setText(tile.getCaption());
			
			ColorDrawable tileColor = tile.getTileColor();
			baseHolder.tileBox.setBackground(tileColor);
			
			// IsTile
			if (!(tile instanceof FolderTile))
			{
				TileViewHolder holder = (TileViewHolder) baseHolder;
				
				if (tile.getTileSize() == Tile.TileSize.Small)
				{
					holder.tileIconImageView.getLayoutParams().width = 90;
					holder.tileIconImageView.getLayoutParams().height = 90;
					holder.tileLabel.clearAnimation();
					holder.tileLabel.setVisibility(View.INVISIBLE);
				}
				else
				{
					holder.tileIconImageView.getLayoutParams().width = 130;
					holder.tileIconImageView.getLayoutParams().height = 130;
					holder.tileLabel.clearAnimation();
					holder.tileLabel.setVisibility(View.VISIBLE);
				}
				
				if (tile.getTileStyle() == Tile.TileStyle.Icon)
				{
					FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.CENTER;
					params.width = holder.tileIconImageView.getLayoutParams().width;
					params.height = holder.tileIconImageView.getLayoutParams().height;
					holder.tileIconImageView.setLayoutParams(params);
				}
				else if (tile.getTileStyle() == Tile.TileStyle.Image)
				{
					FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
					params.gravity = Gravity.FILL;
					holder.tileIconImageView.setLayoutParams(params);
				}
				else if (tile.getTileStyle() == Tile.TileStyle.LiveTile)
				{

				}
				
				Drawable icon = tile.getIcon();
				if (icon != null)
					holder.tileIconImageView.setImageDrawable(icon);
				else
					holder.tileIconImageView.setImageResource(tile.getIconId());
				
				holder.tileIconImageView.requestLayout();
			}
			// IsFolder
			else
			{
				FolderTileViewHolder folderTileViewHolder = (FolderTileViewHolder) baseHolder;
				
				FolderTile folderTile = (FolderTile) tile;
				List<Tile> subTiles = folderTile.getSubTiles();
				
				folderTileViewHolder.folderTileGridView.removeAllViews();
				
				for (int i = 0; i < subTiles.size(); i++)
				{
					Tile subTile = subTiles.get(i);
					
					if (folderTile.getTileSize() == Tile.TileSize.Small && i >= 3)
						break;
					if (folderTile.getTileSize() == Tile.TileSize.Medium && i >= 5)
						break;
					if (folderTile.getTileSize() == Tile.TileSize.MediumWide && i >= 11)
						break;
					if (folderTile.getTileSize() == Tile.TileSize.Large && i >= 23)
						break;
					
					LayoutInflater inflater = (LayoutInflater) baseHolder.itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					LinearLayout thumbnailView = (LinearLayout) inflater.inflate(R.layout.folder_tile_thumbnail, null);
					ImageView imageView = thumbnailView.findViewById(R.id.thumbnail_image);
					
					int thumbnailSize = SizeConverter.GetFolderTileThumbnailSize(baseHolder.itemView.getContext());
					int thumbnailImageSize = (int) (thumbnailSize * 0.6f);
					int thumbnailImageMargin = (int) (thumbnailSize * 0.2f);
					
					LinearLayout.LayoutParams thumbnailLayoutParams = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
					thumbnailView.setLayoutParams(thumbnailLayoutParams);
					thumbnailView.setBackgroundColor(subTile.getTileColor().getColor());
					
					LinearLayout.LayoutParams thumbnailImageLayoutParams = new LinearLayout.LayoutParams(thumbnailImageSize, thumbnailImageSize);
					thumbnailImageLayoutParams.gravity = Gravity.CENTER;
					thumbnailImageLayoutParams.setMargins(thumbnailImageMargin, thumbnailImageMargin, thumbnailImageMargin, thumbnailImageMargin);
					imageView.setLayoutParams(thumbnailImageLayoutParams);
					imageView.setImageDrawable(subTile.getIcon());
					
					int parentTileSize = SizeConverter.GetTileWidth(baseHolder.itemView.getContext(), folderTile.getTileSize());
					folderTileViewHolder.folderTileCover.getLayoutParams().height = (int) (parentTileSize * 2 / 3 + 1);
					
					folderTileViewHolder.folderTileGridView.addView(thumbnailView, i);
				}
			}
			
			
			baseHolder.tileBox.setLayoutParams(this.calculateTileBoxLayoutParams(tile));
			baseHolder.tileLayout.setLayoutParams(this.calculateTileBoxSpanLayoutParams(tile));
			baseHolder.tileLabel.setTypeface(segoeTypeface);
			baseHolder.tileBox.requestLayout();
			
			/*
			if (this.getTileOrderManager().isEditMode())
			{
				baseHolder.onEditMode();
			}
			*/
			
			/*
			baseHolder.itemView.setOnTouchListener(new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					if(event.getAction() == MotionEvent.ACTION_DOWN)
					{
						tileClickTime = (Long) System.currentTimeMillis();
					}
					else if(event.getAction() == MotionEvent.ACTION_UP)
					{
						if(((Long) System.currentTimeMillis() - tileClickTime) > 4000)
						{
							if (dragStartListener != null && MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN)
							{
								dragStartListener.onStartDrag(baseHolder);
							}
							
							return true;
						}
					}
					
					return false;
				}
			});
			*/
		}
		else
		{
			TileFolderViewHolder holder = (TileFolderViewHolder) baseHolder;
			
			if (tile.getTileSize() != Tile.TileSize.Small)
				holder.tileLabel.setText(tile.getCaption());
		}
	}
	
	public void setOnClickListener(View.OnClickListener onClickListener)
	{
		this.onClickListener = onClickListener;
	}
	
	public void setOnLongClickListener(View.OnLongClickListener onLongClickListener)
	{
		this.onLongClickListener = onLongClickListener;
	}
	
	public void InsertTile(Tile tile, int index)
	{
		this.tileList.add(index, tile);
		this.notifyItemInserted(index);
	}
	
	public void RemoveTile(int index)
	{
		this.tileList.remove(index);
		this.notifyItemRemoved(index);
	}
	
	public Tile getDataContext(View view)
	{
		if (this.tileViewDictionary.containsKey(view))
			return this.tileViewDictionary.get(view);
		
		return null;
	}
	
	public List<View> getTileViewList()
	{
		return new ArrayList<>(tileViewDictionary.keySet());
	}
	
	public void ScrollToItem(int index)
	{
		if (this.parentView instanceof Activity)
		{
			Activity activity = (Activity) this.parentView;
			RecyclerView recyclerView = activity.findViewById(R.id.recycler_view);
			recyclerView.smoothScrollToPosition(index);
		}
	}
	
	@Override
	public int getItemCount()
	{
		return this.tileList.size();
	}
	
	public int getItemIndex(Tile tile)
	{
		return this.tileList.indexOf(tile);
	}
	
	@Override
	public int getItemViewType(int position)
	{
		Tile tileBase = this.tileList.get(position);
		
		if (tileBase instanceof TileFolder)
			return TILE_FOLDER_CODE;
		else if (tileBase.getTileType() == Tile.TileType.FolderTile)
			return FOLDER_TILE_CODE;
		else
			return TILE_CODE;
	}
	
	@Override
	public void onItemDismiss(int position)
	{
		tileList.remove(position);
		notifyItemRemoved(position);
	}
	
	@Override
	public boolean onItemMove(int fromPosition, int toPosition)
	{
		Collections.swap(tileList, fromPosition, toPosition);
		notifyItemMoved(fromPosition, toPosition);
		
		return true;
	}
	
	public int GetItemColumnSpanSize(int position)
	{
		Tile tile = this.tileList.get(position);
		switch (tile.getTileSize())
		{
			case Small:
			{
				return 1;
			}
			case Medium:
			{
				return 2;
			}
			case MediumWide:
			{
				return 4;
			}
			case Large:
			{
				return 4;
			}
		}
		
		return 6;
	}
	
	public SpanSize GetItemSpanSize(int position)
	{
		Tile tile = this.tileList.get(position);
		switch (tile.getTileSize())
		{
			case Small:
			{
				return new SpanSize(1, 1);
			}
			case Medium:
			{
				return new SpanSize(2, 2);
			}
			case MediumWide:
			{
				return new SpanSize(4, 2);
			}
			case Large:
			{
				return new SpanSize(4, 4);
			}
		}
		
		return new SpanSize(6, 2);
	}
	
	private SpanLayoutParams calculateTileBoxSpanLayoutParams(Tile tile)
	{
		SpanSize spanSize = new SpanSize(1, 1);

		if (!(tile instanceof TileFolder))
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
			TileFolder folderTile = (TileFolder) tile;
			spanSize = new SpanSize(6, folderTile.GetTotalRowCount());
		}

		SpanLayoutParams tileBoxLayoutParams = new SpanLayoutParams(spanSize);

		tileBoxLayoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
		tileBoxLayoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;

		int margin = TILE_MARGIN;
		tileBoxLayoutParams.setMargins(margin, margin, margin, margin);

		return tileBoxLayoutParams;
	}
	
	private LinearLayout.LayoutParams calculateTileBoxLayoutParams(Tile tile)
	{
		if (!(tile instanceof TileFolder))
		{
			switch (tile.getTileSize())
			{
				case Small:
				{
					LinearLayout.LayoutParams tileBoxLayoutParams = new LinearLayout.LayoutParams(SMALL_TILE_SIZE, SMALL_TILE_SIZE);
					int margin = TILE_MARGIN;
					tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
					tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

					return tileBoxLayoutParams;
				}
				case Medium:
				{
					LinearLayout.LayoutParams tileBoxLayoutParams = new LinearLayout.LayoutParams(MEDIUM_TILE_SIZE, MEDIUM_TILE_SIZE);
					int margin = TILE_MARGIN;
					tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
					tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

					return tileBoxLayoutParams;
				}
				case MediumWide:
				{
					LinearLayout.LayoutParams tileBoxLayoutParams = new LinearLayout.LayoutParams(WIDE_TILE_SIZE, MEDIUM_TILE_SIZE);
					int margin = TILE_MARGIN;
					tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
					tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

					return tileBoxLayoutParams;
				}
				case Large:
				{
					LinearLayout.LayoutParams tileBoxLayoutParams = new LinearLayout.LayoutParams(WIDE_TILE_SIZE, WIDE_TILE_SIZE);
					int margin = TILE_MARGIN;
					tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
					tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

					return tileBoxLayoutParams;
				}
			}
		}
		else
		{
			TileFolder folderTile = (TileFolder) tile;
			int height = this.CalculateTotalRowHeight(folderTile.GetTotalRowCount());
			LinearLayout.LayoutParams tileBoxLayoutParams = new LinearLayout.LayoutParams(FULL_TILE_SIZE, height);
			int margin = TILE_MARGIN;
			tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
			tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;

			return tileBoxLayoutParams;
		}

		return null;
	}
	
	private int CalculateTotalRowHeight(int rowCount)
	{
		if (rowCount <= 0)
			return 0;

		return rowCount * SMALL_TILE_SIZE + (rowCount - 1) * (TILE_MARGIN * 2);
	}
	
	public abstract class BaseTileViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder
	{
		protected View itemView;
		protected LinearLayout tileLayout;
		protected FrameLayout tileBox;
		protected TextView tileLabel;

		public BaseTileViewHolder(View itemView)
		{
			super(itemView);

			this.itemView = itemView;
			this.tileLayout = itemView.findViewById(R.id.tile_layout);
			this.tileBox = itemView.findViewById(R.id.tile_box);
			this.tileLabel = itemView.findViewById(R.id.tileLabel);
		}

		@Override
		public void onItemSelected()
		{

		}

		@Override
		public void onItemClear()
		{

		}
		
		private void setOpacity(float value)
		{
			this.tileLayout.setAlpha(value);
		}
		
		public void onEditMode()
		{
			this.setOpacity(0.7f);
			this.tileLayout.setPadding(27,27,27,27);
		}
	}
	
	public class TileViewHolder extends BaseTileViewHolder
	{
		private ImageView tileIconImageView;

		public TileViewHolder(View itemView)
		{
			super(itemView);

			this.tileIconImageView = itemView.findViewById(R.id.tile_icon);
		}
	}
	
	public class FolderTileViewHolder extends BaseTileViewHolder
	{
		private GridLayout folderTileGridView;
		private GridLineView folderTileCover;

		public FolderTileViewHolder(View itemView)
		{
			super(itemView);

			this.folderTileGridView = itemView.findViewById(R.id.folder_tile_grid_view);
			this.folderTileCover = itemView.findViewById(R.id.folderTileGrid);
		}
	}
	
	public class TileFolderViewHolder extends BaseTileViewHolder
	{
		private RecyclerView recyclerView;

		public TileFolderViewHolder(View itemView)
		{
			super(itemView);
			this.itemView = itemView;

			this.recyclerView = itemView.findViewById(R.id.folder_recycler_view);
		}

		public View getFolderLayoutBase()
		{
			return itemView;
		}

		public RecyclerView getRecyclerView()
		{
			return recyclerView;
		}
	}
}
