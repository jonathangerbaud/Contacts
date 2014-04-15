/**
* @author Jonathan
*/

package com.abewy.android.apps.contacts.model;


public class ContactEmail extends BaseContact
{
	public final String email;
	public final int type;
	public final String label;
	
	public ContactEmail(String email, int type)
	{
		this(email, type, "");
	}
	
	public ContactEmail(String email, int type, String label)
	{
		this.email = email;
		this.type = type;
		this.label = label;
	}

	@Override
	public int getItemViewType()
	{
		return ObjectType.CONTACT_EMAIL;
	}

	@Override
	public String getItemPrimaryLabel()
	{
		return email;
	}
}
