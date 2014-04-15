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
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.view.View;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.core.CorePrefs;
import com.abewy.android.apps.contacts.model.Group;
import com.abewy.android.extended.items.BaseType;

public class FavoritesFragmentContainer extends FragmentContainer
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
		if (mPosition > 0)
			return Data.CONTENT_URI;
		
		return super.getUri();
	}

	@Override
	protected String getAdditionalFilters()
	{
		if (mPosition == 0)
		{
			return " AND " + Contacts.STARRED + " = " + 1;
		}
		else
		{
			return " AND " + Data.MIMETYPE + " = ? AND " + CommonDataKinds.GroupMembership.GROUP_ROW_ID + " = ?";
		}
	}
	
	@Override
	protected String[] getSelectionArgs()
	{
		if (mPosition > 0)
		{
			Group group = (Group) mGroups.get(mPosition);
			
			return new String[]{GroupMembership.CONTENT_ITEM_TYPE, String.valueOf(group.getId())};
		}
		
		return null;
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
	
	@Override
	protected void filter()
	{
		((IContactFragment) mCurrent).filter(mSearchQuery);
	}

	private void onGroupsLoaded(ArrayList<BaseType> groups)
	{
		mGroups = groups;
		
		if (mOnFront)
			onSetToFront();
	}
	
	@Override
	protected void onDataChange()
	{
		super.onDataChange();
		
		new LoadGroupTask().execute();
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
			
			cursor = getActivity().getContentResolver().query(FavoritesFragmentContainer.super.getUri(), null, Contacts.STARRED + " = " + 1, null, null);

			Group fGroup = new Group(-1, getString(R.string.title_favorites), cursor.getCount());
			
			Collections.sort(groups, new Comparator<BaseType>() {

				@Override
				public int compare(BaseType lhs, BaseType rhs)
				{
					return lhs.getItemPrimaryLabel().compareTo(rhs.getItemPrimaryLabel());
				}
			});
			
			groups.add(0, fGroup);
			
			return groups;
		}

		@Override
		protected void onPostExecute(ArrayList<BaseType> list)
		{
			super.onPostExecute(list);

			onGroupsLoaded(list);
		}
	}
	
	private boolean mOnFront = false;

	@Override
	public void onSetToFront()
	{
		mOnFront = true;
		
		getActivity().setTitle(R.string.title_favorites);

		if (mGroups != null)
		{
			((IActionbarSpinner) getActivity()).displaySpinnerInActionBar(mGroups, mPosition, listener);
		}
	}

	@Override
	public void onSetToBack()
	{
		mOnFront = false;
		
		((IActionbarSpinner) getActivity()).removeSpinnerInActionBar();
	}
}