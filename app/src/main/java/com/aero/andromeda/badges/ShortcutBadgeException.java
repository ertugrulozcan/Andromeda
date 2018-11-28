package com.aero.andromeda.badges;

public class ShortcutBadgeException extends Exception
{
	public ShortcutBadgeException(String message)
	{
		super(message);
	}
	
	public ShortcutBadgeException(String message, Exception e)
	{
		super(message, e);
	}
}
