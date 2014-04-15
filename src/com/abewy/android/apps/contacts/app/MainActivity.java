package com.abewy.android.apps.contacts.app;

import java.util.List;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem.OnActionExpandListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.adapter.LayoutType;
import com.abewy.android.apps.contacts.adapter.MultiObjectAdapter;
import com.abewy.android.apps.contacts.core.CoreApplication;
import com.abewy.android.apps.contacts.core.CorePrefs;
import com.abewy.android.apps.contacts.iab.IabHelper;
import com.abewy.android.apps.contacts.iab.IabResult;
import com.abewy.android.apps.contacts.iab.Inventory;
import com.abewy.android.apps.contacts.iab.Purchase;
import com.abewy.android.extended.items.BaseType;
import com.crashlytics.android.Crashlytics;
import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, IActionbarSpinner
{
	private static final int		SETTINGS_CODE		= 125;

	private SectionsPagerAdapter	mSectionsPagerAdapter;
	private JazzyViewPager			mViewPager;
	private SearchView				mSearchView;
	private TransitionEffect		mCurrentEffect;
	private int						mCurrentFragmentIndex;
	private OnQueryTextListener		mQueryTextListener	= new OnQueryTextListener() {

															@Override
															public boolean onQueryTextSubmit(String query)
															{
																return false;
															}

															@Override
															public boolean onQueryTextChange(String newText)
															{
																mSectionsPagerAdapter.setSearchQuery(newText);
																return true;
															}
														};
	private OnActionExpandListener	mExpandListener		= new OnActionExpandListener() {

															@Override
															public boolean onMenuItemActionExpand(MenuItem item)
															{
																mViewPager.setPagingEnabled(false);
																mSearchView.setOnQueryTextListener(mQueryTextListener);
																return true;
															}

															@Override
															public boolean onMenuItemActionCollapse(MenuItem item)
															{
																Log.d("MainActivity", "onMenuItemActionCollapse: ");
																mViewPager.setPagingEnabled(true);
																mSearchView.setOnQueryTextListener(null);
																mSectionsPagerAdapter.setSearchQuery(null);
																return true;
															}
														};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(null);
		Crashlytics.start(this);
		setContentView(R.layout.activity_main);

		String base64EncodedPublicKey = CoreApplication.generateIabKey();

		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set this to false).
		// mHelper.enableDebugLogging(true);

		mViewPager = (JazzyViewPager) findViewById(R.id.pager);

		// CorePrefs.setHasDonated(false);

		setupViewPager();

		if (CorePrefs.isFirstLaunch())
		{
			ViewStub showcaseStub = (ViewStub) findViewById(R.id.showcase_stub);
			showcaseStub.setLayoutResource(R.layout.activity_main_showcase);
			View showcase = showcaseStub.inflate();
			
			showcase.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					return true;
				}
			});

			final Button button = (Button) showcase.findViewById(R.id.showcase_button);
			button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v)
				{
					CorePrefs.setFirstLaunchDone();
					((ViewGroup) button.getParent().getParent()).removeView(((View) button.getParent()));
					mViewPager.setPagingEnabled(true);
					invalidateOptionsMenu();
				}
			});
		}

		if (!CorePrefs.hasDonated())
		{
			launchIab();
		}
	}

	private void setupViewPager()
	{
		mCurrentEffect = CorePrefs.getViewPagerEffect();
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager.setTransitionEffect(mCurrentEffect);
		mViewPager.setFadeEnabled(false);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setPageMargin(30);
		mViewPager.setCurrentItem(mCurrentFragmentIndex);
		mViewPager.setOnPageChangeListener(mSectionsPagerAdapter);
		mViewPager.setPagingEnabled(!CorePrefs.isFirstLaunch());

		// mSectionsPagerAdapter.setData(contacts, favContacts);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == SETTINGS_CODE)
		{
			if (CorePrefs.getPrefsHaveChanged())
			{
				CorePrefs.setPrefsHaveChanged(false);
				restart();
			}
		}

		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data))
		{
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		}
		else
		{
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}

	private void restart()
	{
		Intent localIntent = new Intent(this, MainActivity.class);
		localIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(localIntent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if (!CorePrefs.isFirstLaunch())
		{
			getMenuInflater().inflate(R.menu.main, menu);

			MenuItem item = menu.findItem(R.id.action_search);
			mSearchView = (SearchView) item.getActionView();

			item.setOnActionExpandListener(mExpandListener);

			if (!CorePrefs.hasDonated())
			{
				menu.add(Menu.NONE, R.id.action_help_me, 99, R.string.action_help_me);
			}
		}

		return true;
	}

	// ___ Tabs management

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.action_add_contact)
		{
			// Creates a new Intent to insert a contact
			Intent intent = new Intent(Intents.Insert.ACTION);
			// Sets the MIME type to match the Contacts Provider
			intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
			intent.putExtra("finishActivityOnSaveCompleted", true);
			startActivity(intent);
			return true;
		}

		if (item.getItemId() == R.id.action_help_me)
		{
			startActivity(new Intent(this, HelpMeActivity.class));
			return true;
		}

		if (item.getItemId() == R.id.action_settings)
		{
			mCurrentFragmentIndex = mViewPager.getCurrentItem();
			startActivityForResult(new Intent(this, PreferencesActivity.class), SETTINGS_CODE);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft)
	{

	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft)
	{
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft)
	{

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (mHelper != null)
		{
			mHelper.dispose();
			mHelper = null;
		}

		if (mSectionsPagerAdapter != null)
			mSectionsPagerAdapter.onDestroy();

		if (mViewPager != null)
		{
			mViewPager.setOnPageChangeListener(null);
		}

		mSectionsPagerAdapter = null;
		mViewPager = null;
		mSearchView = null;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener
	{
		private FragmentContainer	peopleFragment;
		private FragmentContainer	favoritesFragment;

		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			if (position == 0)
			{
				peopleFragment = new FragmentContainer();

				return peopleFragment;
			}
			else
			{
				favoritesFragment = new FavoritesFragmentContainer();

				return favoritesFragment;
			}
		}

		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}

		@Override
		public int getCount()
		{
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			/*Locale l = Locale.getDefault();

			switch (position)
			{
				case 0:
				{
					return getString(R.string.title_section1).toUpperCase(l);
				}
				case 1:
				{
					return getString(R.string.title_section2).toUpperCase(l);
				}
			}*/

			return null;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position)
		{
			Object obj = super.instantiateItem(container, position);
			mViewPager.setObjectForPosition(obj, position);
			return obj;
		}

		public void setSearchQuery(String query)
		{
			if (mViewPager.getCurrentItem() == 0)
				peopleFragment.searchQuery(query);
			else
				favoritesFragment.searchQuery(query);
		}

		public void destroyItem(ViewGroup container, int position, Object obj)
		{
			container.removeView(mViewPager.findViewFromObject(position));
		}

		public void onDestroy()
		{
			peopleFragment = null;
			favoritesFragment = null;
		}

		@Override
		public void onPageSelected(int position)
		{
			if (peopleFragment == null)
				return;

			if (position == 0)
			{
				peopleFragment.onSetToFront();
				favoritesFragment.onSetToBack();
			}
			else
			{
				favoritesFragment.onSetToFront();
				peopleFragment.onSetToBack();
			}
		}

		@Override
		public void onPageScrollStateChanged(int state)
		{

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{

		}
	}

	// ___ InApp Billing
	private IabHelper							mHelper;

	private static final String					TAG						= "MainActivity";

	// Listener that's called when we finish querying the items and subscriptions we own
	IabHelper.QueryInventoryFinishedListener	mGotInventoryListener	= new IabHelper.QueryInventoryFinishedListener() {
																			public void onQueryInventoryFinished(IabResult result, Inventory inventory)
																			{
																				Log.d(TAG, "Query inventory finished.");
																				// MessengerApplication.PRO_VERSION_CHECKED = true;
																				// Have we been disposed of in the meantime? If so, quit.
																				if (mHelper == null)
																					return;

																				// Is it a failure?
																				if (result.isFailure())
																				{
																					Log.d(TAG, "Failed to query inventory: " + result);
																					// Fail to check, so we don't display ads
																					// to avoid pro users to see ads
																					// MessengerApplication.IS_PRO_VERSION = true;
																					return;
																				}

																				Log.d(TAG, "Query inventory was successful.");

																				/*
																				 * Check for items we own. Notice that for each purchase, we check
																				 * the developer payload to see if it's correct! See
																				 * verifyDeveloperPayload().
																				 */

																				String[] skus = getResources().getStringArray(R.array.donate_values);

																				for (String sku : skus)
																				{
																					Purchase donation = inventory.getPurchase(sku);

																					if (donation != null)
																					{
																						// mHelper.consumeAsync(donation, null);
																						CorePrefs.setHasDonated(true);
																						break;
																					}
																				}

																				if (CorePrefs.hasDonated())
																					invalidateOptionsMenu();

																				Log.d(TAG, "Initial inventory query finished; enabling main UI.");
																			}
																		};

	private void launchIab()
	{
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result)
			{
				Log.d(TAG, "Setup finished.");

				if (!result.isSuccess())
				{
					// Oh noes, there was a problem.
					Log.d("MainActivity.onCreate(...).new OnIabSetupFinishedListener() {...}",
							"onIabSetupFinished: Problem setting up in-app billing: " + result);
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				// IAB is fully set up. Now, let's get an inventory of stuff we own.
				Log.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});

	}

	@Override
	public void displaySpinnerInActionBar(String[] array, int position, OnNavigationListener listener)
	{
		ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(getActionBar().getThemedContext(),
				android.R.layout.simple_dropdown_item_1line, array);
		list.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActionBar().setListNavigationCallbacks(list, listener);
		getActionBar().setSelectedNavigationItem(position);
	}

	@Override
	public void displaySpinnerInActionBar(int array, int position, OnNavigationListener listener)
	{
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(getActionBar().getThemedContext(), array,
				android.R.layout.simple_dropdown_item_1line);
		list.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActionBar().setListNavigationCallbacks(list, listener);
		getActionBar().setSelectedNavigationItem(position);
	}

	@Override
	public void displaySpinnerInActionBar(List<BaseType> data, int position, OnNavigationListener listener)
	{
		MultiObjectAdapter adapter = new MultiObjectAdapter(null, LayoutType.DROP_DOWN_ITEM);
		adapter.addAll(data);

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActionBar().setListNavigationCallbacks(adapter, listener);
		getActionBar().setSelectedNavigationItem(position);
	}

	@Override
	public void removeSpinnerInActionBar()
	{
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActionBar().setListNavigationCallbacks(null, null);
	}
}
