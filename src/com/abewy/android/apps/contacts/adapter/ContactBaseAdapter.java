package com.abewy.android.apps.contacts.adapter;

import android.view.View;
import android.widget.ImageView;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.R.id;
import com.abewy.android.apps.contacts.imageloader.ImageLoader;
import com.abewy.android.extended.adapter.TypeAdapter;
import com.abewy.android.extended.items.BaseType;

public abstract class ContactBaseAdapter extends TypeAdapter<BaseType>
{

	public ContactBaseAdapter()
	{
		
	}
	
	@Override
	protected int getLayoutRes()
	{
		return 0;
	}
		
	@Override
	public void bindData(View view, BaseType object, int position)
	{
	}
	
	@Override
	public boolean  isEnabled(BaseType object)
	{
		return true;
	}
	
	@Override
	protected void attachViewHolder(View view)
	{
		
	}

	@Override
	public void setLayoutParams(View view)
	{
		
	}

	protected void loadImage(ImageView imageView, String url)
	{
		ImageLoader.display(imageView, url);
	}

	protected void loadImage(ImageView imageView, String url, int placeHolder)
	{
		ImageLoader.display(imageView, url, placeHolder);
	}

	protected void loadImage(ImageView imageView, String url, boolean fadeIn)
	{
		ImageLoader.display(imageView, url, fadeIn);
	}

	protected void loadImage(ImageView imageView, String url, int placeHolder, boolean fadeIn)
	{
		ImageLoader.display(imageView, url, fadeIn, placeHolder);
	}
	
	protected void setHolder(View view, Object holder)
	{
		view.setTag(R.id.view_holder, holder);
	}

	protected Object getHolder(View view)
	{
		return view.getTag(R.id.view_holder);
	}
}
