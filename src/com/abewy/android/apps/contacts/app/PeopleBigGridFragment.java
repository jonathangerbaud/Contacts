/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.app;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Filter;
import android.widget.GridView;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.adapter.LayoutType;
import com.abewy.android.apps.contacts.adapter.MultiObjectAdapter;
import com.abewy.android.apps.contacts.core.CorePrefs;
import com.abewy.android.apps.contacts.model.Contact;
import com.abewy.android.extended.items.BaseType;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;

public class PeopleBigGridFragment extends ContactsGridFragment implements IContactFragment
{
	private static int		mLastPosition		= 0;
	private static int		mLastFavPosition	= 0;

	private List<BaseType>	mContacts;
	private boolean			mIsCreated			= false;
	private boolean			mIsFavoriteFragment;
	private String			mLastFilter;

	public PeopleBigGridFragment()
	{

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		if (CorePrefs.isAnimatingListGridItems())
		{
			ScaleInAnimationAdapter adapter = new ScaleInAnimationAdapter(new MultiObjectAdapter(getListView(), getAdapterLayoutType()), 0.7f, 75L,
					150L);
			adapter.setAbsListView(getListView());
			setListAdapter(adapter);
		}
		else
		{
			setListAdapter(new MultiObjectAdapter(getListView(), getAdapterLayoutType()));
		}

		getAdapter().setFilter(new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				load(((List<BaseType>) results.values));
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint)
			{
				FilterResults results = new FilterResults();
				List<BaseType> filteredContacts = new ArrayList<BaseType>();

				// perform your search here using the searchConstraint String.

				constraint = constraint.toString().toLowerCase();
				for (int i = 0, n = mContacts.size(); i < n; i++)
				{
					Contact contact = (Contact) mContacts.get(i);
					if (contact.filter(constraint.toString()))
					{
						filteredContacts.add(contact);
					}
				}

				results.count = filteredContacts.size();
				results.values = filteredContacts;

				return results;
			}
		});

		mIsCreated = true;

		if (mContacts != null)
			load();
	}

	@Override
	protected int getNumColumn()
	{
		return getResources().getInteger(R.integer.big_grid_columns);
	}

	@Override
	protected int getCustomLayout()
	{
		return R.layout.grid_big;
	}

	protected int getAdapterLayoutType()
	{
		return LayoutType.GRID_BIG;
	}

	public void setContacts(List<BaseType> contacts)
	{
		savePosition();

		this.mContacts = contacts;

		if (mIsCreated == true && isAdded())
		{
			load();
			filter(mLastFilter, false);
		}
	}

	private void load()
	{
		load(mContacts);
	}

	private void load(List<BaseType> data)
	{
		getAdapter().setData(data);
		setListVisible(true);

		restorePosition();

		getGridView().setSelection(getLastPosition(mIsFavoriteFragment));
	}

	@Override
	public void onGridItemClick(GridView gridView, View view, int position, long id)
	{
		super.onGridItemClick(gridView, view, position, id);

		Contact contact = (Contact) getAdapter().getItem(position);

		((FragmentContainer) getParentFragment()).openContactDetail(contact);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mIsFavoriteFragment = getParentFragment() instanceof FavoritesFragmentContainer;
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		savePosition();
	}

	private void savePosition()
	{
		if (getListView() != null)
		{
			int index = getListView().getFirstVisiblePosition();

			setLastPosition(mIsFavoriteFragment, index);
		}
	}

	private void restorePosition()
	{
		if (getListView() != null)
		{
			getListView().setSelection(getLastPosition(mIsFavoriteFragment));
		}
	}

	private void resetPosition()
	{
		setLastPosition(mIsFavoriteFragment, 0);
	}

	protected int getLastPosition(boolean favorite)
	{
		return favorite ? mLastFavPosition : mLastPosition;
	}

	protected void setLastPosition(boolean favorite, int position)
	{
		if (favorite)
			mLastFavPosition = position;
		else
			mLastPosition = position;
	}

	@Override
	public void filter(String filter)
	{
		filter(filter, true);
	}

	private void filter(String filter, boolean reset)
	{
		if (reset)
			resetPosition();

		if (filter != null && filter.length() > 0)
			getAdapter().getFilter().filter(filter);
		else
			load();

		mLastFilter = filter;
	}

	@Override
	public List<Contact> getDisplayedContacts()
	{
		List<Contact> list = new ArrayList<Contact>();

		for (int i = 0, n = getAdapter().getCount(); i < n; i++)
		{
			list.add((Contact) getAdapter().getItem(i));
		}

		return list;
	}
}
