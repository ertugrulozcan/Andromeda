package com.aero.andromeda.managers;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;

import com.aero.andromeda.R;
import com.aero.andromeda.helpers.Colors;
import com.aero.andromeda.models.AppModel;
import com.aero.andromeda.models.tiles.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class TileListManager
{
	private static long ID_COUNTER = 1;
	
	private Context context;
	private List<AppModel> appModelList = new ArrayList<>();
	
	public TileListManager(Context context, List<AppModel> appList)
	{
		this.context = context;
		appModelList.addAll(appList);
	}
	
	public List<TileBase> GetTileList()
	{
		List<TileBase> tileList = new ArrayList<>();
		
		try
		{
			tileList.clear();
			
			String jsonStr = this.ReadTileLayoutsFromJsonResource();
			tileList.addAll(this.ExtractTilesFromJson(jsonStr, this.appModelList));
		}
		catch (Exception ex)
		{
		
		}
		
		return this.FillEmptyCells(tileList);
	}
	
	private List<TileBase> FillEmptyCells(final List<TileBase> tileList)
	{
		List<TileBase> filledList = new ArrayList<>();
		filledList.addAll(tileList);
		
		TileMap map = new TileMap(tileList);
		List<Integer> emptyCells = map.getEmptyCellIndexes();
		List<List<Point>> cellGroups = map.GetSequentialEmptyCellGroups();
		for (int i = 0; i < emptyCells.size(); i++)
		{
			if (i < cellGroups.size())
			{
				List<Point> group = cellGroups.get(i);
				int index = emptyCells.get(i);
				for (int j = 0; j < group.size(); j++)
				{
					filledList.add(index++, new FakeTile(ID_COUNTER++));
				}
			}
		}
		
		return filledList;
	}
	
	
	
	private List<TileBase> ExtractTilesFromJson(String jsonStr, final List<AppModel> appList)
	{
		try
		{
			JSONObject jsonObj = new JSONObject(jsonStr);
			JSONArray tiles = jsonObj.getJSONArray("tiles");
			
			return this.ExtractTiles(tiles, appList);
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	private List<TileBase> ExtractTiles(JSONArray tiles, final List<AppModel> appList)
	{
		List<TileBase> tileList = new ArrayList<>();
		
		try
		{
			for (int i = 0; i < tiles.length(); i++)
			{
				JSONObject tileData = tiles.getJSONObject(i);
				
				int tileSizeValue = tileData.getInt("tileSize");
				if (tileSizeValue < 0 || tileSizeValue >= TileBase.TileSize.values().length)
					tileSizeValue = 0;
				
				TileBase.TileSize tileSize = TileBase.TileSize.values()[tileSizeValue];
				
				int tileTypeValue = tileData.getInt("tileType");
				if (tileTypeValue < 0 || tileTypeValue >= TileBase.TileType.values().length)
					tileTypeValue = 0;
				
				TileBase.TileType tileType = TileBase.TileType.values()[tileTypeValue];
				
				String tileBackgroundStr = tileData.getString("tileBackground");
				ColorDrawable tileColor = Colors.rgb(tileBackgroundStr);
				if (tileBackgroundStr == null || tileBackgroundStr.isEmpty())
					tileColor = new ColorDrawable(this.context.getResources().getColor(R.color.colorTileBackground));
				
				String iconName = null;
				try { iconName = tileData.getString("iconName"); } catch (Exception ex) { }
				
				TileBase tile = null;
				
				if (!tileData.isNull("packageName"))
				{
					String appPackageName = tileData.getString("packageName");
					for (int a = 0; a < appList.size(); a++)
					{
						AppModel application = appList.get(a);
						if (appPackageName.equals(application.getApplicationPackageName()))
						{
							switch (tileType)
							{
								case Icon:
								{
									tile = new IconTile(ID_COUNTER++, application);
								}
								break;
								case Image:
								{
									tile = new ImageTile(ID_COUNTER++, application);
								}
								break;
								case LiveTile:
								{
									tile = new LiveTile(ID_COUNTER++, application);
								}
								break;
								case Folder:
								{
									tile = this.CreateFolderTile(tileData, appList);
								}
								break;
							}
							
							break;
						}
					}
				}
				else if (!tileData.isNull("folderName"))
				{
					tile = this.CreateFolderTile(tileData, appList);
				}
				
				if (tile != null)
				{
					tile.setTileSize(tileSize);
					
					if (tile.getTileType() != TileBase.TileType.FolderTile && tile.getTileType() != TileBase.TileType.Folder)
						tile.setTileColor(tileColor);
					
					if (tile.getTileType() == TileBase.TileType.Icon)
						((IconTile)tile).setIconName(iconName);
					
					tileList.add(tile);
				}
			}
			
			return tileList;
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	private FolderTile CreateFolderTile(final JSONObject tileData, final List<AppModel> appList) throws JSONException
	{
		String folderName = tileData.getString("folderName");
		
		FolderTile folderTile = new FolderTile(ID_COUNTER++);
		folderTile.setCaption(folderName);
		
		JSONArray subTilesArray = tileData.getJSONArray("subTiles");
		List<TileBase> allSubTiles = this.ExtractTiles(subTilesArray, appList);
		List<Tile> subTiles = new ArrayList<>();
		for (TileBase subTile : allSubTiles)
		{
			if (subTile instanceof Tile)
				subTiles.add((Tile)subTile);
		}
		
		folderTile.AddTiles(subTiles);
		
		return folderTile;
	}
	
	private String ReadTileLayoutsFromJsonResource() throws IOException
	{
		InputStream is = this.context.getResources().openRawResource(R.raw.tiles_layout);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		
		try
		{
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1)
			{
				writer.write(buffer, 0, n);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			is.close();
		}
		
		String jsonString = writer.toString();
		return jsonString;
	}
}
