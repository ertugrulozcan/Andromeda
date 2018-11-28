package com.aero.andromeda.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.aero.andromeda.R;
import com.aero.andromeda.helpers.Colors;
import com.aero.andromeda.helpers.SizeConverter;
import com.aero.andromeda.models.tiles.FolderTile;
import com.aero.andromeda.models.tiles.Tile;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.utilities.GridLineView;

import java.util.ArrayList;
import java.util.List;

public class FolderTileViewHolder extends BaseTileViewHolder
{
	private TextView tileLabel;
	private GridLayout folderTileGridView;
	private GridLineView folderTileCover;
	private List<View> subTileViewList;
	
	public FolderTileViewHolder(@NonNull View itemView)
	{
		super(itemView);
		
		this.subTileViewList = new ArrayList<>();
		this.tileLabel = itemView.findViewById(R.id.tileLabel);
		this.folderTileGridView = itemView.findViewById(R.id.folder_tile_grid_view);
		this.folderTileCover = itemView.findViewById(R.id.folderTileGrid);
	}
	
	@Override
	protected void setLayoutProperties(TileBase tile)
	{
		FolderTile folderTile = (FolderTile) tile;
		List<Tile> subTiles = folderTile.getSubTiles();
		
		if (tile.getTileSize() == TileBase.TileSize.Small)
		{
			this.tileLabel.clearAnimation();
			this.tileLabel.setVisibility(View.INVISIBLE);
		}
		else
		{
			this.tileLabel.setText(tile.getCaption());
			this.tileLabel.clearAnimation();
			this.tileLabel.setVisibility(View.VISIBLE);
		}
		
		this.folderTileGridView.removeAllViews();
		
		for (int i = 0; i < subTiles.size(); i++)
		{
			TileBase subTile = subTiles.get(i);
			
			if (folderTile.getTileSize() == TileBase.TileSize.Small && i >= 3)
				break;
			if (folderTile.getTileSize() == TileBase.TileSize.Medium && i >= 5)
				break;
			if (folderTile.getTileSize() == TileBase.TileSize.MediumWide && i >= 11)
				break;
			if (folderTile.getTileSize() == TileBase.TileSize.Large && i >= 23)
				break;
			
			LayoutInflater inflater = (LayoutInflater) this.itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout thumbnailView = (LinearLayout) inflater.inflate(R.layout.folder_tile_thumbnail, null);
			ImageView imageView = thumbnailView.findViewById(R.id.thumbnail_image);
			
			int thumbnailSize = SizeConverter.Current.GetFolderTileThumbnailSize();
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
			
			int parentTileSize = SizeConverter.Current.GetTileWidth(folderTile.getTileSize());
			this.folderTileCover.getLayoutParams().height = (int) (parentTileSize * 2 / 3 + 1);
			
			this.folderTileGridView.addView(thumbnailView, i);
			this.subTileViewList.add(thumbnailView);
		}
	}
	
	public List<View> getSubTileViewList()
	{
		return subTileViewList;
	}
}
