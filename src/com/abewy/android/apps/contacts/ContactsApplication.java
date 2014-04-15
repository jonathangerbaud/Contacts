package com.abewy.android.apps.contacts;

import android.graphics.Typeface;
import android.preference.PreferenceManager;
import com.abewy.android.apps.contacts.core.CoreApplication;
import com.abewy.android.apps.contacts.imageloader.ImageLoader;

public class ContactsApplication extends CoreApplication
{
	private boolean  mIsFirstLaunch = true;
	
	public static ContactsApplication getInstance()
	{
		return (ContactsApplication) CoreApplication.getInstance();
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	protected void initPreferences()
	{
		//PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	@Override
	protected void initGlobals()
	{
		//KlyphLocale.setAppLocale(KlyphLocale.getAppLocale());
	}
	
	@Override
	protected void initOthers()
	{
		ImageLoader.initImageLoader(this);
		Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
		Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
		//ImageLoader.FADE_ENABLED = KlyphPreferences.isPhotoEffectEnabled();
	}
	
	public boolean  isFirstLaunch()
	{
		return mIsFirstLaunch;
	}
	
	public void launchComplete()
	{
		mIsFirstLaunch = false;
	}
}