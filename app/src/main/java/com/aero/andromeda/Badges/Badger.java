package com.aero.andromeda.Badges;

import android.content.ComponentName;
import android.content.Context;

import java.util.List;

public interface Badger
{
	void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException;
	
	List<String> getSupportLaunchers();
}
