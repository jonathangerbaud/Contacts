package com.abewy.android.apps.contacts.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.adapter.holder.ContactHolder;
import com.abewy.android.apps.contacts.core.CorePrefs;
import com.abewy.android.apps.contacts.imageloader.ImageLoader;
import com.abewy.android.apps.contacts.model.Contact;
import com.abewy.android.apps.contacts.util.ContactsUtil;
import com.abewy.android.extended.items.BaseType;

public class ContactBigGridAdapter extends ContactBaseAdapter
{
	public ContactBigGridAdapter()
	{
		super();
	}

	@Override
	protected int getLayoutRes()
	{
		if (CorePrefs.isRoundedPictures())
			return R.layout.item_contact_big_grid_rounded_item;
			
		return R.layout.item_contact_big_grid_item;
	}

	@Override
	protected void attachViewHolder(View view)
	{
		TextView letterText = (TextView) view.findViewById(R.id.letter_text);
		ImageView friendPicture = (ImageView) view.findViewById(R.id.picture);
		TextView friendName = (TextView) view.findViewById(R.id.primary_text);

		setHolder(view, new ContactHolder(letterText, friendPicture, friendName));
	}

	@Override
	public void bindData(View view, BaseType data, int position)
	{
		ContactHolder holder = (ContactHolder) getHolder(view);

		Contact contact = (Contact) data;

		holder.primaryText.setText(contact.getName());

		String url = contact.getPhoto();

		if (url != null && url.length() > 0)
		{
			ImageLoader.display(holder.picture, url);
			holder.letterText.setVisibility(View.GONE);
		}
		else
		{
			holder.letterText.setText(contact.getInitials());
			holder.picture.setImageResource(ContactsUtil.getDrawable(contact.getId()));
			holder.letterText.setVisibility(View.VISIBLE);
		}
	}
}
