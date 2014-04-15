package com.abewy.android.apps.contacts.adapter;

import android.view.View;
import android.widget.TextView;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.adapter.holder.SimpleTextHolder;
import com.abewy.android.extended.items.BaseType;
import com.abewy.android.extended.items.TextItem;

public class TextItemAdapter extends ContactBaseAdapter
{
	public TextItemAdapter()
	{
		super();
	}

	@Override
	protected int getLayoutRes()
	{
		return R.layout.item_text;
	}

	@Override
	protected void attachViewHolder(View view)
	{
		TextView primaryText = (TextView) view.findViewById(R.id.primary_text);

		setHolder(view, new SimpleTextHolder(primaryText));
	}

	@Override
	public void bindData(View view, BaseType data, int position)
	{
		final SimpleTextHolder holder = (SimpleTextHolder) getHolder(view);

		final TextItem item = ((TextItem) data);

		holder.primaryText.setText(item.text);
	}
}
