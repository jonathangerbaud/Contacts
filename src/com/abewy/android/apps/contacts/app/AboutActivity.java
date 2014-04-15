package com.abewy.android.apps.contacts.app;

import java.util.Calendar;
import java.util.GregorianCalendar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.extended.util.ApplicationUtil;
import com.abewy.android.extended.util.PhoneUtil;

public class AboutActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		

		setTitle(R.string.about_activity_title);
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_background_transparent_gradient));
		getActionBar().setDisplayHomeAsUpEnabled(true);

		ImageView companyLogo = (ImageView) findViewById(R.id.company_logo);

		companyLogo.setClickable(true);
		companyLogo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v)
			{
				PhoneUtil.openURL(AboutActivity.this, getString(R.string.company_url));
			}
		});

		TextView version = (TextView) findViewById(R.id.version);
		TextView copyright = (TextView) findViewById(R.id.copyright);

		version.setText(getString(R.string.about_version, ApplicationUtil.getAppVersion(this)));
		
		int year = new GregorianCalendar().get(Calendar.YEAR);
		copyright.setText(getString(R.string.about_copyright, year));
	}
}
