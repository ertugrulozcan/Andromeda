package com.aero.andromeda.adapters;

import android.view.View;

public interface StickyHeaders
{
	boolean isStickyHeader(int position);
	
	interface ViewSetup
	{
		void setupStickyHeaderView(View stickyHeader);
		void teardownStickyHeaderView(View stickyHeader);
	}
}
