package com.aero.andromeda.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		
		List<Class<? extends Object>> registeredTypes = new ArrayList<>(this.serviceDictionary.keySet());
		for (int i = 0; i < registeredTypes.size(); i++)
		{
			T service = (T) this.serviceDictionary.get(registeredTypes.get(i));
			Class<?>[] interfaces = service.getClass().getInterfaces();
			for (Class<?> base : interfaces)
			{
				if (base.equals(t))
					return service;
			}
		}
		
		return null;
	}
}
