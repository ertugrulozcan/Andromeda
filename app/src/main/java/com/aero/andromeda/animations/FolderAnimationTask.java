package com.aero.andromeda.animations;

import android.os.AsyncTask;

import com.aero.andromeda.managers.TileFolderManager;
import com.aero.andromeda.models.tiles.FolderTile;

public class FolderAnimationTask extends AsyncTask<FolderTile, Integer, FolderTile>
{
	private TileFolderManager tileFolderManager;
	
	public FolderAnimationTask(TileFolderManager tileFolderManager)
	{
		this.tileFolderManager = tileFolderManager;
	}
	
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
	}
	
	@Override
	protected FolderTile doInBackground(FolderTile... params)
	{
		if (params != null && params.length > 0)
		{
			return params[0];
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(FolderTile folderTile)
	{
		super.onPostExecute(folderTile);
		
		this.tileFolderManager.OnClickFolderTile(folderTile);
	}
	
	@Override
	protected void onCancelled(FolderTile folderTile)
	{
		super.onCancelled(folderTile);
	}
}

