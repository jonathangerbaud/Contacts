package com.abewy.android.apps.contacts.model;

import com.abewy.android.extended.items.BaseType;

public class Group extends BaseType
{
	private long id;
	private String title;
	private int size;
	
	public Group()
	{
		
	}
	
	public Group(long id, String title, int size)
	{
		this.id = id;
		this.title = title;
		this.size = size;
	}
	
	@Override
	public int getItemViewType()
	{
		return ObjectType.GROUP;
	}

	@Override
	public String getItemPrimaryLabel()
	{
		return title;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}
}
