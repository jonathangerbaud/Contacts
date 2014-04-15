package com.abewy.android.apps.contacts.adapter;

import android.util.Log;
import com.abewy.android.apps.contacts.model.ObjectType;
import com.abewy.android.extended.adapter.TypeAdapter;
import com.abewy.android.extended.items.BaseType;

public class AdapterSelector
{
	public AdapterSelector()
	{

	}

	static TypeAdapter<BaseType> getAdapter(BaseType object, int layoutType, MultiObjectAdapter parentAdapter)
	{
		switch (object.getItemViewType())
		{
			case ObjectType.CONTACT:
			{
				if (layoutType == LayoutType.GRID_BIG)
					return new ContactBigGridAdapter();
				else if (layoutType == LayoutType.GRID_MEDIUM)
					return new ContactMediumGridAdapter();
				else if (layoutType == LayoutType.GRID_SMALL)
					return new ContactSmallGridAdapter();
				
				return new ContactAdapter();
			}
			case BaseType.HEADER:
			{
				return new HeaderAdapter();
			}
			case ObjectType.CONTACT_PHONE:
			{
				return new ContactPhoneAdapter();
			}
			case ObjectType.CONTACT_EMAIL:
			{
				return new ContactEmailAdapter();
			}
			case ObjectType.GROUP:
			{
				return new GroupAdapter();
			}
		}

		Log.e("AdapterSelector", "No adapter defined for type " + object);
		return null;
	}
}
