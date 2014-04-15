package com.abewy.android.apps.contacts.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import android.support.v4.util.LongSparseArray;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.core.CorePrefs;
import com.abewy.android.apps.contacts.model.Contact;
import com.abewy.android.apps.contacts.model.ContactEmail;
import com.abewy.android.apps.contacts.model.ContactPhone;
import com.abewy.android.extended.items.BaseType;

public class ContactsUtil
{
	private static LongSparseArray<Integer>	map			= new LongSparseArray<Integer>();

	private static int[]					circleBgds	= new int[] {
					R.drawable.circle_bgd_1,
					R.drawable.circle_bgd_2,
					R.drawable.circle_bgd_3,
					R.drawable.circle_bgd_4,
					R.drawable.circle_bgd_5,
					R.drawable.circle_bgd_6,
					R.drawable.circle_bgd_7,
					R.drawable.circle_bgd_8,
					R.drawable.circle_bgd_9,
					R.drawable.circle_bgd_10,
					R.drawable.circle_bgd_11,
					R.drawable.circle_bgd_12,
					R.drawable.circle_bgd_13,
					R.drawable.circle_bgd_14,
					R.drawable.circle_bgd_15,
					R.drawable.circle_bgd_16			};

	private static int[]					squareBgds	= new int[] {
					R.drawable.square_bgd_1,
					R.drawable.square_bgd_2,
					R.drawable.square_bgd_3,
					R.drawable.square_bgd_4,
					R.drawable.square_bgd_5,
					R.drawable.square_bgd_6,
					R.drawable.square_bgd_7,
					R.drawable.square_bgd_8,
					R.drawable.square_bgd_9,
					R.drawable.square_bgd_10,
					R.drawable.square_bgd_11,
					R.drawable.square_bgd_12,
					R.drawable.square_bgd_13,
					R.drawable.square_bgd_14,
					R.drawable.square_bgd_15,
					R.drawable.square_bgd_16			};

	private static int						current		= 0;

	private static final int				BGD_COUNT	= squareBgds.length;

	public static int getSquarePlaceHolder(long id)
	{
		if (map.indexOfKey(id) < 0)
			map.append(id, getNextIndex());

		int index = map.get(id);
		
		return squareBgds[index % BGD_COUNT];
	}

	public static int getCirclePlaceHolder(long id)
	{
		if (map.indexOfKey(id) < 0)
			map.append(id, getNextIndex());

		int index = map.get(id);
		
		return circleBgds[index % BGD_COUNT];
	}

	public static int getDrawable(long id)
	{
		if (CorePrefs.isRoundedPictures())
			return getCirclePlaceHolder(id);
		else
			return getSquarePlaceHolder(id);
	}

	private static int getNextIndex()
	{
		if (current >= squareBgds.length)
			current = 0;

		return current++;
	}
	
	public static ArrayList<BaseType> filter(String query, List<BaseType> lookingInto)
	{
		ArrayList<BaseType> list = new ArrayList<BaseType>();
		query = query.toLowerCase();

		for (BaseType baseType : lookingInto)
		{
			Contact contact = (Contact) baseType;
			
			if (StringUtils.startsWith(contact.getName().toLowerCase(), query))
			{
				list.add(contact);
				continue;
			}

			if (StringUtils.startsWith(contact.getFirstName().toLowerCase(), query))
			{
				list.add(contact);
				continue;
			}

			if (StringUtils.startsWith(contact.getLastName().toLowerCase(), query))
			{
				list.add(contact);
				continue;
			}

			if (contact.getEmailAddresses().size() > 0)
			{
				for (int i = 0, n = contact.getPhoneNumbers().size(); i < n; i++)
				{
					ArrayList<ContactEmail> emails = contact.getEmailAddresses().valueAt(i);

					for (ContactEmail email : emails)
					{
						if (email.email.toLowerCase().indexOf(query) != -1)
						{
							Contact c = new Contact();
							c.setId(contact.getId());
							c.setLookupKey(contact.getLookupKey());
							c.setName(contact.getName());
							c.setFirstName(contact.getFirstName());
							c.setLastName(contact.getLastName());
							c.addEmailAddress(contact.getEmailAddresses().keyAt(i), email.email);
							list.add(c);
							continue;
						}
					}
				}
			}

			if (contact.getPhoneNumbers().size() > 0)
			{
				for (int i = 0, n = contact.getPhoneNumbers().size(); i < n; i++)
				{
					ArrayList<ContactPhone> phones = contact.getPhoneNumbers().valueAt(i);

					for (ContactPhone phone : phones)
					{
						if (phone.phoneNumber.toLowerCase().indexOf(query) != -1)
						{
							Contact c = new Contact();
							c.setId(contact.getId());
							c.setLookupKey(contact.getLookupKey());
							c.setName(contact.getName());
							c.setFirstName(contact.getFirstName());
							c.setLastName(contact.getLastName());
							c.addPhoneNumber(contact.getPhoneNumbers().keyAt(i), phone.phoneNumber);
							list.add(c);
							continue;
						}
					}
				}
			}
		}

		return list;
	}
}
