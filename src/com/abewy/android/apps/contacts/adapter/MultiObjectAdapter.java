/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.adapter;

import java.util.ArrayList;
import java.util.List;
import android.widget.AbsListView;
import com.abewy.android.extended.adapter.MultiTypeAdapter;
import com.abewy.android.extended.adapter.TypeAdapter;
import com.abewy.android.extended.items.BaseType;
import com.crashlytics.android.Crashlytics;
import com.haarman.listviewanimations.itemmanipulation.AnimateDismissAdapter;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;

public class MultiObjectAdapter extends MultiTypeAdapter<BaseType>
{
	private AnimateDismissAdapter<BaseType>	deleteAdapter;

	public MultiObjectAdapter(AbsListView listView)
	{
		this(listView, 0);
	}

	public MultiObjectAdapter(AbsListView listView, int layoutType)
	{
		super(layoutType);

		if (listView != null)
		{
			deleteAdapter = new AnimateDismissAdapter<BaseType>(this, new OnDismissCallback() {

				@Override
				public void onDismiss(AbsListView listView, int[] reverseSortedPositions)
				{
					for (int position : reverseSortedPositions)
					{
						removeAt(position);
					}
				}
			});
			deleteAdapter.setAbsListView(listView);
		}
	}

	@Override
	public void remove(BaseType object)
	{
		remove(object, false);
	}

	public void remove(BaseType object, boolean animated)
	{
		if (animated == false || deleteAdapter == null)
		{
			super.remove(object);
			notifyDataSetChanged();
		}
		else
		{
			List<Integer> list = new ArrayList<Integer>();
			list.add(getItemPosition(object));
			deleteAdapter.animateDismiss(list);
		}
	}

	@Override
	public void removeAt(int index)
	{
		removeAt(index, false);
	}

	public void removeAt(int index, boolean animated)
	{
		if (index >= 0 && index < getCount())
		{
			if (animated == false || deleteAdapter == null)
			{
				super.removeAt(index);
				notifyDataSetChanged();
			}
			else
			{
				List<Integer> list = new ArrayList<Integer>();
				list.add(index);
				deleteAdapter.animateDismiss(list);
			}
		}
	}
	
	@Override
	public void removeFirst()
	{
		removeFirst(false);
	}
	
	public void removeFirst(boolean animated)
	{
		if (animated == false || deleteAdapter == null)
		{
			super.removeFirst();
		}
		else
		{
			List<Integer> list = new ArrayList<Integer>();
			list.add(0);
			deleteAdapter.animateDismiss(list);
		}
	}
	
	@Override
	public void removeLast()
	{
		removeLast(false);
	}
	
	public void removeLast(boolean animated)
	{
		if (animated == false || deleteAdapter == null)
		{
			super.removeLast();
		}
		else
		{
			List<Integer> list = new ArrayList<Integer>();
			list.add(getCount() - 1);
			deleteAdapter.animateDismiss(list);
		}
	}

	@Override
	protected TypeAdapter<BaseType> getAdapter(BaseType object, int layoutType)
	{
		TypeAdapter<BaseType> adapter = AdapterSelector.getAdapter(object, layoutType, this);

		if (adapter == null)
		{
			Crashlytics.setString("MultiObjectAdapter_object", object != null ? object.toString() : "object is null");
			Crashlytics.setString("MultiObjectAdapter_layout", String.valueOf(layoutType));
		}

		return adapter;
	}

	private List<Integer>	types	= new ArrayList<Integer>();

	@Override
	protected int getItemViewType(BaseType object)
	{
		int type = object.getItemViewType();
		int index = types.indexOf(type);

		if (index == -1)
		{
			index = types.size();
			types.add(type);
		}

		return index;
	}

}
