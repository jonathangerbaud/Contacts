package com.abewy.android.apps.contacts.core;

import android.annotation.TargetApi;
import android.app.Application;
import android.util.Log;
import com.crashlytics.android.Crashlytics;

public abstract class CoreApplication extends Application
{
	private static CoreApplication instance;
	
	@Override
	public void onCreate()
	{
		instance = this;
		
		initGlobals();
		initBugReport();
		initPreferences();
		initOthers();

		super.onCreate();
	}
	
	public static CoreApplication getInstance()
	{
		return instance;
	}

	private void initBugReport()
	{
		if (CoreFlags.ENABLE_BUG_REPORT)
		{
			Log.d("CoreApplication", "initBugReport: ");
			Crashlytics.start(this);
		}
	}

	protected abstract void initPreferences();

	protected abstract void initGlobals();
	
	protected abstract void initOthers();

	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		Log.i("BaseApplication", "onLowMemory");
	}

	@Override
	@TargetApi(14)
	public void onTrimMemory(int level)
	{
		super.onTrimMemory(level);
		Log.i("BaseApplication", "onTrimMemory");
	}
	
	public static String generateIabKey()
	{
		return "[YOUR_KEY]";
	}
}

