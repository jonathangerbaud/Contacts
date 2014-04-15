/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Filter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.abewy.android.apps.contacts.adapter.AnimatedContactAdapter;
import com.abewy.android.apps.contacts.adapter.ContactListAdapter;
import com.abewy.android.apps.contacts.core.CorePrefs;
import com.abewy.android.apps.contacts.model.Contact;
import com.abewy.android.extended.items.BaseType;
import com.abewy.android.extended.items.Header;
import com.hb.views.PinnedSectionListView;

public class PeopleListFragment extends ContactsListFragment implements IContactFragment
{
	private static int		mLastPosition		= 0;
	private static int		mLastPositionTop	= 0;
	private static int		mLastFavPosition	= 0;
	private static int		mLastFavPositionTop	= 0;

	private List<BaseType>	mContacts;
	private boolean			mIsCreated			= false;
	private boolean			mIsFavoriteFragment;
	private String			mLastFilter;

	public PeopleListFragment()
	{

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		((PinnedSectionListView) getListView()).setShadowVisible(false);

		ListAdapter adapter = null;
		if (CorePrefs.isAnimatingListGridItems())
		{
			adapter = new AnimatedContactAdapter(new ContactListAdapter(getListView()), 0.8f, 75L, 150L);
			((AnimatedContactAdapter) adapter).setAbsListView(getListView());
		}
		else
		{
			adapter = new ContactListAdapter(getListView());
		}

		setListAdapter(adapter);
		getAdapter().setFilter(new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				addHeadersAndDisplay(((List<BaseType>) results.values));
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
			addHeadersAndDisplay(mContacts);
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
			View v = getListView().getChildAt(index);
			int top = v == null ? 0 : v.getTop();

			if (mIsFavoriteFragment)
			{
				mLastFavPosition = index;
				mLastFavPositionTop = top;
			}
			else
			{
				mLastPosition = index;
				mLastPositionTop = top;
			}
		}
	}

	private void restorePosition()
	{
		if (getListView() != null)
		{
			getListView().setSelectionFromTop(mIsFavoriteFragment ? mLastFavPosition : mLastPosition,
					mIsFavoriteFragment ? mLastFavPositionTop : mLastPositionTop);
		}
	}

	private void resetPosition()
	{
		if (mIsFavoriteFragment)
		{
			mLastFavPosition = mLastFavPositionTop = 0;
		}
		else
		{
			mLastPosition = mLastPositionTop = 0;
		}
	}

	public void setContacts(List<BaseType> contacts)
	{
		savePosition();

		mContacts = contacts;

		addHeadersAndDisplay(mContacts);

		filter(mLastFilter, false);
	}

	private void addHeadersAndDisplay(List<BaseType> data)
	{
		ArrayList<BaseType> list = new ArrayList<BaseType>();

		if (data.size() > 0)
		{
			char previous = data.get(0).getItemPrimaryLabel().charAt(0);

			Header header = new Header(previous + "");

			list.add(header);
			list.add(data.get(0));

			if (data.size() > 1)
			{
				Iterator<BaseType> iterator = data.iterator();
				iterator.next();

				while (iterator.hasNext())
				{
					BaseType contact = iterator.next();

					char current = contact.getItemPrimaryLabel().charAt(0);

					if (previous != current)
					{
						header = new Header(current + "");
						list.add(header);

						previous = current;
					}

					list.add(contact);
				}
			}
		}

		if (mIsCreated == true && isAdded())
			load(list);
	}

	private void load(List<BaseType> data)
	{
		getAdapter().setData(data);

		restorePosition();

		setListVisible(true);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id)
	{
		super.onListItemClick(listView, view, position, id);

		Contact contact = (Contact) getAdapter().getItem(position);

		((FragmentContainer) getParentFragment()).openContactDetail(contact);
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
			addHeadersAndDisplay(mContacts);

		mLastFilter = filter;
	}

	@Override
	public List<Contact> getDisplayedContacts()
	{
		List<Contact> list = new ArrayList<Contact>();
		
		for (int i = 0, n = getAdapter().getCount(); i < n; i++)
		{
			BaseType baseType = getAdapter().getItem(i);
			
			if (baseType instanceof Contact)
			{
				list.add((Contact) baseType);
			}
		}
		
		return list;
	}
}
