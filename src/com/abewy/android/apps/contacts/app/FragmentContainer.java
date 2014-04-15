/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Contactables;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.core.CoreIntentCodes;
import com.abewy.android.apps.contacts.core.CorePrefs;
import com.abewy.android.apps.contacts.model.Contact;
import com.abewy.android.extended.items.BaseType;

public class FragmentContainer extends Fragment implements LoaderCallbacks<Cursor>
{
	protected Fragment			mCurrent;
	protected List<BaseType>	mContacts;
	protected String			mSearchQuery;
	private Uri					uri;

	private Handler				handler		= new Handler();
	private ContentObserver		observer	= new ContentObserver(handler) {

												@Override
												public void onChange(boolean selfChange)
												{
													super.onChange(selfChange);

													onDataChange();
												}
											};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View fragView = inflater.inflate(R.layout.fragment_container, container, false);

		return fragView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		mCurrent = getFragmentForType(getSelectedView());

		if (mContacts != null)
			((IContactFragment) mCurrent).setContacts(mContacts);

		getChildFragmentManager().beginTransaction().add(R.id.fragment_container, mCurrent).commit();

		getLoaderManager().initLoader(0, null, this);
	}

	protected int getSelectedView()
	{
		return CorePrefs.getPeopleViewType();
	}

	protected void setSelectedView(int viewType)
	{
		CorePrefs.setPeopleViewType(viewType);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		if (!CorePrefs.isFirstLaunch())
		{
			inflater.inflate(R.menu.fragment_container, menu);

			MenuItem item = menu.findItem(R.id.action_view_as);
			switch (getSelectedView())
			{
				case CorePrefs.VIEW_TYPE_LIST:
				{
					item.setIcon(R.drawable.ic_ab_view_as_list);
					break;
				}
				case CorePrefs.VIEW_TYPE_BIG_GRID:
				{
					item.setIcon(R.drawable.ic_ab_view_as_big_grid);
					break;
				}
				case CorePrefs.VIEW_TYPE_MEDIUM_GRID:
				{
					item.setIcon(R.drawable.ic_ab_view_as_medium_grid);
					break;
				}
				case CorePrefs.VIEW_TYPE_SMALL_GRID:
				{
					item.setIcon(R.drawable.ic_ab_view_as_small_grid);
					break;
				}
			}
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.action_view_as_list)
		{
			if (getSelectedView() != CorePrefs.VIEW_TYPE_LIST)
			{
				setSelectedView(CorePrefs.VIEW_TYPE_LIST);

				switchFragment();
			}
		}
		else if (item.getItemId() == R.id.action_view_as_big_grid)
		{
			if (getSelectedView() != CorePrefs.VIEW_TYPE_BIG_GRID)
			{
				setSelectedView(CorePrefs.VIEW_TYPE_BIG_GRID);

				switchFragment();
			}
		}
		else if (item.getItemId() == R.id.action_view_as_medium_grid)
		{
			if (getSelectedView() != CorePrefs.VIEW_TYPE_MEDIUM_GRID)
			{
				setSelectedView(CorePrefs.VIEW_TYPE_MEDIUM_GRID);

				switchFragment();
			}
		}
		else if (item.getItemId() == R.id.action_view_as_small_grid)
		{
			if (getSelectedView() != CorePrefs.VIEW_TYPE_SMALL_GRID)
			{
				setSelectedView(CorePrefs.VIEW_TYPE_SMALL_GRID);

				switchFragment();
			}
		}

		return super.onOptionsItemSelected(item);
	}

	private void switchFragment()
	{
		Fragment fragment = getFragmentForType(getSelectedView());
		mCurrent = fragment;
		((IContactFragment) fragment).setContacts(mContacts);
		getChildFragmentManager().beginTransaction()
		/*.setCustomAnimations(R.animator.card_flip_right_in, R.animator.card_flip_right_out, R.animator.card_flip_left_in,
				R.animator.card_flip_left_out)*/.replace(R.id.fragment_container, fragment).commit();
		// .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).commit();
		// .setCustomAnimations(R.animator.vertical_flip_out, R.animator.vertical_flip_in).commit();
	}

	public void setContacts(List<BaseType> contacts)
	{
		this.mContacts = contacts;
		//mSearchQuery = null;

		if (mCurrent != null)
			((IContactFragment) mCurrent).setContacts(contacts);
	}

	protected Fragment getFragmentForType(int type)
	{
		switch (type)
		{
			case CorePrefs.VIEW_TYPE_LIST:
			{
				return new PeopleListFragment();
			}
			case CorePrefs.VIEW_TYPE_BIG_GRID:
			{
				return new PeopleBigGridFragment();
			}
			case CorePrefs.VIEW_TYPE_MEDIUM_GRID:
			{
				return new PeopleMediumGridFragment();
			}
			case CorePrefs.VIEW_TYPE_SMALL_GRID:
			{
				return new PeopleSmallGridFragment();
			}
			default:
			{
				return new PeopleListFragment();
			}
		}
	}

	// ___ Search
	public void searchQuery(String query)
	{
		mSearchQuery = query;

		filter();
	}

	protected void filter()
	{
		((IContactFragment) mCurrent).filter(mSearchQuery);
		// getLoaderManager().restartLoader(0, null, this);
	}

	public void openContactDetail(Contact contact)
	{
		Intent intent = new Intent(getActivity(), ContactActivity.class);

		List<Contact> list = ((IContactFragment) mCurrent).getDisplayedContacts();
		
		String[] lookupKeys = new String[list.size()];
		for (int i = 0, n = list.size(); i < n; i++)
		{
			lookupKeys[i] = list.get(i).getLookupKey();
		}

		intent.putExtra("lookupKey", contact.getLookupKey());
		intent.putExtra("name", contact.getName());
		intent.putExtra("lookupKeys", lookupKeys);

		startActivityForResult(intent, CoreIntentCodes.SHOW_CONTACT);
	}

	// ___ Cursor management

	private final String		SELECTION	= Contactables.HAS_PHONE_NUMBER + " = " + 1;

	// Sort results such that rows for the same contact stay together.
	private static final String	SORT_BY		= Contactables.LOOKUP_KEY;

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		// Easy way to limit the query to contacts with phone numbers.
		String selection = SELECTION + getAdditionalFilters();

		uri = getUri();

		getActivity().getContentResolver().registerContentObserver(uri, true, observer);

		return new CursorLoader(getActivity(),  // Context
				uri,       // URI representing the table/resource to be queried
				null,      // projection - the list of columns to return. Null means "all"
				selection, // selection - Which rows to return (condition rows must match)
				getSelectionArgs(),      // selection args - can be provided separately and subbed into selection.
				SORT_BY);   // string specifying sort order
	}

	protected Uri getUri()
	{
		return Contacts.CONTENT_URI;
	}

	protected String[] getProjection()
	{
		return null;
	}

	protected String getAdditionalFilters()
	{
		return "";
	}

	protected String[] getSelectionArgs()
	{
		return null;
	}

	protected boolean isFilterQuery()
	{
		return mSearchQuery != null && mSearchQuery.length() > 0;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
	{
		new LoaderTask().execute(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0)
	{
		Log.d("FragmentContainer", "onLoaderReset: ");
	}

	protected void onLoadFinished(List<BaseType> contacts)
	{
		setContacts(contacts);
	}

	protected void restartLoader()
	{
		getLoaderManager().restartLoader(0, null, this);
	}

	public void onSetToFront()
	{
		getActivity().setTitle(R.string.title_all_contacts);
	}

	public void onSetToBack()
	{

	}

	@Override
	public void onPause()
	{
		super.onPause();
		//getActivity().getContentResolver().unregisterContentObserver(observer);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		//getActivity().getContentResolver().registerContentObserver(uri, true, observer);
	}
	
	protected void onDataChange()
	{
		getLoaderManager().restartLoader(0, null, FragmentContainer.this);
	}

	private class LoaderTask extends AsyncTask<Cursor, Void, List<BaseType>>
	{
		private final String[]			PROJECTION	= new String[] {
																	StructuredName.CONTACT_ID,
																	StructuredName.LOOKUP_KEY,
																	StructuredName.GIVEN_NAME,
																	StructuredName.FAMILY_NAME };

		private final String			SELECTION	= ContactsContract.Data.MIMETYPE + " = ?";
		private final String[]			PARAMETERS	= new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
		private Map<String, Contact>	map;

		// private LongSparseArray<Contact> map;

		@Override
		protected List<BaseType> doInBackground(Cursor... params)
		{
			map = new HashMap<String, Contact>();
			// map = new LongSparseArray<Contact>();

			ArrayList<BaseType> list = new ArrayList<BaseType>();

			Cursor cursor = params[0];

			if (cursor.getCount() != 0)
			{
				int idColumnIndex, lookupColumnIndex, nameColumnIndex, photoUriColumnIndex, photoThumbUriColumnIndex, starredColumnIndex;

				idColumnIndex = cursor.getColumnIndex(Contacts._ID);

				if (idColumnIndex != -1)
				{
					lookupColumnIndex = cursor.getColumnIndex(Contacts.LOOKUP_KEY);

					nameColumnIndex = cursor.getColumnIndex(Contacts.DISPLAY_NAME);

					photoUriColumnIndex = cursor.getColumnIndex(Contacts.PHOTO_URI);
					photoThumbUriColumnIndex = cursor.getColumnIndex(Contacts.PHOTO_THUMBNAIL_URI);

					starredColumnIndex = cursor.getColumnIndex(Contacts.STARRED);
				}
				else
				{
					idColumnIndex = cursor.getColumnIndex(Data.CONTACT_ID);
					lookupColumnIndex = cursor.getColumnIndex(Data.LOOKUP_KEY);

					nameColumnIndex = cursor.getColumnIndex(Data.DISPLAY_NAME);

					photoUriColumnIndex = cursor.getColumnIndex(Data.PHOTO_URI);
					photoThumbUriColumnIndex = cursor.getColumnIndex(Data.PHOTO_THUMBNAIL_URI);

					starredColumnIndex = cursor.getColumnIndex(Data.STARRED);
				}

				String lookupKey = "";
				Contact contact = null;

				cursor.moveToFirst();

				do
				{
					String currentLookupKey = cursor.getString(lookupColumnIndex);

					if (!lookupKey.equals(currentLookupKey))
					{
						contact = new Contact();

						contact.setId(cursor.getLong(idColumnIndex));
						contact.setLookupKey(currentLookupKey);

						contact.setStarred(cursor.getInt(starredColumnIndex) == 1);

						String name = cursor.getString(nameColumnIndex);
						contact.setName(StringUtils.capitalize(name));

						contact.setPhoto(cursor.getString(photoUriColumnIndex));
						contact.setPhotoThumb(cursor.getString(photoThumbUriColumnIndex));

						map.put(contact.getLookupKey(), contact);
						list.add(contact);

						lookupKey = currentLookupKey;
					}
				}
				while (cursor.moveToNext());
			}

			cursor.close();

			// Getting first and last names
			Cursor names = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI, PROJECTION, SELECTION, PARAMETERS,
					StructuredName.CONTACT_ID + " ASC");

			Contact contact = null;

			while (names.moveToNext())
			{
				contact = (Contact) map.get(names.getString(1));

				if (contact != null)
				{
					String firstName = names.getString(2);
					String lastName = names.getString(3);

					if (CorePrefs.isSortingByLastName())
					{
						String name = "";

						if (lastName != null && lastName.length() > 0)
							name += StringUtils.capitalize(lastName) + " ";

						name += StringUtils.capitalize(firstName);

						contact.setName(name);
					}

					contact.setFirstName(firstName);
					contact.setLastName(lastName);
				}
			}

			names.close();

			String[] projection = { Phone.LOOKUP_KEY, Phone.TYPE, Phone.NORMALIZED_NUMBER, Phone.NUMBER };

			// Getting phones
			Cursor phones = getActivity().getContentResolver().query(Phone.CONTENT_URI, projection, null, null, Phone.CONTACT_ID + " ASC");

			while (phones.moveToNext())
			{
				contact = (Contact) map.get(phones.getString(0));

				if (contact != null)
				{
					if (phones.getString(2) != null)
						contact.addPhoneNumber(phones.getInt(1), phones.getString(2));
					else
						contact.addPhoneNumber(phones.getInt(1), phones.getString(3));
				}
			}

			phones.close();

			// Getting emails
			projection = new String[] { Email.LOOKUP_KEY, Email.TYPE, Email.ADDRESS };

			Cursor email = getActivity().getContentResolver().query(Email.CONTENT_URI, projection, null, null, Email.CONTACT_ID + " ASC");

			contact = null;

			while (email.moveToNext())
			{
				contact = (Contact) map.get(email.getString(0));

				if (contact != null)
				{
					contact.addEmailAddress(email.getInt(1), email.getString(2));
				}
			}

			email.close();

			Comparator<BaseType> comp = new Comparator<BaseType>() {

				@Override
				public int compare(BaseType lhs, BaseType rhs)
				{
					return lhs.getItemPrimaryLabel().compareTo(rhs.getItemPrimaryLabel());
				}
			};

			Collections.sort(list, comp);

			return list;
		}

		@Override
		protected void onPostExecute(List<BaseType> result)
		{
			super.onPostExecute(result);

			onLoadFinished(result);
		}
	}
}