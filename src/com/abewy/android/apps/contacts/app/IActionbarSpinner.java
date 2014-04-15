/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.app;

import java.util.List;
import android.app.ActionBar.OnNavigationListener;
import com.abewy.android.extended.items.BaseType;

public interface IActionbarSpinner
{
	public void displaySpinnerInActionBar(String[] array, int position, OnNavigationListener listener);
	
	public void displaySpinnerInActionBar(int array, int position, OnNavigationListener listener);

	public void displaySpinnerInActionBar(List<BaseType> data, int position, OnNavigationListener listener);

	public void removeSpinnerInActionBar();
}
