package com.abewy.android.apps.contacts.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import com.abewy.android.apps.contacts.R;
import com.inscription.ChangeLogDialog;

public class ChangeLogActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changelog);

		setTitle(R.string.changelog);
		
		WebView webView = (WebView) findViewById(R.id.webView);
		
		ChangeLogDialog cld = new ChangeLogDialog(this);
		webView.loadDataWithBaseURL(null, cld.getHTML(), "text/html", "utf-8", null);
	}

	@Override
	public boolean  onCreateOptionsMenu(Menu menu)
	{
		return false;
	}
}
