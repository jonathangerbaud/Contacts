package com.abewy.android.apps.contacts.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.QuickContact;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.adapter.holder.ContactHolder2;
import com.abewy.android.apps.contacts.imageloader.ImageLoader;
import com.abewy.android.apps.contacts.model.Contact;
import com.abewy.android.apps.contacts.model.ContactEmail;
import com.abewy.android.apps.contacts.model.ContactPhone;
import com.abewy.android.apps.contacts.util.ContactsUtil;
import com.abewy.android.extended.items.BaseType;
import com.abewy.android.extended.util.PhoneUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class ContactAdapter extends ContactBaseAdapter
{
	public ContactAdapter()
	{
		super();
	}

	@Override
	protected int getLayoutRes()
	{
		return R.layout.item_contact_list_item;
	}

	@Override
	protected void attachViewHolder(View view)
	{
		TextView letterText = (TextView) view.findViewById(R.id.letter_text);
		ImageView picture = (ImageView) view.findViewById(R.id.picture);
		TextView friendName = (TextView) view.findViewById(R.id.primary_text);
		TextView phoneNumber = (TextView) view.findViewById(R.id.secondary_text);
		ImageButton overflowButton = (ImageButton) view.findViewById(R.id.overflow_button);

		setHolder(view, new ContactHolder2(letterText, picture, friendName, phoneNumber, overflowButton));
	}

	@Override
	public void bindData(View view, BaseType data, int position)
	{
		final ContactHolder2 holder = (ContactHolder2) getHolder(view);

		final Contact contact = (Contact) data;

		holder.primaryText.setText(contact.getName());

		if (contact.getPhoneNumbers().size() > 0)
		{
			ContactPhone cp = contact.getPhoneNumbers().valueAt(0).get(0);
			
			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			
			try
			{
				PhoneNumber numberProto = phoneUtil.parse(cp.phoneNumber, "");
				holder.secondaryText.setText(phoneUtil.format(numberProto, PhoneNumberFormat.NATIONAL));
			}
			catch (NumberParseException e)
			{
				holder.secondaryText.setText(cp.phoneNumber);
			}
			
			holder.secondaryText.setVisibility(View.VISIBLE);
		}
		else if (contact.getEmailAddresses().size() > 0)
		{
			ContactEmail contactEmail = contact.getEmailAddresses().valueAt(0).get(0);
			holder.secondaryText.setText(contactEmail.email);
			holder.secondaryText.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.secondaryText.setVisibility(View.GONE);
		}

		String url = contact.getPhotoThumb();

		if (url != null && url.length() > 0)
		{
			ImageLoader.display(holder.picture, Uri.parse(contact.getPhoto()));
			holder.letterText.setVisibility(View.GONE);
		}
		else
		{
			holder.letterText.setText(contact.getInitials());
			holder.picture.setImageResource(ContactsUtil.getDrawable(contact.getId()));
			holder.letterText.setVisibility(View.VISIBLE);
		}
		
		((View) holder.picture.getParent()).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v)
			{
				Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, contact.getLookupKey());
				QuickContact.showQuickContact(holder.picture.getContext(), holder.picture, uri, ContactsContract.QuickContact.MODE_LARGE, null);
			}
		});

		holder.overflowButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v)
			{
				displayPopupMenu(v, contact);
			}
		});
	}

	private void displayPopupMenu(final View view, final Contact contact)
	{
		PopupMenu popup = new PopupMenu(view.getContext(), view);

		if (contact.getPhoneNumbers() != null && contact.getPhoneNumbers().size() > 0)
		{
			popup.getMenu().add(Menu.NONE, R.id.menu_call, Menu.NONE, R.string.menu_call);
			popup.getMenu().add(Menu.NONE, R.id.menu_send_sms, Menu.NONE, R.string.menu_send_sms);
		}

		if (contact.getEmailAddresses() != null && contact.getEmailAddresses().size() > 0)
		{
			popup.getMenu().add(Menu.NONE, R.id.menu_send_email, Menu.NONE, R.string.menu_send_email);
		}

		popup.getMenu().add(Menu.NONE, R.id.menu_add_favorite, Menu.NONE, contact.isStarred() ? R.string.menu_remove_favorite : R.string.menu_add_favorite);

		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				int id = item.getItemId();

				if (id == R.id.menu_call)
				{
					handleCall(view.getContext(), contact);
					return true;
				}
				if (id == R.id.menu_send_sms)
				{
					handleSMS(view.getContext(), contact);
					return true;
				}
				if (id == R.id.menu_send_email)
				{
					handleEmail(view.getContext(), contact);
					return true;
				}
				if (id == R.id.menu_add_favorite)
				{
					handleFavorite(view.getContext(), contact);
					return true;
				}

				return false;
			}
		});

		view.setTag(popup);

		popup.show();
	}

	private void handleCall(final Context context, final Contact contact)
	{
		PhoneUtil.callNumber(context, contact.getPhoneNumbers().valueAt(0).get(0).phoneNumber);
	}
	
	private void handleSMS(final Context context, final Contact contact)
	{
		PhoneUtil.sendSMS(context, contact.getPhoneNumbers().valueAt(0).get(0).phoneNumber);
	}
	
	private void handleEmail(final Context context, final Contact contact)
	{
		PhoneUtil.sendMail(context, contact.getEmailAddresses().valueAt(0).get(0).email);
	}
	
	private void handleFavorite(final Context context, final Contact contact)
	{
		ContentValues values = new ContentValues();
		values.put(Contacts.STARRED, contact.isStarred() ? 0 : 1);
		contact.setStarred(!contact.isStarred());

		context.getContentResolver().update(Contacts.CONTENT_URI, values, Contacts.LOOKUP_KEY + "= ?", new String[] { contact.getLookupKey() });
	}
}
