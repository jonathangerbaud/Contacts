package com.abewy.android.apps.contacts.adapter;

import android.view.View;
import android.widget.TextView;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.adapter.holder.GroupHolder;
import com.abewy.android.apps.contacts.model.Group;
import com.abewy.android.extended.items.BaseType;

public class GroupAdapter extends ContactBaseAdapter
{
	public GroupAdapter()
	{
		super();
	}
	
	@Override
	protected int getLayoutRes()
	{
		return R.layout.item_dropdown;
	}
	
	@Override
	protected void attachViewHolder(View view)
	{
		view.setTag(new GroupHolder((TextView) view.findViewById(R.id.title), (TextView) view.findViewById(R.id.size)));
	}
	
	@Override
	public void bindData(View view, BaseType data, int position)
	{
		Group group = (Group) data;
		
		GroupHolder holder = (GroupHolder) view.getTag();
		holder.title.setText(group.getTitle());
		holder.size.setText(String.valueOf(group.getSize()));
	}

	@Override
	public boolean isEnabled(BaseType object)
	{
		return true;
	}	
}
