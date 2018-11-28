package com.aero.andromeda.models;

public class Result<T>
{
	private boolean _isSuccess;
	private T data;
	private String message;
	
	public boolean isSuccess()
	{
		return _isSuccess;
	}
	
	public T getData()
	{
		return data;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public Result(boolean isSuccess)
	{
		this._isSuccess = isSuccess;
	}
	
	public Result(boolean isSuccess, String message)
	{
		this._isSuccess = isSuccess;
		this.message = message;
	}
	
	public Result(boolean isSuccess, T data)
	{
		this._isSuccess = isSuccess;
		this.data = data;
	}
	
	public Result(boolean isSuccess, String message, T data)
	{
		this._isSuccess = isSuccess;
		this.message = message;
		this.data = data;
	}
}
