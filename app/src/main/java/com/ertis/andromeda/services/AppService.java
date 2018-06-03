package com.ertis.andromeda.services;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.ertis.andromeda.AppDrawerActivity;
import com.ertis.andromeda.R;
import com.ertis.andromeda.adapters.AppMenuAdapter;
import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.helpers.Colors;
import com.ertis.andromeda.managers.AppsLoader;
import com.ertis.andromeda.models.AppMenuItem;
import com.ertis.andromeda.models.AppModel;
import com.ertis.andromeda.models.FolderTile;
import com.ertis.andromeda.models.Tile;

import org.json.JSONArray;
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

public class AppService implements IAppService, LoaderManager.LoaderCallbacks<ArrayList<AppModel>>
{
	private AppsLoader appLoader;
	private AppDrawerActivity appDrawer;
	private List<AppModel> appModelList;
	
	private TilesAdapter tilesAdapter;
	
	private AppMenuAdapter menuItemAdapter;
	
	private List<Tile> tileList = new ArrayList<>();
	private List<AppMenuItem> menuItemList = new ArrayList<>();
	
	public AppService(Context context)
	{
		this.appDrawer = (AppDrawerActivity) context;
		this.appModelList = new ArrayList<>();
		
		this.tilesAdapter = new TilesAdapter(this.appDrawer, tileList, this.appDrawer.getAppDrawerFragment());
		this.menuItemAdapter = new AppMenuAdapter(this.appDrawer, this.menuItemList);
		
		// create the loader to load the apps list in background
		this.appDrawer.getLoaderManager().initLoader(0, null, this);
	}
	
	public AppsLoader GetAppLoader()
	{
		return appLoader;
	}
	
	public List<AppModel> GetAppModelList()
	{
		return appModelList;
	}
	
	public List<Tile> GetTileList()
	{
		return tileList;
	}
	
	public List<AppMenuItem> GetMenuItemList()
	{
		return menuItemList;
	}
	
	public TilesAdapter GetTilesAdapter()
	{
		return tilesAdapter;
	}
	
	public AppMenuAdapter GetMenuItemAdapter()
	{
		return menuItemAdapter;
	}
	
	@Override
	public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle args)
	{
		this.appLoader = new AppsLoader(this.appDrawer);
		return this.appLoader;
	}
	
	@Override
	public void onLoadFinished(Loader<ArrayList<AppModel>> loader, ArrayList<AppModel> data)
	{
		this.tileList.clear();
		this.loadTiles(data);
		
		this.menuItemList.clear();
		this.loadMenuItemList(data);
	}
	
	@Override
	public void onLoaderReset(Loader<ArrayList<AppModel>> loader)
	{
		this.loadTiles(null);
		this.loadMenuItemList(null);
	}
	
	private void loadTiles(final ArrayList<AppModel> appList)
	{
		if (appList == null)
		{
			this.tileList.clear();
			this.tilesAdapter.notifyDataSetChanged();
			return;
		}
		
		try
		{
			this.tileList.clear();
			
			String jsonStr = this.ReadTileLayoutsFromJsonResource();
			this.tileList.addAll(this.ExtractTilesFromJson(jsonStr, appList));
		}
		catch (Exception ex)
		{
		
		}
		
		this.tilesAdapter.notifyDataSetChanged();
	}
	
	private List<Tile> ExtractTiles(JSONArray tiles, final ArrayList<AppModel> appList)
	{
		List<Tile> tileList = new ArrayList<>();
		
		try
		{
			for (int i = 0; i < tiles.length(); i++)
			{
				Tile tile = null;
				
				JSONObject tileData = tiles.getJSONObject(i);
				
				int tileTypeValue = tileData.getInt("tileSize");
				if (tileTypeValue < 0 || tileTypeValue >= Tile.TileSize.values().length)
					tileTypeValue = 0;
				
				Tile.TileSize tileSize = Tile.TileSize.values()[tileTypeValue];
				
				int tileStyleValue = tileData.getInt("tileStyle");
				if (tileStyleValue < 0 || tileStyleValue >= Tile.TileStyle.values().length)
					tileStyleValue = 0;
				
				Tile.TileStyle tileStyle = Tile.TileStyle.values()[tileStyleValue];
				
				String tileBackgroundStr = tileData.getString("tileBackground");
				ColorDrawable tileColor = Colors.rgb(tileBackgroundStr);
				if (tileBackgroundStr == null || tileBackgroundStr.isEmpty())
					tileColor = new ColorDrawable(this.appDrawer.getResources().getColor(R.color.colorTileBackground));
				
				if (!tileData.isNull("packageName"))
				{
					String appPackageName = tileData.getString("packageName");
					for (int a = 0; a < appList.size(); a++)
					{
						AppModel application = appList.get(a);
						if (appPackageName.equals(application.getApplicationPackageName()))
						{
							tile = new Tile(application, tileSize, tileColor, tileStyle);
							
							continue;
						}
					}
					
					if (tile == null)
					{
						tile = Tile.CreateFakeTile(tileSize);
					}
					
					String queryParams = tileData.getString("queryParams");
					tile.setQueryParams(queryParams);
					
					if (queryParams.equals("phoneDialer"))
					{
						Resources res = this.appDrawer.getResources();
						Drawable drawable = res.getDrawable(R.drawable.phone);
						tile.setCustomIcon(drawable);
						
						tile.setCustomLabel("Phone");
					}
				}
				else if (!tileData.isNull("folderName"))
				{
					String folderName = tileData.getString("folderName");
					tile = new FolderTile(folderName, tileSize);
					FolderTile folderTile = (FolderTile) tile;
					
					Resources res = this.appDrawer.getResources();
					Drawable drawable = res.getDrawable(R.drawable.tile_folder_bg);
					tile.setCustomIcon(drawable);
					
					JSONArray subTilesArray = tileData.getJSONArray("subTiles");
					List<Tile> subTiles = this.ExtractTiles(subTilesArray, appList);
					folderTile.AddTiles(subTiles);
				}
				
				tileList.add(tile);
			}
			
			return tileList;
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	private List<Tile> ExtractTilesFromJson(String jsonStr, final ArrayList<AppModel> appList)
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
	
	private void loadMenuItemList(ArrayList<AppModel> appList)
	{
		if (appList == null)
		{
			this.menuItemList.clear();
			this.menuItemAdapter.notifyDataSetChanged();
			return;
		}
		
		char lastHeaderChar = '?';
		for (int i = 0; i < appList.size(); i++)
		{
			AppModel application = appList.get(i);
			AppMenuItem item = new AppMenuItem(application);
			
			Character firstLetter = item.getLabel().charAt(0);
			if (!Character.isLetterOrDigit(firstLetter))
				firstLetter = '#';
			
			firstLetter = Character.toUpperCase(firstLetter);
			
			if (firstLetter != lastHeaderChar)
			{
				this.menuItemList.add(AppMenuItem.CreateHeaderMenuItem(firstLetter.toString()));
				lastHeaderChar = firstLetter;
			}
			
			this.menuItemList.add(item);
		}
		
		this.menuItemAdapter.notifyDataSetChanged();
	}
	
	private String ReadTileLayoutsFromJsonResource() throws IOException
	{
		InputStream is = this.appDrawer.getResources().openRawResource(R.raw.tiles_layout);
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
	
	private Tile.TileSize GetTileType(int index)
	{
		switch (index)
		{
			case 1:
				return Tile.TileSize.Medium;
			case 2:
				return Tile.TileSize.Medium;
			case 3:
				return Tile.TileSize.Small;
			case 4:
				return Tile.TileSize.Small;
			case 5:
				return Tile.TileSize.Small;
			case 6:
				return Tile.TileSize.Small;
			case 7:
				return Tile.TileSize.Medium;
			case 8:
				return Tile.TileSize.MediumWide;
			case 9:
				return Tile.TileSize.Small;
			case 10:
				return Tile.TileSize.Small;
			case 11:
				return Tile.TileSize.Medium;
			case 12:
				return Tile.TileSize.Medium;
			case 13:
				return Tile.TileSize.Small;
			case 14:
				return Tile.TileSize.Small;
			case 15:
				return Tile.TileSize.MediumWide;
			case 16:
				return Tile.TileSize.Medium;
			case 17:
				return Tile.TileSize.Large;
			case 18:
				return Tile.TileSize.Medium;
			case 19:
				return Tile.TileSize.Small;
			case 20:
				return Tile.TileSize.Small;
			case 21:
				return Tile.TileSize.Small;
			case 22:
				return Tile.TileSize.Small;
			
			case 23:
				return Tile.TileSize.Medium;
			case 24:
				return Tile.TileSize.Medium;
			case 25:
				return Tile.TileSize.Small;
			case 26:
				return Tile.TileSize.Small;
			default:
			{
				return this.GetTileType(index - 26);
			}
		}
	}
	
	private Tile.TileSize GetTileType2(int index)
	{
		switch (index)
		{
			case 1:
				return Tile.TileSize.Medium;
			case 2:
				return Tile.TileSize.Small;
			case 3:
				return Tile.TileSize.Small;
			case 4:
				return Tile.TileSize.Small;
			case 5:
				return Tile.TileSize.Small;
			case 6:
				return Tile.TileSize.MediumWide;
			case 7:
				return Tile.TileSize.Small;
			case 8:
				return Tile.TileSize.Small;
			case 9:
				return Tile.TileSize.Medium;
			case 10:
				return Tile.TileSize.Medium;
			case 11:
				return Tile.TileSize.Medium;
			case 12:
				return Tile.TileSize.Medium;
			case 13:
				return Tile.TileSize.MediumWide;
			case 14:
				return Tile.TileSize.Small;
			case 15:
				return Tile.TileSize.Small;
			case 16:
				return Tile.TileSize.Medium;
			case 17:
				return Tile.TileSize.Medium;
			case 18:
				return Tile.TileSize.Small;
			case 19:
				return Tile.TileSize.Small;
			case 20:
				return Tile.TileSize.MediumWide;
			case 21:
				return Tile.TileSize.Medium;
			case 22:
				return Tile.TileSize.Large;
			case 23:
				return Tile.TileSize.Medium;
			case 24:
				return Tile.TileSize.Small;
			case 25:
				return Tile.TileSize.Small;
			case 26:
				return Tile.TileSize.Small;
			
			default:
			{
				return this.GetTileType2(index - 26);
			}
		}
	}
}
