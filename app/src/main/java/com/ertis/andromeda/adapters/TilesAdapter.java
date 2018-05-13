package com.ertis.andromeda.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ertis.andromeda.R;
import com.ertis.andromeda.managers.SpanLayoutParams;
import com.ertis.andromeda.managers.SpanSize;
import com.ertis.andromeda.managers.TileFolderManager;
import com.ertis.andromeda.models.Tile;
import com.ertis.andromeda.models.FolderTile;
import com.ertis.andromeda.models.TileFolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ertugrulozcan on 18.04.2018.
 */

public class TilesAdapter extends RecyclerView.Adapter<TilesAdapter.BaseTileViewHolder>
{
	private Context parentView;
	private List<Tile> tileList;
	private HashMap<View, Tile> tileViewDictionary;
	
	private View.OnClickListener onClickListener;
	private View.OnLongClickListener onLongClickListener;
	
	private static Typeface segoeTypeface;
	
	private int TILE_CODE = 124;
	private int FOLDER_CODE = 666;
	
	public TilesAdapter(Context context, List<Tile> tileList)
	{
		this.parentView = context;
		this.tileViewDictionary = new LinkedHashMap<>();
		this.tileList = tileList;
		
		segoeTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/segoewp/segoe-wp.ttf");
	}
	
	@Override
	public BaseTileViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		int layoutFileId = R.layout.tile;
		if (viewType == FOLDER_CODE)
			layoutFileId = R.layout.tile_folder;
		
		View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutFileId, parent, false);
		
		if (viewType != FOLDER_CODE)
		{
			itemView.setOnClickListener(this.onClickListener);
			itemView.setOnLongClickListener(this.onLongClickListener);
			
			return new TileViewHolder(itemView);
		}
		else
		{
			TileFolderViewHolder holder = new TileFolderViewHolder(itemView);
			TileFolderManager.Current.Construct(holder);
			return holder;
		}
	}
	
	@Override
	public void onBindViewHolder(BaseTileViewHolder baseHolder, int position)
	{
		Tile tile = this.tileList.get(position);
		
		if (baseHolder.itemView != null)
		{
			this.tileViewDictionary.put(baseHolder.itemView, tile);
		}
		
		baseHolder.tileBox.setLayoutParams(this.calculateTileBoxLayoutParams(tile));
		baseHolder.tileLayout.setLayoutParams(this.calculateTileBoxSpanLayoutParams(tile));
		baseHolder.tileLabel.setTypeface(segoeTypeface);
		
		if (!(tile instanceof TileFolder))
		{
			TileViewHolder holder = (TileViewHolder)baseHolder;
			
			ColorDrawable tileColor = tile.getTileColor();
			holder.tileBox.setBackground(tileColor);
			
			if (tile.getTileSize() != Tile.TileSize.Small)
				holder.tileLabel.setText(tile.getCaption());
			
			if (tile.getTileSize() == Tile.TileSize.Small)
			{
				holder.tileIconImageView.getLayoutParams().width = 90;
				holder.tileIconImageView.getLayoutParams().height = 90;
			}
			else
			{
				holder.tileIconImageView.getLayoutParams().width = 130;
				holder.tileIconImageView.getLayoutParams().height = 130;
			}
			
			if (tile.getTileStyle() == Tile.TileStyle.Icon)
			{
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				params.width = holder.tileIconImageView.getLayoutParams().width;
				params.height = holder.tileIconImageView.getLayoutParams().height;
				holder.tileIconImageView.setLayoutParams(params);
				
				holder.tileLabel.setVisibility(View.VISIBLE);
			}
			else if (tile.getTileStyle() == Tile.TileStyle.Image)
			{
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
				params.gravity = Gravity.FILL;
				holder.tileIconImageView.setLayoutParams(params);
				
				holder.tileLabel.setVisibility(View.INVISIBLE);
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
		else
		{
			TileFolderViewHolder holder = (TileFolderViewHolder)baseHolder;
			
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
			Activity activity = (Activity)this.parentView;
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
		if (this.tileList.get(position) instanceof TileFolder)
			return FOLDER_CODE;
		else
			return TILE_CODE;
	}
	
	public abstract class BaseTileViewHolder extends RecyclerView.ViewHolder
	{
		protected LinearLayout tileLayout;
		protected FrameLayout tileBox;
		protected TextView tileLabel;
		
		public BaseTileViewHolder(View itemView)
		{
			super(itemView);
			
			this.tileLayout = itemView.findViewById(R.id.tile_layout);
			this.tileBox = itemView.findViewById(R.id.tile_box);
			this.tileLabel = itemView.findViewById(R.id.tileLabel);
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
	
	public class TileFolderViewHolder extends BaseTileViewHolder
	{
		private View folderLayoutBase;
		private RecyclerView recyclerView;
		
		public View getFolderLayoutBase()
		{
			return folderLayoutBase;
		}
		
		public RecyclerView getRecyclerView()
		{
			return recyclerView;
		}
		
		public TileFolderViewHolder(View itemView)
		{
			super(itemView);
			this.folderLayoutBase = itemView;
			
			this.recyclerView = itemView.findViewById(R.id.folder_recycler_view);
		}
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
			spanSize = new SpanSize(6, 3);
		}
		
		SpanLayoutParams tileBoxLayoutParams = new SpanLayoutParams(spanSize);
		
		tileBoxLayoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
		tileBoxLayoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;
		
		int margin = 8;
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
					LinearLayout.LayoutParams tileBoxLayoutParams = new LinearLayout.LayoutParams(221, 221);
					int margin = 8;
					tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
					tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
					
					return tileBoxLayoutParams;
				}
				case Medium:
				{
					LinearLayout.LayoutParams tileBoxLayoutParams = new LinearLayout.LayoutParams(458, 458);
					int margin = 8;
					tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
					tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
					
					return tileBoxLayoutParams;
				}
				case MediumWide:
				{
					LinearLayout.LayoutParams tileBoxLayoutParams = new LinearLayout.LayoutParams(932, 458);
					int margin = 8;
					tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
					tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
					
					return tileBoxLayoutParams;
				}
				case Large:
				{
					LinearLayout.LayoutParams tileBoxLayoutParams = new LinearLayout.LayoutParams(932, 932);
					int margin = 8;
					tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
					tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
					
					return tileBoxLayoutParams;
				}
			}
		}
		else
		{
			LinearLayout.LayoutParams tileBoxLayoutParams = new LinearLayout.LayoutParams(1406, 695);
			int margin = 8;
			tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
			tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
			
			return tileBoxLayoutParams;
		}
		
		return null;
	}
}
