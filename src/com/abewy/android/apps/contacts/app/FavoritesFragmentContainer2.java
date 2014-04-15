/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.app.ActionBar.OnNavigationListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Data;
import android.view.View;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.core.CorePrefs;
import com.abewy.android.apps.contacts.model.Group;
import com.abewy.android.extended.items.BaseType;

public class FavoritesFragmentContainer2 extends FragmentContainer
{
	private ArrayList<BaseType>		mGroups;
	private static int				mPosition	= 0;
	private boolean					mFirstLoad	= true;
	private Group					mFavGroup;
	private int						mFavCount;

	private OnNavigationListener	listener	= new OnNavigationListener() {

													@Override
													public boolean onNavigationItemSelected(int itemPosition, long itemId)
													{
														if (mPosition != itemPosition)
														{
															mPosition = itemPosition;
															restartLoader();
														}
														return true;
													}
												};

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		new LoadGroupTask().execute();
	}

	@Override
	protected int getSelectedView()
	{
		return CorePrefs.getFavoritesViewType();
	}

	@Override
	protected void setSelectedView(int viewType)
	{
		CorePrefs.setFavoritesViewType(viewType);
	}

	@Override
	protected Uri getUri()
	{
		Uri uri = Data.CONTENT_URI;

		if (isFilterQuery())
		{
			uri = Uri.withAppendedPath(Data.CONTENT_URI, mSearchQuery);
		}

		return uri;
	}

	@Override
	protected String getAdditionalFilters()
	{
		if (mPosition == 0)
		{
			return " AND " + Data.STARRED + " = " + 1;
		}
		else
		{
			Group group = (Group) mGroups.get(mPosition);
			return " AND " + CommonDataKinds.GroupMembership.GROUP_ROW_ID + " = " + group.getId();
		}
	}

	@Override
	protected boolean isFilterQuery()
	{
		return super.isFilterQuery() && mPosition > 0;
	}

	@Override
	protected void onLoadFinished(List<BaseType> contacts)
	{
		if (mFirstLoad)
		{
			mFavCount = contacts.size();
			
			if (mFavGroup != null)
			{
				mFavGroup.setSize(mFavCount);
			}
		}

		mFirstLoad = false;

		super.onLoadFinished(contacts);
	}

	private void onGroupsLoaded(ArrayList<BaseType> groups)
	{
		mFavGroup = new Group(-1, getString(R.string.title_favorites), mFavCount);

		Collections.sort(groups, new Comparator<BaseType>() {

			@Override
			public int compare(BaseType lhs, BaseType rhs)
			{
				return lhs.getItemPrimaryLabel().compareTo(rhs.getItemPrimaryLabel());
			}
		});

		mGroups = groups;
		mGroups.add(0, mFavGroup);
	}

	private class LoadGroupTask extends AsyncTask<Void, Void, ArrayList<BaseType>>
	{
		@Override
		protected ArrayList<BaseType> doInBackground(Void... arg0)
		{
			String selection = ContactsContract.Groups.DELETED + "=?";// and " + ContactsContract.Groups.GROUP_VISIBLE + "=?";
			String[] selectionArgs = { "0" };
			Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Groups.CONTENT_SUMMARY_URI,
					new String[] { ContactsContract.Groups._ID, ContactsContract.Groups.TITLE, ContactsContract.Groups.SUMMARY_COUNT }, selection,
					selectionArgs, null);

			ArrayList<BaseType> groups = new ArrayList<BaseType>();

			while (cursor.moveToNext())
			{
				Group group = new Group(cursor.getLong(0), cursor.getString(1), cursor.getInt(2));
				groups.add(group);
			}

			cursor.close();

			return groups;
		}

		@Override
		protected void onPostExecute(ArrayList<BaseType> list)
		{
			super.onPostExecute(list);

			onGroupsLoaded(list);
		}
	}

	@Override
	public void onSetToFront()
	{
		getActivity().setTitle(R.string.title_favorites);

		if (mGroups != null)
		{
			((IActionbarSpinner) getActivity()).displaySpinnerInActionBar(mGroups, mPosition, listener);
		}
	}

	@Override
	public void onSetToBack()
	{
		((IActionbarSpinner) getActivity()).removeSpinnerInActionBar();
	}
}