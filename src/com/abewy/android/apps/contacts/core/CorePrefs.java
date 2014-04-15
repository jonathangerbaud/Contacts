package com.abewy.android.apps.contacts.core;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;

public class CorePrefs
{
	public static final String	ROUNDED_PICTURES		= "contacts_rounded_pictures";
	public static final String	PEOPLE_VIEW_TYPE		= "people_view_type";
	public static final String	FAVORITES_VIEW_TYPE		= "favorites_view_type";
	public static final String	VIEW_PAGER_EFFECT		= "contacts_effects";
	public static final String	SORT_BY_LAST_NAME		= "contacts_sort_by_last_name";
	public static final String	LIST_GRID_ANMIATION		= "contacts_list_grid_animation";
	public static final String	HAS_DONATED				= "contacts_has_donated";
	public static final String	FIRST_LAUNCH			= "contacts_first_launch";

	public static final int		VIEW_TYPE_LIST			= 0;
	public static final int		VIEW_TYPE_BIG_GRID		= 1;
	public static final int		VIEW_TYPE_MEDIUM_GRID	= 2;
	public static final int		VIEW_TYPE_SMALL_GRID	= 3;

	private static boolean		prefsHaveChanged;

	static SharedPreferences getPreferences()
	{
		return PreferenceManager.getDefaultSharedPreferences(CoreApplication.getInstance());
	}

	public static boolean isRoundedPictures()
	{
		return getPreferences().getBoolean(ROUNDED_PICTURES, true);
	}

	public static boolean isSortingByLastName()
	{
		return getPreferences().getBoolean(SORT_BY_LAST_NAME, false);
	}

	public static boolean isAnimatingListGridItems()
	{
		return getPreferences().getBoolean(LIST_GRID_ANMIATION, false);
	}

	public static void setPeopleViewType(int viewType)
	{
		Editor editor = getPreferences().edit();
		editor.putInt(PEOPLE_VIEW_TYPE, viewType);
		editor.commit();
	}

	public static int getPeopleViewType()
	{
		return getPreferences().getInt(PEOPLE_VIEW_TYPE, 0);
	}

	public static void setFavoritesViewType(int viewType)
	{
		Editor editor = getPreferences().edit();
		editor.putInt(FAVORITES_VIEW_TYPE, viewType);
		editor.commit();
	}

	public static int getFavoritesViewType()
	{
		return getPreferences().getInt(FAVORITES_VIEW_TYPE, 0);
	}

	public static JazzyViewPager.TransitionEffect getViewPagerEffect()
	{
		int effect = Integer.parseInt(getPreferences().getString(VIEW_PAGER_EFFECT, "4"));

		switch (effect)
		{
			case 0:
				return TransitionEffect.Standard;
			case 1:
				return TransitionEffect.Tablet;
			case 2:
				return TransitionEffect.CubeIn;
			case 3:
				return TransitionEffect.CubeOut;
			case 4:
				return TransitionEffect.FlipHorizontal;
			case 5:
				return TransitionEffect.FlipVertical;
			case 6:
				return TransitionEffect.Stack;
			case 7:
				return TransitionEffect.ZoomIn;
			case 8:
				return TransitionEffect.ZoomOut;
			case 9:
				return TransitionEffect.RotateUp;
			case 10:
				return TransitionEffect.RotateDown;
			case 11:
				return TransitionEffect.Accordion;
			default:
				return TransitionEffect.FlipHorizontal;
		}
	}

	public static void setPrefsHaveChanged(boolean changed)
	{
		prefsHaveChanged = changed;
	}

	public static boolean getPrefsHaveChanged()
	{
		return prefsHaveChanged;
	}

	public static void setHasDonated(boolean donated)
	{
		Editor editor = getPreferences().edit();
		editor.putBoolean(HAS_DONATED, donated);
		editor.commit();
	}

	public static boolean hasDonated()
	{
		return getPreferences().getBoolean(HAS_DONATED, false);
	}
	
	public static boolean isFirstLaunch()
	{
		return getPreferences().getBoolean(FIRST_LAUNCH, true);
	}
	
	public static void setFirstLaunchDone()
	{
		Editor editor = getPreferences().edit();
		editor.putBoolean(FIRST_LAUNCH, false);
		editor.commit();
	}
}