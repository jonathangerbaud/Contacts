package com.abewy.android.apps.contacts.app;

import android.app.ListFragment;

public class BaseListFragment extends android.support.v4.app.ListFragment
{
	protected void setListVisible(boolean visible)
	{
		setListShown(visible);
		
	}

	protected void setEmptyText(int resId)
	{
		if (getActivity() != null)
			setEmptyText(getResources().getString(resId));
	}
}
