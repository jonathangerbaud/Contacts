/**
* @author Jonathan
*/

package com.abewy.android.apps.contacts.app;

import java.util.List;
import com.abewy.android.apps.contacts.model.Contact;
import com.abewy.android.extended.items.BaseType;

public interface IContactFragment
{
	public void setContacts(List<BaseType> contacts);
	public void filter(String filter);
	public List<Contact> getDisplayedContacts();
}
