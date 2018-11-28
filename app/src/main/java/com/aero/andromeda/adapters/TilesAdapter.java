package com.aero.andromeda.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aero.andromeda.Andromeda;
import com.aero.andromeda.R;
import com.aero.andromeda.helpers.OnStartDragListener;
import com.aero.andromeda.managers.TileFolderManager;
import com.aero.andromeda.models.TilesFooter;
import com.aero.andromeda.models.tiles.Folder;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.ui.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class TilesAdapter extends RecyclerView.Adapter<BaseTileViewHolder> implements ItemTouchHelperAdapter
{
	private final int ICON_TILE_CODE = 100;
	private final int IMAGE_TILE_CODE = 200;
	private final int LIVE_TILE_CODE = 300;
	private final int FOLDER_TILE_CODE = 400;
	private final int FOLDER_CODE = 900;
	
	private final int HEADER_CODE = 4000;
	private final int FOOTER_CODE = 5000;
	
	private OnStartDragListener dragStartListener;
	
	private List<TileBase> tileList;
	private HashMap<View, TileBase> tileViewDictionary;
	
	public TilesAdapter(List<TileBase> tileList, boolean isMainTilesAdapter, boolean hasStableIds)
	{
		//this.setHasStableIds(true);
		this.tileList = tileList;
		
		if (isMainTilesAdapter)
		{
			//this.tileList.add(0, new TilesHeader());
			this.tileList.add(new TilesFooter());
		}
		
		this.tileViewDictionary = new LinkedHashMap<>();
		
		this.setHasStableIds(hasStableIds);
	}
	
	public void setDragStartListener(OnStartDragListener onStartDragListener)
	{
		this.dragStartListener = onStartDragListener;
	}
	
	@Override
	public long getItemId(int position)
	{
		TileBase tile = this.tileList.get(position);
		if (tile.getTileType() == TileBase.TileType.Folder)
			return 9716;
		
		return tile.hashCode();
	}
	
	@NonNull
	@Override
	public BaseTileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
	{
		View itemView = null;
		
		switch (viewType)
		{
			case ICON_TILE_CODE:
			default:
			{
				View tileAdorner = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tile_adorner, viewGroup, false);
				ViewGroup adornerViewGroup = (FrameLayout)tileAdorner.findViewById(R.id.tileContentLayout);
				itemView = LayoutInflater.from(adornerViewGroup.getContext()).inflate(R.layout.tile, adornerViewGroup, false);
				adornerViewGroup.addView(itemView);
				
				if (itemView == null)
					return null;
				
				return new TileViewHolder(tileAdorner);
			}
			case IMAGE_TILE_CODE:
			{
				itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_tile, viewGroup, false);
				if (itemView == null)
					return null;
				
				return new ImageTileViewHolder(itemView);
			}
			case LIVE_TILE_CODE:
			{
				itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.live_tile, viewGroup, false);
				if (itemView == null)
					return null;
				
				return new LiveTileViewHolder(itemView);
			}
			case FOLDER_TILE_CODE:
			{
				itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.folder_tile, viewGroup, false);
				if (itemView == null)
					return null;
				
				return new FolderTileViewHolder(itemView);
			}
			case FOLDER_CODE:
			{
				itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.folder, viewGroup, false);
				if (itemView == null)
					return null;
				
				FolderViewHolder holder = new FolderViewHolder(itemView);
				TileFolderManager.Current.BindFolderViewHolder(holder);
				return holder;
			}
			case HEADER_CODE:
			{
				itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tiles_header, viewGroup, false);
				if (itemView == null)
					return null;
				
				return new TilesHeaderViewHolder(itemView);
			}
			case FOOTER_CODE:
			{
				itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tiles_footer, viewGroup, false);
				if (itemView == null)
					return null;
				
				return new TilesFooterViewHolder(itemView);
			}
		}
	}
	
	@Override
	public void onBindViewHolder(@NonNull final BaseTileViewHolder baseHolder, int i)
	{
		if (baseHolder == null)
			return;
		
		TileBase tile = this.tileList.get(i);
		if (tile == null)
			return;
		
		View itemView = baseHolder.getItemView();
		this.OnTileViewHolderChanged(tile, itemView);
		
		baseHolder.bindViewHolder(tile, i, this.dragStartListener);
	}
	
	@Override
	public void onItemDismiss(int position)
	{
	
	}
	
	@Override
	public boolean onItemMove(int fromPosition, int toPosition)
	{
		Collections.swap(this.tileList, fromPosition, toPosition);
		notifyItemMoved(fromPosition, toPosition);
		
		return true;
	}
	
	@Override
	public int getItemViewType(int position)
	{
		TileBase tileBase = this.tileList.get(position);
		switch (tileBase.getTileType())
		{
			case Icon:
				return ICON_TILE_CODE;
			case Image:
				return IMAGE_TILE_CODE;
			case LiveTile:
				return LIVE_TILE_CODE;
			case FolderTile:
				return FOLDER_TILE_CODE;
			case Folder:
				return FOLDER_CODE;
			case TilesHeader:
				return HEADER_CODE;
			case TilesFooter:
				return FOOTER_CODE;
			default:
				return ICON_TILE_CODE;
		}
	}
	
	@Override
	public int getItemCount()
	{
		return tileList.size();
	}
	
	public TileBase getItem(int index)
	{
		if (index >= 0 && index < tileList.size())
			return tileList.get(index);
		else
			return null;
	}
	
	public int getItemIndex(TileBase tile)
	{
		return this.tileList.indexOf(tile);
	}
	
	public void InsertTile(TileBase tile, int index)
	{
		this.tileList.add(index, tile);
		this.notifyItemInserted(index);
	}
	
	public void RemoveTile(int index)
	{
		if (index >= 0)
		{
			this.tileList.remove(index);
			this.notifyItemRemoved(index);
		}
	}
	
	public TileBase getDataContext(View view)
	{
		if (this.tileViewDictionary.containsKey(view))
			return this.tileViewDictionary.get(view);
		
		return null;
	}
	
	public List<View> getTileViewList()
	{
		return new ArrayList<>(this.tileViewDictionary.keySet());
	}
	
	public View getTileView(int index)
	{
		List<View> viewSet = this.getTileViewList();
		if (index >= 0 && index < viewSet.size())
			return viewSet.get(index);
		else
			return null;
	}
	
	private void OnTileViewHolderChanged(TileBase tile, View newView)
	{
		if (newView == null)
			return;
		
		if (this.tileViewDictionary.containsValue(tile))
		{
			List<View> views = getTileViewList();
			for (View view : views)
			{
				if (this.tileViewDictionary.get(view).equals(tile))
				{
					if (!view.equals(newView))
					{
						this.tileViewDictionary.remove(view);
						
						if (!this.tileViewDictionary.containsKey(newView))
						{
							this.tileViewDictionary.put(newView, tile);
						}
						
						//this.notifyItemChanged(this.getItemIndex(tile));
					}
					
					break;
				}
			}
		}
		
		if (!this.tileViewDictionary.containsKey(newView))
		{
			this.tileViewDictionary.put(newView, tile);
		}
	}
}
