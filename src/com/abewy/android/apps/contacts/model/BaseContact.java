package com.abewy.android.apps.contacts.model;

import com.abewy.android.extended.items.BaseType;

public class BaseContact extends BaseType
{
	@Override
	public int getItemViewType()
	{
		return 0;
	}

	@Override
	public String getItemPrimaryLabel()
	{
		return "";
	}
}
