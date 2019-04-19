package com.aero.andromeda;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aero.andromeda.adapters.TilesAdapter;
import com.aero.andromeda.animations.TileAnimationManager;
import com.aero.andromeda.helpers.OnStartDragListener;
import com.aero.andromeda.helpers.TileTouchHelperCallback;
import com.aero.andromeda.managers.SpannableGridLayoutManager;
import com.aero.andromeda.managers.TilesLayoutManager;
import com.aero.andromeda.managers.TileFolderManager;
import com.aero.andromeda.managers.TilesLayoutManagerBase;
import com.aero.andromeda.models.tiles.TileBase;
import com.aero.andromeda.services.ServiceLocator;
import com.aero.andromeda.services.interfaces.IAppService;
import com.aero.andromeda.services.interfaces.ISettingsService;
import com.aero.andromeda.settings.UISettings;
import com.aero.andromeda.ui.BaseTileViewHolder;

public class AppDrawerFragment extends Fragment implements OnStartDragListener
{
	private RecyclerView recyclerView;
	private TilesAdapter tilesAdapter;
	private TilesLayoutManagerBase gridLayoutManager;
	private ItemTouchHelper itemTouchHelper;
	private boolean isEnabled = true;
	
	public AppDrawerFragment()
	{
		Bundle args = new Bundle();
		this.setArguments(args);
		
		ServiceLocator.Current().RegisterInstance(this);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			// mParam1 = getArguments().getString(ARG_PARAM1);
			// mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	    View view = inflater.inflate(R.layout.fragment_app_drawer, container, false);
        this.recyclerView = view.findViewById(R.id.recycler_view);

		this.SetTilesRecycler();
		
		ItemTouchHelper.Callback callback = new TileTouchHelperCallback(this.tilesAdapter);
		this.itemTouchHelper = new ItemTouchHelper(callback);
		this.itemTouchHelper.attachToRecyclerView(recyclerView);
		this.tilesAdapter.setDragStartListener(this);
		
		return view;
	}

	private void SetTilesRecycler()
    {
        final IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
        this.gridLayoutManager = this.CreateTilesLayoutManager(appService);

        this.tilesAdapter = appService.getTilesAdapter();
        this.recyclerView.setLayoutManager(this.gridLayoutManager);
        this.recyclerView.setAdapter(this.tilesAdapter);
    }

	private TilesLayoutManagerBase CreateTilesLayoutManager(final IAppService appService)
    {
        boolean useNewImplementation = false;

        final ISettingsService settingsService = ServiceLocator.Current().GetInstance(ISettingsService.class);
        final UISettings uiSettings = settingsService.getUISettings();

        if (useNewImplementation)
        {
            final int columnCount = uiSettings.getLayoutWidth();
            return new SpannableGridLayoutManager(new SpannableGridLayoutManager.GridSpanLookup()
            {
                @Override
                public SpannableGridLayoutManager.SpanInfo getSpanInfo(int position)
                {
                    TileBase tile = appService.getTileList().get(position);
                    TilesLayoutManager.SpanSize tileSpan = BaseTileViewHolder.GetSpanSize(tile, columnCount);
                    return new SpannableGridLayoutManager.SpanInfo(tileSpan.getWidth(), tileSpan.getHeight());
                }
            }, columnCount, 1f);
        }
        else
        {
            TilesLayoutManager tilesLayoutManager = new TilesLayoutManager(TilesLayoutManager.Orientation.VERTICAL, uiSettings.getLayoutWidth());
            tilesLayoutManager.setItemOrderIsStable(true);

            return tilesLayoutManager;
        }
    }

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
	}
	
	public View GetContainer(View view)
	{
		return this.recyclerView.findContainingItemView(view);
	}
	
	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
	}
	
	@Override
	public void onStartDrag(RecyclerView.ViewHolder viewHolder)
	{
		if (this.itemTouchHelper != null)
			this.itemTouchHelper.startDrag(viewHolder);
	}

    private final int REQUEST_PERMISSION_PHONE_STATE = 1;

	public void Enable()
	{
        // this.CheckNotificationAccessPermission();

		TileAnimationManager.Current().Start();
		this.isEnabled = true;
	}

	private void CheckNotificationAccessPermission()
    {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE))
            {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                showExplanation("Permission Needed", "Rationale", Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE, REQUEST_PERMISSION_PHONE_STATE);
            }
            else
            {
                // No explanation needed, we can request the permission.
                requestPermission(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE, REQUEST_PERMISSION_PHONE_STATE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this.getActivity(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this.getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void showExplanation(String title, String message, final String permission, final int permissionRequestCode)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(title).setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                requestPermission(permission, permissionRequestCode);
            }
        });

        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode)
    {
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{permissionName}, permissionRequestCode);
    }
	
	public void Disable()
	{
		this.isEnabled = false;
		TileAnimationManager.Current().Stop();
	}
	
	public boolean isEnabled()
	{
		return this.isEnabled;
	}
	
	public void ScrollToTop()
	{
		this.recyclerView.smoothScrollToPosition(0);
	}
	
	public void ScrollToBottom()
	{
		IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
		TilesAdapter tilesAdapter = appService.getTilesAdapter();
		this.recyclerView.smoothScrollToPosition(tilesAdapter.getItemCount() - 1);
	}
	
	public void RefreshLayout(boolean aggressiveRefresh)
	{
		TileFolderManager.Current.CloseFolder();
		
		if (aggressiveRefresh)
		{
            final IAppService appService = ServiceLocator.Current().GetInstance(IAppService.class);
			ISettingsService settingsService = ServiceLocator.Current().GetInstance(ISettingsService.class);
			UISettings uiSettings = settingsService.getUISettings();

            if (this.gridLayoutManager.getSpanCount() != uiSettings.getLayoutWidth())
            {
                this.gridLayoutManager = this.CreateTilesLayoutManager(appService);
            }

            this.recyclerView.setAdapter(null);
            this.recyclerView.setLayoutManager(null);
            this.recyclerView.setAdapter(this.tilesAdapter);
            this.recyclerView.setLayoutManager(this.gridLayoutManager);
			
			synchronized (this.recyclerView)
			{
				this.tilesAdapter.notifyDataSetChanged();
			}
		}
		else
		{
			synchronized (this.recyclerView)
			{
				this.tilesAdapter.notifyDataSetChanged();
			}
		}
	}
}
