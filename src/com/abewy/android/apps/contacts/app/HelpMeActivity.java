/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import com.abewy.android.apps.contacts.R;

public class HelpMeActivity extends FragmentActivity
{
	private HelpMeFragment mFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_me);
		
		setTitle(R.string.action_help_me);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mFragment = (HelpMeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d("HelpMeActivity", "onActivityResult: ");
		super.onActivityResult(requestCode, resultCode, data);
		
		if (mFragment != null)
			mFragment.onActivityResult(requestCode, resultCode, data);
	}
	
	
}
