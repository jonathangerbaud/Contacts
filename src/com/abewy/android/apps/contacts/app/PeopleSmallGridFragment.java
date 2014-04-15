/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.app;

import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.adapter.LayoutType;


public class PeopleSmallGridFragment extends PeopleBigGridFragment
{
	private static int		mLastPosition		= 0;
	private static int		mLastFavPosition	= 0;
	
	public PeopleSmallGridFragment()
	{

	}
	
	@Override
	protected int getNumColumn()
	{
		return getResources().getInteger(R.integer.small_grid_columns);
	}

	@Override
	protected int getCustomLayout()
	{
		return R.layout.grid_big;
	}
	
	protected int getAdapterLayoutType()
	{
		return LayoutType.GRID_SMALL;
	}
	
	@Override
	protected int getLastPosition(boolean favorite)
	{
		return favorite ? mLastFavPosition : mLastPosition;
	}

	@Override
	protected void setLastPosition(boolean favorite, int position)
	{
		if (favorite)
			mLastFavPosition = position;
		else
			mLastPosition = position;
	}
}
