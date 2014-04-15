/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.LongSparseArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.core.CorePrefs;
import com.abewy.android.apps.contacts.model.Contact;
import com.abewy.android.extended.items.BaseType;
import com.abewy.android.extended.util.Android;

public class FragmentContainer2 extends Fragment implements LoaderCallbacks<Cursor>
{
	private Fragment		mCurrent;
	private List<BaseType>	mContacts;
	protected String		mSearchQuery;

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

		// getActivity().invalidateOptionsMenu();
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
		mSearchQuery = null;

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

		getLoaderManager().restartLoader(0, null, this);
	}

	public void openContactDetail(Contact contact)
	{
		Intent intent = new Intent(getActivity(), ContactActivity.class);

		long[] idList = new long[mContacts.size()];
		for (int i = 0, n = mContacts.size(); i < n; i++)
		{
			idList[i] = ((Contact) mContacts.get(i)).getId();
		}

		intent.putExtra("id", contact.getId());
		intent.putExtra("name", contact.getName());
		intent.putExtra("idList", idList);

		startActivity(intent);
	}

	// ___ Cursor management

	private final String		SELECTION	= Data.HAS_PHONE_NUMBER + " = " + 1;
	// private final String SELECTION = "" + Contacts.DISPLAY_NAME_PRIMARY + " NOTNULL AND " + Contacts.HAS_PHONE_NUMBER
	// + "=1 AND " + Contacts.DISPLAY_NAME_PRIMARY + " <> '' " + getAdditionalFilters();

	// Sort results such that rows for the same contact stay together.
	private static final String	SORT_BY		= Data.LOOKUP_KEY;

	/*@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		//Uri uri = Contacts.CONTENT_URI;
		/*if (mSearchQuery != null && mSearchQuery.length() > 0)
		{
			uri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI, Uri.encode(mSearchQuery));
			Log.d("FragmentContainer", "onCreateLoader: " + uri);
		}*

		String selection = SELECTION;
		String[] selectionArgs = null;

		if (mSearchQuery != null && mSearchQuery.length() > 0)
		{
			selection += " AND " + Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? ";
			selectionArgs = new String[] { "%" + mSearchQuery + "%" };
		}

		// Starts the query
		return new CursorLoader(getActivity(), Contacts.CONTENT_URI, PROJECTION, selection, selectionArgs, SORT_BY);
	}*/

	//////////////////////////////////////////////////////
	//
	// Séparer implémentations API 14-17 et API 18+
	//
	//////////////////////////////////////////////////////
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		// Easy way to limit the query to contacts with phone numbers.
		String selection = SELECTION + getAdditionalFilters();
		
		return new CursorLoader(getActivity(),  // Context
				getUri(),       // URI representing the table/resource to be queried
				null,      // projection - the list of columns to return. Null means "all"
				selection, // selection - Which rows to return (condition rows must match)
				getSelectionArgs(),      // selection args - can be provided separately and subbed into selection.
				SORT_BY);   // string specifying sort order
	}

	protected Uri getUri()
	{
		if (Android.isMinAPI(18))
		{
			// >= Jelly Bean 4.3
			Uri uri = CommonDataKinds.Contactables.CONTENT_URI;

			if (mSearchQuery != null && mSearchQuery.length() > 0)
			{
				uri = Uri.withAppendedPath(CommonDataKinds.Contactables.CONTENT_FILTER_URI, mSearchQuery);
			}

			return uri;
		}

		Uri uri = Contacts.CONTENT_URI;

		if (mSearchQuery != null && mSearchQuery.length() > 0)
		{
			uri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI, mSearchQuery);
		}

		return uri;
	}

	protected String[] getProjection()
	{
		if (Android.isMinAPI(18))
			return null;

		if (mSearchQuery != null && mSearchQuery.length() > 0)
		{
			return new String[] { Contacts.LOOKUP_KEY };
		}

		return null;
	}

	protected String getAdditionalFilters()
	{
		if (Android.isMinAPI(18))
		{
			// >= Jelly Bean 4.3, filter is on the Uri
			return "";
		}

		if (mSearchQuery != null && mSearchQuery.length() > 0)
		{
			return "";// " AND (" + CommonDataKinds.StructuredName.DISPLAY_NAME + " LIKE ? OR " + CommonDataKinds.Phone.NUMBER + " LIKE ? OR "
						// + CommonDataKinds.Email.ADDRESS + " LIKE ?)";
		}

		return "";
	}

	protected String[] getSelectionArgs()
	{
		if (Android.isMinAPI(18))
		{
			// >= Jelly Bean 4.3, filter is on the Uri
			return null;
		}

		/*if (mSearchQuery != null && mSearchQuery.length() > 0)
		{
			String query = "%" + mSearchQuery + "%";
			return new String[] { query, query, query };
		}*/

		return null;
	}
	
	protected boolean isFilterQuery()
	{
		return mSearchQuery != null && mSearchQuery.length() > 0;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
	{
		if (Android.isMinAPI(18) || isFilterQuery())
			new LoaderTask().execute(cursor);
		else
			new LoaderTask2().execute(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0)
	{

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

	private class LoaderTask extends AsyncTask<Cursor, Void, List<BaseType>>
	{
		@Override
		protected List<BaseType> doInBackground(Cursor... params)
		{
			ArrayList<BaseType> list = new ArrayList<BaseType>();

			Cursor cursor = params[0];

			if (cursor.getCount() != 0)
			{
				int idColumnIndex = cursor.getColumnIndex(CommonDataKinds.StructuredName.CONTACT_ID);
				int lookupColumnIndex = cursor.getColumnIndex(Data.LOOKUP_KEY);

				int nameColumnIndex = cursor.getColumnIndex(Data.DISPLAY_NAME);
				int firstNameColumnIndex = cursor.getColumnIndex(CommonDataKinds.StructuredName.GIVEN_NAME);
				int lastNameColumnIndex = cursor.getColumnIndex(CommonDataKinds.StructuredName.FAMILY_NAME);

				int phoneColumnIndex = cursor.getColumnIndex(CommonDataKinds.Phone.NORMALIZED_NUMBER);
				int phoneTypeColumnIndex = cursor.getColumnIndex(CommonDataKinds.Phone.TYPE);

				int emailColumnIndex = cursor.getColumnIndex(CommonDataKinds.Email.ADDRESS);
				int emailTypeColumnIndex = cursor.getColumnIndex(CommonDataKinds.Email.TYPE);

				int photoUriColumnIndex = cursor.getColumnIndex(CommonDataKinds.Photo.PHOTO_URI);
				int photoThumbUriColumnIndex = cursor.getColumnIndex(CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI);

				int starredColumnIndex = cursor.getColumnIndex(Data.STARRED);

				int typeColumnIndex = cursor.getColumnIndex(Data.MIMETYPE);

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

						list.add(contact);

						lookupKey = currentLookupKey;
					}

					String mimeType = cursor.getString(typeColumnIndex);

					if (mimeType.equals(CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE))
					{
						String name = cursor.getString(nameColumnIndex);
						String firstName = cursor.getString(firstNameColumnIndex);
						String lastName = cursor.getString(lastNameColumnIndex);
						if (CorePrefs.isSortingByLastName())
						{
							String n = "";

							if (lastName != null && lastName.length() > 0)
								n += StringUtils.capitalize(lastName) + " ";

							n += StringUtils.capitalize(firstName);

							contact.setName(n);
						}
						else if (name != null)
						{
							contact.setName(StringUtils.capitalize(name));
						}

						contact.setFirstName(firstName);
						contact.setLastName(lastName);

					}
					else if (mimeType.equals(CommonDataKinds.Photo.CONTENT_ITEM_TYPE))
					{
						contact.setPhoto(cursor.getString(photoUriColumnIndex));
						contact.setPhotoThumb(cursor.getString(photoThumbUriColumnIndex));
					}
					else if (mimeType.equals(CommonDataKinds.Phone.CONTENT_ITEM_TYPE))
					{
						contact.addPhoneNumber(cursor.getInt(phoneTypeColumnIndex), cursor.getString(phoneColumnIndex));
					}
					else if (mimeType.equals(CommonDataKinds.Email.CONTENT_ITEM_TYPE))
					{
						contact.addEmailAddress(cursor.getInt(emailTypeColumnIndex), cursor.getString(emailColumnIndex));
					}
				}
				while (cursor.moveToNext());
			}

			cursor.close();

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

		/*@Override
		protected List<BaseType> doInBackground(Cursor... params)
		{
			LongSparseArray<BaseType> map = new LongSparseArray<BaseType>();

			Cursor cursor = params[0];

			if (cursor != null && !cursor.isClosed())
			{
				cursor.moveToFirst();
				while (!cursor.isAfterLast())
				{
					Contact contact = new Contact();

					long id = cursor.getLong(CONTACT_ID_INDEX);

					contact.setId(id);
					contact.setLookupKey(cursor.getString(LOOKUP_KEY_INDEX));
					contact.setName(cursor.getString(CONTACT_NAME_INDEX));
					contact.setPhotoThumb(cursor.getString(CONTACT_PHOTO_THUMB_INDEX));
					contact.setPhoto(cursor.getString(CONTACT_PHOTO_INDEX));
					contact.setStarred(cursor.getInt(CONTACT_STARRED_INDEX) == 1);
					map.append(contact.getId(), contact);

					cursor.moveToNext();
				}

				// Method 2 : < 100 ms
				String[] projection = { Phone.CONTACT_ID, Phone.TYPE, Phone.NORMALIZED_NUMBER, Phone.NUMBER };

				Cursor phones = getActivity().getContentResolver().query(Phone.CONTENT_URI, projection, null, null, Phone.CONTACT_ID + " ASC");

				Contact contact = null;

				while (phones.moveToNext())
				{
					long id = phones.getLong(0);

					contact = (Contact) map.get(id);

					if (contact != null)
					{
						if (phones.getString(2) != null)
							contact.addPhoneNumber(phones.getInt(1), phones.getString(2));
						else
							contact.addPhoneNumber(phones.getInt(1), phones.getString(3));
					}
				}

				phones.close();

				// Same process for the names
				projection = new String[] { StructuredName.CONTACT_ID, StructuredName.GIVEN_NAME, StructuredName.FAMILY_NAME };
				String selection = ContactsContract.Data.MIMETYPE + " = ?";
				String[] parameters = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };

				Cursor names = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, parameters,
						StructuredName.CONTACT_ID + " ASC");

				contact = null;

				while (names.moveToNext())
				{
					long id = names.getLong(0);

					contact = (Contact) map.get(id);

					if (contact != null)
					{
						String firstName = names.getString(1);
						String lastName = names.getString(2);

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

				projection = new String[] { Email.CONTACT_ID, Email.TYPE, Email.ADDRESS };

				Cursor email = getActivity().getContentResolver().query(Email.CONTENT_URI, projection, null, null, Email.CONTACT_ID + " ASC");

				contact = null;

				while (email.moveToNext())
				{
					long id = email.getLong(0);

					contact = (Contact) map.get(id);

					if (contact != null)
					{
						contact.addEmailAddress(email.getInt(1), email.getString(2));
					}
				}

				email.close();
			}

			ArrayList<BaseType> list = new ArrayList<BaseType>();
			for (int i = 0, n = map.size(); i < n; i++)
			{
				Contact contact = (Contact) map.valueAt(i);
				list.add(contact);
			}

			Comparator<BaseType> comp = new Comparator<BaseType>() {

				@Override
				public int compare(BaseType lhs, BaseType rhs)
				{
					return lhs.getItemPrimaryLabel().compareTo(rhs.getItemPrimaryLabel());
				}
			};

			Collections.sort(list, comp);

			return list;
		}*/

		@Override
		protected void onPostExecute(List<BaseType> result)
		{
			super.onPostExecute(result);

			onLoadFinished(result);
		}
	}

	private class LoaderTask2 extends LoaderTask
	{
		@Override
		protected List<BaseType> doInBackground(Cursor... params)
		{
			Set<String> set = new HashSet<String>();

			Cursor cursor = params[0];

			int idColumnIndex = cursor.getColumnIndex(Contacts.LOOKUP_KEY);

			if (cursor != null && !cursor.isClosed())
			{
				cursor.moveToFirst();
				while (!cursor.isAfterLast())
				{
					set.add("'" + cursor.getString(idColumnIndex) + "'");

					cursor.moveToNext();
				}
			}

			cursor.close();

			String selection = Data.LOOKUP_KEY + " IN (" + StringUtils.join(set, ",") + ")";
			long start = new Date().getTime();
			cursor = getActivity().getContentResolver().query(Data.CONTENT_URI, null, selection, null, SORT_BY);
			Log.d("FragmentContainer.LoaderTask2", "doInBackground: " + (new Date().getTime() - start));
			return super.doInBackground(cursor);
		}
	}
}