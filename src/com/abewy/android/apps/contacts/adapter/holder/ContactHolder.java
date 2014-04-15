package com.abewy.android.apps.contacts.adapter.holder;

import android.widget.ImageView;
import android.widget.TextView;

public class ContactHolder
{
	public final TextView	letterText;
	public final ImageView	picture;
	public final TextView	primaryText;

	public ContactHolder(TextView letterText, ImageView picture, TextView primaryText)
	{
		this.letterText = letterText;
		this.picture = picture;
		this.primaryText = primaryText;
	}
}