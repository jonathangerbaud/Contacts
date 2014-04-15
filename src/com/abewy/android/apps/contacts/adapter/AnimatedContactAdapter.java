package com.abewy.android.apps.contacts.adapter;

import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;

public class AnimatedContactAdapter extends ScaleInAnimationAdapter implements SectionIndexer, PinnedSectionListAdapter
{
	public AnimatedContactAdapter(BaseAdapter baseAdapter)
	{
		super(baseAdapter);
	}

	public AnimatedContactAdapter(BaseAdapter baseAdapter, float scaleFrom)
	{
		super(baseAdapter, scaleFrom);
	}

	public AnimatedContactAdapter(BaseAdapter baseAdapter, float scaleFrom, long animationDelayMillis, long animationDurationMillis)
	{
		super(baseAdapter, scaleFrom, animationDelayMillis, animationDurationMillis);
	}

	@Override
	public boolean isItemViewTypePinned(int viewType)
	{
		return ((PinnedSectionListAdapter) getDecoratedBaseAdapter()).isItemViewTypePinned(viewType);
	}

	@Override
	public int getPositionForSection(int section)
	{
		return ((SectionIndexer) getDecoratedBaseAdapter()).getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position)
	{
		return ((SectionIndexer) getDecoratedBaseAdapter()).getSectionForPosition(position);
	}

	@Override
	public Object[] getSections()
	{
		return ((SectionIndexer) getDecoratedBaseAdapter()).getSections();
	}
}
