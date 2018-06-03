package com.ertis.andromeda.services;

import java.util.HashMap;

public class ServiceLocator
{
	private static ServiceLocator self = null;
	
	private HashMap<Class<? extends Object>, Object> serviceDictionary;
	
	private ServiceLocator()
	{
		this.serviceDictionary = new HashMap<>();
	}
	
	public static ServiceLocator Current()
	{
		if (self == null)
			self = new ServiceLocator();
		
		return self;
	}
	
	public <T extends Object> void RegisterInstance(T service)
	{
		Class<T> t = (Class<T>) service.getClass();
		if (!this.serviceDictionary.containsKey(t))
			this.serviceDictionary.put(t, service);
	}
	
	public <T extends Object> T GetInstance(Class<T> t)
	{
		if (this.serviceDictionary.containsKey(t))
			return (T) this.serviceDictionary.get(t);
		
		return null;
	}
}
