/**
* @author Jonathan
*/

package com.abewy.android.apps.contacts.model;


public class ContactPhone extends BaseContact
{
	public final String phoneNumber;
	public final int type;
	public final String label;
	
	public ContactPhone(String phoneNumber, int type)
	{
		this(phoneNumber, type, "");
	}
	
	public ContactPhone(String phoneNumber, int type, String label)
	{
		this.phoneNumber = phoneNumber;
		this.type = type;
		this.label = label;
	}

	@Override
	public int getItemViewType()
	{
		return ObjectType.CONTACT_PHONE;
	}

	@Override
	public String getItemPrimaryLabel()
	{
		return phoneNumber;
	}
}
