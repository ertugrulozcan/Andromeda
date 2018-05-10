package com.ertis.andromeda.adapters;

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
import com.ertis.andromeda.models.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ertugrulozcan on 18.04.2018.
 */

public class TilesAdapter extends RecyclerView.Adapter<TilesAdapter.TileViewHolder>
{
	private Context parentView;
	private List<Tile> tileList;
	private HashMap<View, Tile> tileViewDictionary;
	
	private View.OnClickListener onClickListener;
	private View.OnLongClickListener onLongClickListener;
	
	private static Typeface segoeTypeface;
	
	public TilesAdapter(Context context, List<Tile> tileList)
	{
		this.parentView = context;
		this.tileViewDictionary = new LinkedHashMap<>();
		this.tileList = tileList;
		
		segoeTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/segoewp/segoe-wp.ttf");
	}
	
	@Override
	public TileViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile, parent, false);
		itemView.setOnClickListener(this.onClickListener);
		itemView.setOnLongClickListener(this.onLongClickListener);
		
		return new TileViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(TileViewHolder holder, int position)
	{
		Tile tile = this.tileList.get(position);
		
		if (holder.itemView != null)
		{
			this.tileViewDictionary.put(holder.itemView, tile);
		}
		
		holder.tileBox.setLayoutParams(this.calculateTileBoxLayoutParams(tile.getTileType()));
		holder.tileLayout.setLayoutParams(this.calculateTileBoxSpanLayoutParams(tile.getTileType()));
		
		ColorDrawable tileColor = tile.getTileColor();
		holder.tileBox.setBackground(tileColor);
		
		holder.tileLabel.setTypeface(segoeTypeface);
		if (tile.getTileType() != Tile.TileType.Small)
			holder.tileLabel.setText(tile.getCaption());
		
		if (tile.getTileType() == Tile.TileType.Small)
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
	
	public void setOnClickListener(View.OnClickListener onClickListener)
	{
		this.onClickListener = onClickListener;
	}
	
	public void setOnLongClickListener(View.OnLongClickListener onLongClickListener)
	{
		this.onLongClickListener = onLongClickListener;
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
	
	@Override
	public int getItemCount()
	{
		return this.tileList.size();
	}
	
	public class TileViewHolder extends RecyclerView.ViewHolder
	{
		private LinearLayout tileLayout;
		private FrameLayout tileBox;
		private TextView tileLabel;
		private ImageView tileIconImageView;
		
		public TileViewHolder(View itemView)
		{
			super(itemView);
			
			this.tileLayout = itemView.findViewById(R.id.tile_layout);
			this.tileBox = itemView.findViewById(R.id.tile_box);
			this.tileLabel = itemView.findViewById(R.id.tileLabel);
			this.tileIconImageView = itemView.findViewById(R.id.tile_icon);
		}
	}
	
	private SpanLayoutParams calculateTileBoxSpanLayoutParams(Tile.TileType tileType)
	{
		SpanSize spanSize = new SpanSize(1, 1);
		
		switch (tileType)
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
			case Big:
			{
				spanSize = new SpanSize(4, 4);
			}
			break;
		}
		
		SpanLayoutParams tileBoxLayoutParams = new SpanLayoutParams(spanSize);
		
		tileBoxLayoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
		tileBoxLayoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;
		
		int margin = 8;
		tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
		
		return tileBoxLayoutParams;
	}
	
	private LinearLayout.LayoutParams calculateTileBoxLayoutParams(Tile.TileType tileType)
	{
		switch (tileType)
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
			case Big:
			{
				LinearLayout.LayoutParams tileBoxLayoutParams = new LinearLayout.LayoutParams(932, 932);
				int margin = 8;
				tileBoxLayoutParams.setMargins(margin, margin, margin, margin);
				tileBoxLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
				
				return tileBoxLayoutParams;
			}
		}
		
		return null;
	}
	
	private GridLayout.LayoutParams calculateTileLayoutParams(Tile.TileType tileType)
	{
		GridLayout.LayoutParams param = new GridLayout.LayoutParams();
		param.height = GridLayout.LayoutParams.WRAP_CONTENT;
		param.width = GridLayout.LayoutParams.MATCH_PARENT;
		
		switch (tileType)
		{
			case Small:
			{
				param.columnSpec = GridLayout.spec(1, 1);
				param.rowSpec = GridLayout.spec(1, 1);
			}
			case Medium:
			{
				param.columnSpec = GridLayout.spec(1, 2);
				param.rowSpec = GridLayout.spec(1, 2);
			}
			case MediumWide:
			{
				param.columnSpec = GridLayout.spec(1, 4);
				param.rowSpec = GridLayout.spec(1, 2);
			}
			case Big:
			{
				param.columnSpec = GridLayout.spec(1, 4);
				param.rowSpec = GridLayout.spec(1, 4);
			}
		}
		
		return param;
	}
}
