/**
 * @author Jonathan
 */

package com.abewy.android.apps.contacts.adapter;

import java.util.List;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.SectionIndexer;
import com.abewy.android.extended.items.BaseType;
import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;

public class ContactListAdapter extends MultiObjectAdapter implements SectionIndexer, PinnedSectionListAdapter
{
	private static String	sections	= "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";
	private static String[]	sectionsArr;
	private int[]			sectionMap;
	private int[]			positionMap;

	public ContactListAdapter(AbsListView listView)
	{
		super(listView);
		sectionMap = new int[sections.length()];
		positionMap = new int[1];
	}

	public ContactListAdapter(AbsListView listView, int layoutType)
	{
		super(listView, layoutType);
		sectionMap = new int[sections.length()];
		positionMap = new int[1];
	}

	@Override
	public void setData(List<BaseType> data)
	{
		regenerateSectionMap(data);
		super.setData(data);
	}

	@Override
	public int getPositionForSection(int section)
	{
		return sectionMap[section];
	}

	@Override
	public int getSectionForPosition(int position)
	{
		return positionMap[position];
	}

	@Override
	public Object[] getSections()
	{
		if (sectionsArr == null)
		{
			sectionsArr = new String[sections.length()];

			for (int i = 0; i < sections.length(); i++)
			{
				sectionsArr[i] = "" + sections.charAt(i);
			}
		}
		return sectionsArr;
	}

	/**
	 * Generate the section map
	 * Prevents from calculating the section position
	 * Every time we call getPostionForSection()
	 * e.g everytime we are scrolling
	 */
	private void regenerateSectionMap(List<BaseType> data)
	{
		sectionMap = new int[sections.length()];
		positionMap = new int[data.size()];

		int n = sections.length();

		// First, set all indexes to -1 meaning not found
		for (int i = 0; i < n; i++)
		{
			sectionMap[i] = -1;
		}

		if (data.size() > 0)
		{
			// Next, we define the indexes for each letter
			int k = 0;

			for (int i = 0; i < n; i++)
			{
				char sectionLetter = sections.charAt(i);

				for (int j = k, m = data.size(); j < m; j++)
				{
					char firstLetter = data.get(j).getItemPrimaryLabel().charAt(0);
					if (firstLetter == sectionLetter)
					{
						sectionMap[i] = j;
						k = j;
						break;
					}
				}

				// Some previous letters may have no index
				// So we assign them this value
				// Exemple 15 18 -1 -1 32
				// => 15 18 32 32 32
				if (sectionMap[i] != -1)
				{
					for (int j = i - 1; j >= 0; j--)
					{
						if (sectionMap[j] == -1)
						{
							sectionMap[j] = sectionMap[j + 1];
						}
						else
						{
							break;
						}
					}
				}
			}

			// Lastly, if the last letters have not indexes,
			// We set the index to the last item
			for (int i = n - 1; i >= 0; i--)
			{
				if (sectionMap[i] == -1)
				{
					sectionMap[i] = data.size() - 1;
				}
				else
				{
					break;
				}
			}

			for (int i = 0; i < n; i++)
			{
				positionMap[sectionMap[i]] = i;
			}

			// Fill the gaps
			if (positionMap.length > 0)
			{
				int current = positionMap[0];
				for (int i = 1, m = data.size(); i < m; i++)
				{
					if (positionMap[i] == 0)
					{
						positionMap[i] = current;
					}
					else
					{
						current = positionMap[i];
					}
				}
			}
		}
	}

	@Override
	public boolean isItemViewTypePinned(int viewType)
	{
		return viewType == 0;
	}
}