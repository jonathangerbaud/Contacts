/**
* @author Jonathan
*/

package com.abewy.android.apps.contacts;

import android.content.Context;
import android.util.Log;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

public class ViewAsActionProvider extends ActionProvider
{
	private Context mContext;
	private View mView;
	
	public ViewAsActionProvider(Context context)
	{
		super(context);
		Log.d("ViewAsActionProvider", "ViewAsActionProvider: ");
		this.mContext = context;
	}

	@Override
	@Deprecated
	public View onCreateActionView()
	{
		Log.d("ViewAsActionProvider", "onCreateActionView: ");
		// Inflate the action view to be shown on the action bar.
	    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
	    ImageButton button = (ImageButton) layoutInflater.inflate(R.layout.menu_view_as_item, null);
	    /*button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				Log.d("ViewAsActionProvider.onCreateActionView().new OnClickListener() {...}", "onClick: ");
				displayPopupMenu();
			}
		});*/
	    
	    mView = button;

		return button;
	}

	@Override
	public boolean hasSubMenu()
	{
		return true;
	}

	@Override
	public void onPrepareSubMenu(SubMenu subMenu)
	{
		//super.onPrepareSubMenu(subMenu);
		subMenu.clear();
		
		MenuItem list = subMenu.add("List");
		list.setIcon(R.drawable.ic_action_view_as_list);
		
		MenuItem gridA = subMenu.add("Big grid");
		gridA.setIcon(R.drawable.ic_action_view_as_big_grid);
		
		MenuItem gridB = subMenu.add("Small grid");
		gridB.setIcon(R.drawable.ic_action_view_as_small_grid);
	}
	
	private void displayPopupMenu()
	{
		PopupMenu menu = new PopupMenu(mContext, mView);
		
		menu.getMenu().add("List").setIcon(R.drawable.ic_action_view_as_list);
		menu.getMenu().add("Big grid").setIcon(R.drawable.ic_action_view_as_big_grid);
		menu.getMenu().add("Small grid").setIcon(R.drawable.ic_action_view_as_small_grid);
		
		menu.show();
	}
}
