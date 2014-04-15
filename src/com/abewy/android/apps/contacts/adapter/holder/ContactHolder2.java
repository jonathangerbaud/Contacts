package com.abewy.android.apps.contacts.adapter.holder;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactHolder2
{
	public final TextView		letterText;
	public final ImageView		picture;
	public final TextView		primaryText;
	public final TextView		secondaryText;
	public final ImageButton	overflowButton;

	public ContactHolder2(TextView letterText, ImageView picture, TextView primaryText, TextView secondaryText, ImageButton overflowButton)
	{
		this.letterText = letterText;
		this.picture = picture;
		this.primaryText = primaryText;
		this.secondaryText = secondaryText;
		this.overflowButton = overflowButton;
	}
}