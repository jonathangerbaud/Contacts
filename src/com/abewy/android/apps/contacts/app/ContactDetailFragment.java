/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.app;

import java.io.File;
import com.google.common.io.Closeables;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.adapter.MultiObjectAdapter;
import com.abewy.android.apps.contacts.imageloader.ImageLoader;
import com.abewy.android.apps.contacts.model.Contact;
import com.abewy.android.apps.contacts.model.ContactEmail;
import com.abewy.android.apps.contacts.model.ContactPhone;
import com.abewy.android.apps.contacts.util.ContactsUtil;
import com.abewy.android.extended.items.BaseType;
import com.abewy.android.extended.items.Header;
import com.abewy.android.extended.util.AlertUtil;
import com.abewy.android.extended.util.PhoneUtil;

public class ContactDetailFragment extends ContactsListFragment implements LoaderCallbacks<Cursor>
{
	private static final int	PICK_PHOTO_CODE	= 100;
	private static final int	CROP_PHOTO_CODE	= 200;

	public static interface OnContactDeletedListener
	{
		public void onContactDeleted(String lookupKey);
	}

	private String						mLookupKey;
	private Contact						mContact;
	private OnContactDeletedListener	listener;
	private Uri							tmpPhotoUri;
	private Uri							tmpCroppedPhotoUri;

	private static final String[]		PROJECTION	= {
					StructuredName.CONTACT_ID,
					StructuredName.DISPLAY_NAME,
					StructuredName.GIVEN_NAME,
					StructuredName.FAMILY_NAME,
					StructuredName.PHOTO_URI,
					StructuredName.PHOTO_THUMBNAIL_URI,
					StructuredName.STARRED			};

	private static final String			SELECTION	= StructuredName.LOOKUP_KEY + " = ? AND " + Data.MIMETYPE + " = ? AND (("
														+ StructuredName.DISPLAY_NAME + " NOTNULL) AND (" + StructuredName.DISPLAY_NAME + " != ''))";

	public ContactDetailFragment()
	{

	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		if (activity instanceof OnContactDeletedListener)
			listener = (OnContactDeletedListener) activity;
		else
			throw new ClassCastException("Activity must implement ContactDetailFragment.OnContactDeletedListener");
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		listener = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mLookupKey = getArguments() != null ? getArguments().getString("lookupKey") : null;

		setHasOptionsMenu(true);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		setListAdapter(new MultiObjectAdapter(getListView()));

		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		menu.add(Menu.NONE, R.id.menu_edit, Menu.NONE, R.string.menu_edit).setIcon(R.drawable.ic_action_edit)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		if (mContact != null)
		{
			menu.add(Menu.NONE, R.id.menu_starred, Menu.NONE, mContact.isStarred() ? R.string.menu_add_favorite : R.string.menu_remove_favorite)
					.setIcon(mContact.isStarred() ? R.drawable.ic_action_important : R.drawable.ic_action_not_important)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

			menu.add(Menu.NONE, R.id.menu_delete, Menu.NONE, R.string.menu_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.menu_starred)
		{
			ContentValues values = new ContentValues();
			values.put(Contacts.STARRED, mContact.isStarred() ? 0 : 1);
			mContact.setStarred(!mContact.isStarred());

			getActivity().getContentResolver().update(Contacts.CONTENT_URI, values, Contacts.LOOKUP_KEY + "= ?",
					new String[] { mContact.getLookupKey() });
			getActivity().invalidateOptionsMenu();

			return true;
		}
		else if (item.getItemId() == R.id.menu_edit)
		{
			Uri uri = Contacts.getLookupUri(mContact.getId(), mContact.getLookupKey());

			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setDataAndType(uri, Contacts.CONTENT_ITEM_TYPE);
			intent.putExtra("finishActivityOnSaveCompleted", true);

			startActivity(intent);
		}
		else if (item.getItemId() == R.id.menu_delete)
		{
			AlertUtil.showAlert(getActivity(), AlertUtil.NONE, R.string.delete_dialog_message, R.string.ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					deleteContact();
				}
			}, R.string.cancel, null);
		}

		return super.onOptionsItemSelected(item);
	}

	private void deleteContact()
	{
		Uri uri = Contacts.getLookupUri(mContact.getId(), mContact.getLookupKey());
		getActivity().getContentResolver().delete(uri, null, null);

		if (listener != null)
			listener.onContactDeleted(mLookupKey);
	}

	@Override
	protected int getCustomLayout()
	{
		return R.layout.fragment_contact_detail;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id)
	{
		super.onListItemClick(listView, view, position, id);

		BaseType contact = (BaseType) getAdapter().getItem(position);

		if (contact instanceof ContactPhone)
		{
			PhoneUtil.callNumber(getActivity(), ((ContactPhone) contact).phoneNumber);
		}
		else if (contact instanceof ContactEmail)
		{
			PhoneUtil.sendMail(getActivity(), ((ContactEmail) contact).email);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		String[] mSelectionArgs = { mLookupKey, StructuredName.CONTENT_ITEM_TYPE };
		return new CursorLoader(getActivity(), ContactsContract.Data.CONTENT_URI, PROJECTION, SELECTION, mSelectionArgs, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
	{
		new LoaderTask().execute(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0)
	{

	}

	private void load()
	{
		String url = mContact.getPhoto();

		final ImageView picture = (ImageView) getView().findViewById(R.id.picture);
		final ViewGroup letterLayout = (ViewGroup) getView().findViewById(R.id.letter_layout);
		final TextView letterText = (TextView) getView().findViewById(R.id.letter_text);

		if (url != null && url.length() > 0)
		{
			ImageLoader.display(picture, Uri.parse(mContact.getPhoto()));
			picture.setVisibility(View.VISIBLE);
			letterLayout.setVisibility(View.GONE);
		}
		else
		{
			if (mContact.getFirstName().length() > 0 && mContact.getLastName().length() > 0)
			{
				letterText.setText(String.valueOf(mContact.getFirstName().charAt(0)).toUpperCase() + " "
									+ String.valueOf(mContact.getLastName().charAt(0)).toUpperCase());
			}
			else if (mContact.getName().length() > 0)
			{
				letterText.setText(mContact.getName().charAt(0) + "");
			}

			letterLayout.setBackgroundResource(ContactsUtil.getSquarePlaceHolder(mContact.getId()));
			picture.setVisibility(View.GONE);
			letterLayout.setVisibility(View.VISIBLE);

			letterLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v)
				{
					Log.d("ContactDetailFragment.load().new OnClickListener() {...}", "onClick: ");

					tmpPhotoUri = generateTempImageUri(getActivity().getApplicationContext());

					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					intent.putExtra("output", tmpPhotoUri);
					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
					intent.setClipData(ClipData.newRawUri("output", tmpPhotoUri));
					startActivityForResult(intent, PICK_PHOTO_CODE);
				}
			});
		}

		List<BaseType> data = new ArrayList<BaseType>();

		if (mContact.getPhoneNumbers() != null && mContact.getPhoneNumbers().size() > 0)
		{
			Header header = new Header(getString(R.string.phone));
			data.add(header);

			SparseArray<ArrayList<ContactPhone>> map = mContact.getPhoneNumbers();
			for (int i = 0, n = map.size(); i < n; i++)
			{
				ArrayList<ContactPhone> list = map.valueAt(i);

				for (ContactPhone contactPhone : list)
				{
					data.add(contactPhone);
				}
			}
		}

		if (mContact.getEmailAddresses() != null && mContact.getEmailAddresses().size() > 0)
		{
			Header header = new Header(getString(R.string.email));
			data.add(header);

			SparseArray<ArrayList<ContactEmail>> map = mContact.getEmailAddresses();
			for (int i = 0, n = map.size(); i < n; i++)
			{
				ArrayList<ContactEmail> list = map.valueAt(i);

				for (ContactEmail contactEmail : list)
				{
					data.add(contactEmail);
				}
			}
		}

		getAdapter().setData(data);

		setListVisible(false);
		setListVisible(true);

		if (getActivity() != null)
			getActivity().invalidateOptionsMenu();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("ContactDetailFragment", "onActivityResult: " + requestCode + " " + data);
		if (requestCode == PICK_PHOTO_CODE && resultCode == Activity.RESULT_OK)
		{
			Uri uri;
			boolean flag = false;

			if (data != null && data.getData() != null)
			{
				uri = data.getData();
			}
			else
			{
				uri = tmpPhotoUri;
				flag = true;
			}

			Log.d("ContactDetailFragment", "onActivityResult: flag " + flag);

			if (!flag)
			{
				Uri uri1 = tmpPhotoUri;
				try
				{
					savePhotoFromUriToUri(getActivity(), uri, uri1, false);
				}
				catch (SecurityException securityexception)
				{
					// Log.d(TAG, (new StringBuilder()).append("Did not have read-access to uri : ").append(uri).toString());
					return;
				}

				doCropPhoto(uri1);
			}
			else
			{
				doCropPhoto(uri);
			}
		}
	}

	private void doCropPhoto(Uri uri)
	{
		Log.d("ContactDetailFragment", "doCropPhoto: ");
		tmpCroppedPhotoUri = generateTempCroppedImageUri(getActivity());

		int maxSize = getMaxSize();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("output", uri);
		intent.addFlags(3);
		intent.setClipData(ClipData.newRawUri("output", uri));
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", maxSize);
		intent.putExtra("outputY", maxSize);

		try
		{
			startActivityForResult(intent, CROP_PHOTO_CODE);
			return;
		}
		catch (Exception exception)
		{
			Log.e("ContactDetailFragment", "Cannot crop image", exception);
		}
	}

	private int getMaxSize()
	{
		Cursor cursor = getActivity().getContentResolver().query(android.provider.ContactsContract.DisplayPhoto.CONTENT_MAX_DIMENSIONS_URI,
				new String[] { "display_max_dim" }, null, null, null);
		int i;
		cursor.moveToFirst();
		i = cursor.getInt(0);
		cursor.close();
		return i;
	}

	public void setActive()
	{
		if (mContact != null)
		{
			getActivity().setTitle(mContact.getName());
		}
	}

	private class LoaderTask extends AsyncTask<Cursor, Void, Contact>
	{

		@Override
		protected Contact doInBackground(Cursor... params)
		{
			mContact = new Contact();
			mContact.setLookupKey(mLookupKey);

			Cursor cursor = params[0];

			if (cursor != null && !cursor.isClosed())
			{
				while (cursor.moveToNext())
				{
					long id = cursor.getLong(0);
					String name = cursor.getString(1);
					String firstName = cursor.getString(2);
					String lastName = cursor.getString(3);
					String photo = cursor.getString(4);
					String photoThumb = cursor.getString(5);
					int starred = cursor.getInt(6);

					mContact.setId(id);

					if (name != null)
						mContact.setName(name);

					if (firstName != null)
						mContact.setFirstName(firstName);

					if (lastName != null)
						mContact.setLastName(lastName);

					if (photo != null)
						mContact.setPhoto(photo);

					if (photoThumb != null)
						mContact.setPhotoThumb(photoThumb);

					mContact.setStarred(starred == 1);
				}

				cursor.close();
			}

			String[] projection = { Phone.TYPE, Phone.NORMALIZED_NUMBER, Phone.LABEL, Phone.NUMBER };
			String selection = Phone.LOOKUP_KEY + " = ?";
			String[] parameters = new String[] { mLookupKey };
			Cursor phones = getActivity().getContentResolver().query(Phone.CONTENT_URI, projection, selection, parameters, null);

			while (phones.moveToNext())
			{
				if (phones.getString(1) != null)
					mContact.addPhoneNumber(phones.getInt(0), phones.getString(1), phones.getString(2));
				else
					mContact.addPhoneNumber(phones.getInt(0), phones.getString(3), phones.getString(2));
			}

			phones.close();

			// Same process for the names
			projection = new String[] { Email.TYPE, Email.ADDRESS };
			selection = Email.LOOKUP_KEY + " = ?";
			parameters = new String[] { mLookupKey };

			Cursor emails = getActivity().getContentResolver().query(Email.CONTENT_URI, projection, selection, parameters, null);

			while (emails.moveToNext())
			{
				mContact.addEmailAddress(emails.getInt(0), emails.getString(1));
			}

			return mContact;
		}

		@Override
		protected void onPostExecute(Contact result)
		{
			super.onPostExecute(result);

			load();
		}
	}

	// Image (choose/crop) management
	public static Uri generateTempImageUri(Context context)
	{
		return FileProvider.getUriForFile(context, "com.abewy.android.apps.contacts.files",
				new File(pathForTempPhoto(context, generateTempPhotoFileName())));
	}

	public static Uri generateTempCroppedImageUri(Context context)
	{
		return FileProvider.getUriForFile(context, "com.abewy.android.apps.contacts.files",
				new File(pathForTempPhoto(context, generateTempCroppedPhotoFileName())));
	}

	private static String pathForTempPhoto(Context context, String s)
	{
		File file = context.getCacheDir();
		file.mkdirs();
		return (new File(file, s)).getAbsolutePath();
	}

	private static String generateTempPhotoFileName()
	{
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat simpledateformat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.US);
		return (new StringBuilder()).append("ContactPhoto-").append(simpledateformat.format(date)).append(".jpg").toString();
	}

	private static String generateTempCroppedPhotoFileName()
	{
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat simpledateformat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.US);
		return (new StringBuilder()).append("ContactPhoto-").append(simpledateformat.format(date)).append("-cropped.jpg").toString();
	}

	/**
	 * Given an input photo stored in a uri, save it to a destination uri
	 */
	public static boolean savePhotoFromUriToUri(Context context, Uri inputUri, Uri outputUri, boolean deleteAfterSave)
	{
		FileOutputStream outputStream = null;
		InputStream inputStream = null;
		try
		{
			outputStream = context.getContentResolver().openAssetFileDescriptor(outputUri, "rw").createOutputStream();
			inputStream = context.getContentResolver().openInputStream(inputUri);

			final byte[] buffer = new byte[16 * 1024];
			int length;
			int totalLength = 0;
			while ((length = inputStream.read(buffer)) > 0)
			{
				outputStream.write(buffer, 0, length);
				totalLength += length;
			}
			Log.v("ContactDetailFragment", "Wrote " + totalLength + " bytes for photo " + inputUri.toString());
		}
		catch (IOException e)
		{
			Log.e("ContactDetailFragment", "Failed to write photo: " + inputUri.toString() + " because: " + e);
			return false;
		}
		finally
		{
			Closeables.closeQuietly(inputStream);
			Closeables.closeQuietly(outputStream);
			if (deleteAfterSave)
			{
				context.getContentResolver().delete(inputUri, null, null);
			}
		}
		return true;
	}
}
