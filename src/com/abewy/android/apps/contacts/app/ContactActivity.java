package com.abewy.android.apps.contacts.app;

import org.apache.commons.lang3.ArrayUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.app.ContactDetailFragment.OnContactDeletedListener;
import com.abewy.android.apps.contacts.core.CoreIntentCodes;
import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;

public class ContactActivity extends FragmentActivity implements OnContactDeletedListener
{
	private SectionsPagerAdapter	mSectionsPagerAdapter;
	private JazzyViewPager			mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_background_transparent_gradient));
		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		String lookupKey = getIntent().getStringExtra("lookupKey");
		String[] lookupKeys = getIntent().getStringArrayExtra("lookupKeys");
		String name = getIntent().getStringExtra("name");

		setTitle(name);

		int index = ArrayUtils.indexOf(lookupKeys, lookupKey);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), lookupKeys);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (JazzyViewPager) findViewById(R.id.pager);
		mViewPager.setOnPageChangeListener(mSectionsPagerAdapter);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(index);

		mViewPager.setTransitionEffect(TransitionEffect.FlipHorizontal);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements OnPageChangeListener
	{
		private Fragment[]		fragments;
		private final String[]	lookupKeys;

		public SectionsPagerAdapter(FragmentManager fm, String[] idList)
		{
			super(fm);
			this.lookupKeys = idList;
			fragments = new Fragment[idList.length];
		}

		@Override
		public Fragment getItem(int position)
		{
			ContactDetailFragment fragment = new ContactDetailFragment();

			Bundle args = new Bundle();
			args.putString("lookupKey", lookupKeys[position]);
			fragment.setArguments(args);

			fragments[position] = fragment;

			return fragment;
		}

		private Fragment getItemAt(int position)
		{
			return fragments[position];
		}

		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_UNCHANGED;
		}

		@Override
		public int getCount()
		{
			return lookupKeys.length;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return null;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position)
		{
			Object obj = super.instantiateItem(container, position);
			mViewPager.setObjectForPosition(obj, position);
			return obj;
		}

		@Override
		public void onPageScrollStateChanged(int state)
		{

		}

		@Override
		public void onPageScrolled(int position, float arg1, int arg2)
		{

		}

		@Override
		public void onPageSelected(int position)
		{
			ContactDetailFragment fragment = (ContactDetailFragment) getItemAt(position);

			if (fragment != null)
				fragment.setActive();
		}
	}

	@Override
	public void onContactDeleted(String lookupKey)
	{
		Log.d("ContactActivity", "onContactDeleted: ");
		Intent data = new Intent();
		data.putExtra("lookupKey", lookupKey);
		setResult(CoreIntentCodes.RESULT_CONTACT_DELETED, data);
		Log.d("ContactActivity", "onContactDeleted: finish before");
		finish();
		Log.d("ContactActivity", "onContactDeleted: finish after");
	}
}