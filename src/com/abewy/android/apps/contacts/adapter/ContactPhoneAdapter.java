package com.abewy.android.apps.contacts.adapter;

import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.adapter.holder.ContactPhoneHolder;
import com.abewy.android.apps.contacts.model.ContactPhone;
import com.abewy.android.extended.items.BaseType;
import com.abewy.android.extended.util.PhoneUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class ContactPhoneAdapter extends ContactBaseAdapter
{
	public ContactPhoneAdapter()
	{
		super();
	}

	@Override
	protected int getLayoutRes()
	{
		return R.layout.item_contact_phone;
	}

	@Override
	protected void attachViewHolder(View view)
	{
		TextView primaryText = (TextView) view.findViewById(R.id.primary_text);
		TextView secondaryText = (TextView) view.findViewById(R.id.secondary_text);
		ImageButton button = (ImageButton) view.findViewById(R.id.message_button);

		setHolder(view, new ContactPhoneHolder(primaryText, secondaryText, button));
	}

	@Override
	public void bindData(View view, BaseType data, int position)
	{
		final ContactPhoneHolder holder = (ContactPhoneHolder) getHolder(view);

		final ContactPhone phone = ((ContactPhone) data);

		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		
		try
		{
			PhoneNumber numberProto = phoneUtil.parse(phone.phoneNumber, "");
			holder.primaryText.setText(phoneUtil.format(numberProto, PhoneNumberFormat.NATIONAL));
		}
		catch (NumberParseException e)
		{
			holder.primaryText.setText(phone.phoneNumber);
		}

		holder.secondaryText.setText(Phone.getTypeLabel(holder.secondaryText.getResources(), phone.type, phone.label));
		
		holder.messageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				PhoneUtil.sendSMS(v.getContext(), phone.phoneNumber);
			}
		});
	}
}