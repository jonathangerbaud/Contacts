package com.abewy.android.apps.contacts.imageloader;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import com.abewy.android.apps.contacts.core.CoreApplication;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class ImageLoader
{
	public static boolean		FADE_ENABLED	= true;
	public static final String	FAKE_URI		= "http://www.abc.com/12398752.jpg";
	protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

	public static void initImageLoader(Context context)
	{

	}
	
	public static void display(ImageView imageView, Uri uri)
	{
		display(imageView, uri, false, 0, null);
	}

	public static void display(ImageView imageView, Uri uri, boolean fadeIn)
	{
		display(imageView, uri, fadeIn, 0, null);
	}

	public static void display(ImageView imageView, Uri uri, int stubImage)
	{
		display(imageView, uri, false, stubImage, null);
	}

	public static void display(ImageView imageView, Uri uri, boolean fadeIn, int stubImage)
	{
		display(imageView, uri, fadeIn, stubImage, null);
	}

	public static void display(ImageView imageView, Uri uri, ImageLoaderListener listener)
	{
		display(imageView, uri, false, 0, listener);
	}

	public static void display(ImageView imageView, Uri uri, boolean fadeIn, int stubImage, ImageLoaderListener listener)
	{
		/*if (uri == null || uri.length() == 0)
			uri = FAKE_URI;
		
		//uri = Uri.encode(uri, ALLOWED_URI_CHARS);*/

		Picasso picasso = Picasso.with(imageView.getContext());
		RequestCreator requestCreator = picasso.load(uri);

		if (stubImage != 0)
		{
			requestCreator.placeholder(stubImage);
			requestCreator.error(stubImage);
		}

		if (!(fadeIn && FADE_ENABLED))
			requestCreator.noFade();

		LayoutParams params = imageView.getLayoutParams();

		if (params.width > 0 && params.height > 0)
		{
			requestCreator.resize(params.width, params.height, true);
		}

		requestCreator.inSampleSize(true);
		requestCreator.into(imageView, listener);
	}

	public static void display(ImageView imageView, String uri)
	{
		display(imageView, uri, false, 0, null);
	}

	public static void display(ImageView imageView, String uri, boolean fadeIn)
	{
		display(imageView, uri, fadeIn, 0, null);
	}

	public static void display(ImageView imageView, String uri, int stubImage)
	{
		display(imageView, uri, false, stubImage, null);
	}

	public static void display(ImageView imageView, String uri, boolean fadeIn, int stubImage)
	{
		display(imageView, uri, fadeIn, stubImage, null);
	}

	public static void display(ImageView imageView, String uri, ImageLoaderListener listener)
	{
		display(imageView, uri, false, 0, listener);
	}

	public static void display(ImageView imageView, String uri, boolean fadeIn, int stubImage, ImageLoaderListener listener)
	{
		if (uri == null || uri.length() == 0)
			uri = FAKE_URI;
		
		uri = Uri.encode(uri, ALLOWED_URI_CHARS);

		Picasso picasso = Picasso.with(imageView.getContext());
		RequestCreator requestCreator = picasso.load(uri);

		if (stubImage != 0)
		{
			requestCreator.placeholder(stubImage);
			requestCreator.error(stubImage);
		}

		if (!(fadeIn && FADE_ENABLED))
			requestCreator.noFade();

		LayoutParams params = imageView.getLayoutParams();

		if (params.width > 0 && params.height > 0)
		{
			requestCreator.resize(params.width, params.height, true);
		}

		requestCreator.inSampleSize(true);
		requestCreator.into(imageView, listener);
	}

	public static void displayNoScaling(ImageView imageView, String uri, boolean fadeIn, int stubImage, ImageLoaderListener listener)
	{
		if (uri == null || uri.length() == 0)
			uri = FAKE_URI;

		Picasso picasso = Picasso.with(imageView.getContext());
		RequestCreator requestCreator = picasso.load(uri);

		if (stubImage != 0)
		{
			requestCreator.placeholder(stubImage);
			requestCreator.error(stubImage);
		}

		if (!(fadeIn && FADE_ENABLED))
			requestCreator.noFade();

		requestCreator.into(imageView, listener);
	}

	public static void loadImage(String uri, FakeImageLoaderListener listener)
	{
		if (uri == null || uri.length() == 0)
			uri = FAKE_URI;

		Picasso.with(CoreApplication.getInstance()).load(uri).into(listener);
	}

	public static void cancelDisplay(ImageView imageView)
	{
		if (imageView != null)
		{
			Picasso.with(imageView.getContext()).cancelRequest(imageView);
		}
	}

	public static void clearImageCache()
	{
		//Picasso.with(BaseApplication.getInstance()).clearCache();
	}
}
