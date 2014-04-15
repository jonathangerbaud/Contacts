/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.core.CoreApplication;
import com.abewy.android.apps.contacts.core.CorePrefs;
import com.abewy.android.apps.contacts.iab.IabHelper;
import com.abewy.android.apps.contacts.iab.IabResult;
import com.abewy.android.apps.contacts.iab.Purchase;
import com.abewy.android.extended.util.AlertUtil;
import com.abewy.android.extended.util.PlayStoreUtil;

public class HelpMeFragment extends BaseFragment
{
	private static final String	TAG	= "HelpMeFragment";
	private Spinner				mSpinner;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		final String base64EncodedPublicKey = CoreApplication.generateIabKey();

		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set this to false).
		mHelper.enableDebugLogging(true);

		mSpinner = (Spinner) view.findViewById(R.id.spinner);

		final View seeMoreButton = view.findViewById(R.id.see_more_button);
		seeMoreButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v)
			{
				openPlayStore();
			}
		});

		final View klyphButton = view.findViewById(R.id.klyph_button);
		klyphButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v)
			{
				openKlyph();
			}
		});

		final View klyphMessengerButton = view.findViewById(R.id.klyph_messenger_button);
		klyphMessengerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v)
			{
				openKlyphMessenger();
			}
		});

		final View nextAppButton = view.findViewById(R.id.next_app_button);
		nextAppButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v)
			{
				// openPlayStore();
			}
		});

		final ImageButton donateButton = (ImageButton) view.findViewById(R.id.donate_button);
		donateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v)
			{
				launchPurchaseFlow();
			}
		});

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
				// mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}

	@Override
	protected int getLayout()
	{
		return R.layout.fragment_help_me;
	}

	private void openPlayStore()
	{
		PlayStoreUtil.openMyApps(getActivity());
	}

	private void openKlyph()
	{
		PlayStoreUtil.openApp(getActivity(), "com.abewy.klyph_beta");
	}

	private void openKlyphMessenger()
	{
		PlayStoreUtil.openApp(getActivity(), "com.abewy.android.apps.klyph.messenger");
	}

	private void launchPurchaseFlow()
	{
		final String[] skus = getActivity().getResources().getStringArray(R.array.donate_values);

		final String sku = skus[mSpinner.getSelectedItemPosition()];
		mHelper.launchPurchaseFlow(getActivity(), sku, IabHelper.ITEM_TYPE_INAPP, RC_REQUEST, mPurchaseFinishedListener, null);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
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

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		if (mHelper != null)
		{
			mHelper.dispose();
			mHelper = null;
		}
	}

	// ___ InApp Billing
	private IabHelper						mHelper;

	// (arbitrary) request code for the purchase flow
	private static final int				RC_REQUEST					= 10001;

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener	mPurchaseFinishedListener	= new IabHelper.OnIabPurchaseFinishedListener() {
																			public void onIabPurchaseFinished(IabResult result, Purchase purchase)
																			{
																				Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

																				// if we were disposed of in the meantime, quit.
																				if (mHelper == null)
																					return;

																				if (result.isFailure())
																				{
																					Log.d(TAG, "Error purchasing: " + result);

																					if (result.getResponse() == 7)
																					{
																						Log.d("HelpMeFragment", "onIabPurchaseFinished: " + purchase);
																						Log.d("HelpMeFragment",
																								"onIabPurchaseFinished: " + purchase.getItemType()
																										+ " " + IabHelper.ITEM_TYPE_INAPP);
																						displayDonateSameAmountAgain(purchase);
																					}

																					return;
																				}

																				CorePrefs.setHasDonated(true);
																				Log.d(TAG, "Purchase successful.");

																				if (getActivity() != null)
																				{
																					AlertUtil.showAlert(getActivity(), R.string.thank_you,
																							R.string.thank_you_purchase, R.string.ok);
																				}

																			}
																		};

	private void displayDonateSameAmountAgain(final Purchase purchase)
	{
		AlertUtil.showAlert(getActivity(), R.string.action_help_me, R.string.donate_again, R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				mHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {

					@Override
					public void onConsumeFinished(Purchase purchase, IabResult result)
					{
						launchPurchaseFlow();
					}
				});
			}
		}, R.string.no, null);
	}
}
