package com.abewy.android.apps.contacts.adapter.holder;

import android.widget.ImageButton;
import android.widget.TextView;

public class ContactPhoneHolder
{
	public final TextView		primaryText;
	public final TextView		secondaryText;
	public final ImageButton	messageButton;

	public ContactPhoneHolder(TextView primaryText, TextView secondaryText, ImageButton messageButton)
	{
		this.primaryText = primaryText;
		this.secondaryText = secondaryText;
		this.messageButton = messageButton;
	}
}