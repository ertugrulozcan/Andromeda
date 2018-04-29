package com.ertis.andromeda;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ertis.andromeda.adapters.TilesAdapter;
import com.ertis.andromeda.managers.SpannedGridLayoutManager;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AppDrawerFragment extends Fragment
{
	private RecyclerView recyclerView;
	private TilesAdapter tilesAdapter;
	
	Timer timer = new Timer();
	private AnimatorSet tileFlipAnimation = null;
	private AnimatorSet tilePreFlipAnimation = null;
	private AnimatorSet tilePreFlipAnimation1, tilePreFlipAnimation2, tilePreFlipAnimation3;
	private AnimatorSet tileFlipAnimation1, tileFlipAnimation2, tileFlipAnimation3;
	
	public AppDrawerFragment()
	{
		// Required empty public constructor
	}
	
	public static AppDrawerFragment newInstance(TilesAdapter tilesAdapter)
	{
		AppDrawerFragment fragment = new AppDrawerFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		fragment.tilesAdapter = tilesAdapter;
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_app_drawer, container, false);
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		
		SpannedGridLayoutManager spannedGridLayoutManager = new SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL, 6);
		spannedGridLayoutManager.setItemOrderIsStable(true);
		recyclerView.setLayoutManager(spannedGridLayoutManager);
		
		tilesAdapter.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				AnimateTileFlip(view);
			}
		});
		
		recyclerView.setAdapter(tilesAdapter);
		
		loadAnimations();
		
		return view;
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
	
	@SuppressLint("ResourceType")
	private void loadAnimations()
	{
		tilePreFlipAnimation1 = (AnimatorSet) AnimatorInflater.loadAnimator(this.getActivity(), R.anim.tile_flip_pre);
		tilePreFlipAnimation2 = (AnimatorSet) AnimatorInflater.loadAnimator(this.getActivity(), R.anim.tile_flip_pre);
		tilePreFlipAnimation3 = (AnimatorSet) AnimatorInflater.loadAnimator(this.getActivity(), R.anim.tile_flip_pre);
		
		tileFlipAnimation1 = (AnimatorSet) AnimatorInflater.loadAnimator(this.getActivity(), R.anim.tile_flip);
		tileFlipAnimation2 = (AnimatorSet) AnimatorInflater.loadAnimator(this.getActivity(), R.anim.tile_flip);
		tileFlipAnimation3 = (AnimatorSet) AnimatorInflater.loadAnimator(this.getActivity(), R.anim.tile_flip);
		
		tilePreFlipAnimation2.setStartDelay(tilePreFlipAnimation2.getStartDelay() + 1000);
		tileFlipAnimation2.setStartDelay(tileFlipAnimation2.getStartDelay() + 1000);
		
		tilePreFlipAnimation3.setStartDelay(tilePreFlipAnimation3.getStartDelay() + 2000);
		tileFlipAnimation3.setStartDelay(tileFlipAnimation3.getStartDelay() + 2000);
		
		timer = new Timer();
		timer.schedule(new AnimationTask(), 0,3000);
	}
	
	private void AnimateTileFlip(final View tileView)
	{
		if (tileView == null)
			return;
		
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (!tileFlipAnimation1.isRunning() && !tilePreFlipAnimation1.isRunning())
				{
					tileFlipAnimation = tileFlipAnimation1;
					tilePreFlipAnimation = tilePreFlipAnimation1;
				}
				else if (!tileFlipAnimation2.isRunning() && !tilePreFlipAnimation2.isRunning())
				{
					tileFlipAnimation = tileFlipAnimation2;
					tilePreFlipAnimation = tilePreFlipAnimation2;
				}
				else if (!tileFlipAnimation3.isRunning() && !tilePreFlipAnimation3.isRunning())
				{
					tileFlipAnimation = tileFlipAnimation3;
					tilePreFlipAnimation = tilePreFlipAnimation3;
				}
				else
				{
					return;
				}
				
				if (tileFlipAnimation != null && tilePreFlipAnimation != null)
				{
					tileFlipAnimation.setTarget(tileView);
					tilePreFlipAnimation.setTarget(tileView);
					
					/*
					tilePreFlipAnimation.addListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							super.onAnimationEnd(animation);
							tileFlipAnimation.start();
						}
					});
					
					tilePreFlipAnimation.start();
					*/
					
					tilePreFlipAnimation.start();
					tileFlipAnimation.start();
				}
			}
		});
	}
	
	class AnimationTask extends TimerTask
	{
		@Override
		public void run()
		{
			List<View> tileViews = tilesAdapter.getTileViewList();
			if (tileViews != null && tileViews.size() > 0)
			{
				Random random = new Random();
				int rand1 = random.nextInt(tileViews.size() - 1);
				final View view1 = tileViews.get(rand1);
				
				int rand2 = random.nextInt(tileViews.size() - 1);
				final View view2 = tileViews.get(rand2);
				
				int rand3 = random.nextInt(tileViews.size() - 1);
				final View view3 = tileViews.get(rand3);
				
				AnimateTileFlip(view1);
				AnimateTileFlip(view2);
				AnimateTileFlip(view3);
			}
		}
	};
}
