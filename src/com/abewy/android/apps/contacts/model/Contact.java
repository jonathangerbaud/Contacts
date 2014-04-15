/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.model;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;

public class Contact extends BaseContact
{
	private long									id;
	private String									lookupKey;
	private String									name;
	private String									firstName;
	private String									lastName;
	private String									photo;
	private String									photoThumb;
	private SparseArray<ArrayList<ContactPhone>>	phoneNumbers;		// Keys must be constants of CommonDataKinds.Phone
	private SparseArray<ArrayList<ContactEmail>>	emailAddresses;	// Keys must be constants of CommonDataKinds.Email
	private boolean									starred	= false;

	public Contact()
	{

	}

	@Override
	public int getItemViewType()
	{
		return ObjectType.CONTACT;
	}

	@Override
	public String getItemPrimaryLabel()
	{
		return getName();
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getLookupKey()
	{
		return lookupKey;
	}

	public void setLookupKey(String lookupKey)
	{
		this.lookupKey = lookupKey;
	}

	public String getName()
	{
		if (name == null)
			name = "";

		return name;
	}

	public void setName(String name)
	{
		this.name = StringUtils.capitalize(name);
	}

	public String getFirstName()
	{
		if (firstName == null)
			firstName = "";

		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		if (lastName == null)
			return "";

		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getPhoto()
	{
		return photo;
	}

	public void setPhoto(String photo)
	{
		this.photo = photo;
	}

	public String getPhotoThumb()
	{
		return photoThumb;
	}

	public void setPhotoThumb(String photoThumb)
	{
		this.photoThumb = photoThumb;
	}

	public SparseArray<ArrayList<ContactPhone>> getPhoneNumbers()
	{
		if (phoneNumbers == null)
			phoneNumbers = new SparseArray<ArrayList<ContactPhone>>();

		return phoneNumbers;
	}

	public void setPhoneNumbers(SparseArray<ArrayList<ContactPhone>> phoneNumbers)
	{
		this.phoneNumbers = phoneNumbers;
	}

	public SparseArray<ArrayList<ContactEmail>> getEmailAddresses()
	{
		if (emailAddresses == null)
			emailAddresses = new SparseArray<ArrayList<ContactEmail>>();

		return emailAddresses;
	}

	public void setEmailAddresses(SparseArray<ArrayList<ContactEmail>> emailAddresses)
	{
		this.emailAddresses = emailAddresses;
	}

	public boolean isStarred()
	{
		return starred;
	}

	public void setStarred(boolean starred)
	{
		this.starred = starred;
	}

	/**
	 * Add a phone number to the contact phone numbers
	 * 
	 * @param type Must one of the CommonDataKinds.Phone constants
	 * @param number The phone number
	 */
	public void addPhoneNumber(int type, String number)
	{
		addPhoneNumber(type, number, "");
	}

	/**
	 * Add a phone number to the contact phone numbers
	 * 
	 * @param type Must one of the CommonDataKinds.Phone constants
	 * @param number The phone number
	 * @param label The label in case the type is Phone.TYPE_CUSTOM
	 */
	public void addPhoneNumber(int type, String number, String label)
	{
		if (phoneNumbers == null)
			phoneNumbers = new SparseArray<ArrayList<ContactPhone>>();

		if (phoneNumbers.get(type) == null)
			phoneNumbers.put(type, new ArrayList<ContactPhone>());

		ArrayList<ContactPhone> list = phoneNumbers.get(type);
		list.add(new ContactPhone(number, type, label));
	}

	/**
	 * Add a phone number to the contact phone numbers
	 * 
	 * @param type Must one of the CommonDataKinds.Phone constants
	 * @param number The phone number
	 */
	public void addEmailAddress(int type, String email)
	{
		addEmailAddress(type, email, "");
	}

	/**
	 * Add a phone number to the contact phone numbers
	 * 
	 * @param type Must one of the CommonDataKinds.Phone constants
	 * @param number The phone number
	 * @param label The email label in case type is Email.TYPE_CUSTOM
	 */
	public void addEmailAddress(int type, String email, String label)
	{
		if (emailAddresses == null)
			emailAddresses = new SparseArray<ArrayList<ContactEmail>>();

		if (emailAddresses.get(type) == null)
			emailAddresses.put(type, new ArrayList<ContactEmail>());

		ArrayList<ContactEmail> list = emailAddresses.get(type);
		list.add(new ContactEmail(email, type, label));
	}

	public String getInitials()
	{
		if (getFirstName().length() > 0 && getLastName().length() > 0)
		{

			return String.valueOf(getFirstName().charAt(0)).toUpperCase() + " " + String.valueOf(getLastName().charAt(0)).toUpperCase();
		}

		return String.valueOf(getName().charAt(0)).toUpperCase();
	}

	public boolean filter(String filter)
	{
		if (name.toLowerCase().indexOf(filter) != -1)
			return true;

		if (phoneNumbers != null)
		{
			for (int i = 0, n = phoneNumbers.size(); i < n; i++)
			{
				ArrayList<ContactPhone> phones = phoneNumbers.valueAt(i);
				for (ContactPhone contactPhone : phones)
				{
					if (contactPhone.phoneNumber.indexOf(filter) != -1)
						return true;
				}
			}
		}

		if (emailAddresses != null)
		{
			for (int i = 0, n = emailAddresses.size(); i < n; i++)
			{
				ArrayList<ContactEmail> emails = emailAddresses.valueAt(i);
				for (ContactEmail contactEmail : emails)
				{
					if (contactEmail.email.indexOf(filter) != -1)
						return true;
				}
			}
		}

		return false;
	}
}
