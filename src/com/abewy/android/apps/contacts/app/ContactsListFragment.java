package com.abewy.android.apps.contacts.app;

import java.util.List;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.R.layout;
import com.abewy.android.apps.contacts.adapter.MultiObjectAdapter;
import com.abewy.android.apps.contacts.model.BaseContact;
import com.abewy.android.apps.contacts.widget.ListEmptyView;
import com.haarman.listviewanimations.BaseAdapterDecorator;

public class ContactsListFragment extends BaseListFragment
{
	private final String	TAG				= "KlyphFragment " + this.getClass().getSimpleName() + " " + this;

	// Layout Views
	private View			mLoadingView;
	private ListView		mListView;

	// Flags
	private boolean			mLoading		= false;
	private boolean			mFirstLoad		= true;
	private boolean			mViewDestroyed	= false;

	// Used if custom layout
	private boolean			mListVisible	= false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Log.d("KlyphFragment", "onCreateView");
		View view;
		if (!hasCustomLayout())
		{
			view = super.onCreateView(inflater, container, savedInstanceState);
		}
		else
		{
			view = inflater.inflate(getCustomLayout(), container, false);
		}

		mListView = (ListView) view.findViewById(android.R.id.list);
		
		View emptyView = getEmptyView();

		if (emptyView != null)
		{
			emptyView.setId(android.R.id.empty);

			((ViewGroup) mListView.getParent()).addView(emptyView);

			mListView.setEmptyView(emptyView);
		}

		// Now set the ScrollView as the refreshable view, and the refresh listener (this)
		// if (requestNewestType != Query.NONE)
		// attachViewToPullToRefresh(view);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		getListView().setDrawSelectorOnTop(true);

		if (hasCustomLayout())
		{
			getListView().setVisibility(View.GONE);
			((View) getListView().getParent()).setVisibility(View.GONE);
			mLoadingView = view.findViewById(android.R.id.progress);
		}
	}

	@Override
	public ListView getListView()
	{
		return mListView;
	}

	/**
	 * Create, add and set the list empty view Override this method if you want
	 * a custom empty view, or if you replaced listview by a gridview for
	 * example
	 */
	protected View getEmptyView()
	{
		return new ListEmptyView(getActivity());
	}

	/**
	 * Override this method to define this activity's layout. Default layout is
	 * the default ListFragment layout : a list, an empty TextView, a
	 * ProgressBar
	 * 
	 * @return the activity's layout. Example : <code>R.layout.main</code>
	 */
	protected int getCustomLayout()
	{
		return R.layout.list;
	}

	private boolean hasCustomLayout()
	{
		return getCustomLayout() != -1;
	}

	@Override
	protected void setListVisible(boolean visible)
	{
		if (!hasCustomLayout())
		{
			super.setListVisible(visible);
		}
		else
		{
			setListVisibility(visible, true);
		}
	}

	private void setListVisibility(boolean visible, boolean animate)
	{
		ensureList();
		
		animate = false;

		if (mListVisible == visible)
		{
			return;
		}

		mListVisible = visible;

		View parent = (View) getListView().getParent();

		if (visible)
		{
			if (animate)
			{
				mLoadingView.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
				parent.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
			}
			else
			{
				mLoadingView.clearAnimation();
				parent.clearAnimation();
			}
			mLoadingView.setVisibility(View.GONE);
			parent.setVisibility(View.VISIBLE);
		}
		else
		{
			if (animate)
			{
				mLoadingView.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
				parent.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
			}
			else
			{
				mLoadingView.clearAnimation();
				parent.clearAnimation();
			}
			mLoadingView.setVisibility(View.VISIBLE);
			parent.setVisibility(View.GONE);
		}
	}

	protected void ensureList()
	{
		// getListView().setEmptyView(getView().findViewById(android.R.id.empty));
	}

	@Override
	protected void setEmptyText(int resId)
	{
		if (getListView().getEmptyView() != null)
		{
			((ListEmptyView) getListView().getEmptyView()).setText(resId);
		}
	}

	private void setViewDestroyed(boolean viewDestroyed)
	{
		this.mViewDestroyed = viewDestroyed;
	}

	protected boolean isViewDestroyed()
	{
		return mViewDestroyed;
	}

	protected boolean isLoading()
	{
		return mLoading;
	}

	protected boolean isFirstLoad()
	{
		return mFirstLoad;
	}

	protected void setIsFirstLoad(boolean isFirstLoad)
	{
		this.mFirstLoad = isFirstLoad;
	}

	protected void refresh()
	{
		startLoading();
	}

	protected MultiObjectAdapter getAdapter()
	{
		if (getListAdapter() != null)
		{
			if (getListAdapter() instanceof BaseAdapterDecorator)
			{
				return (MultiObjectAdapter) ((BaseAdapterDecorator) getListAdapter()).getDecoratedBaseAdapter();
			}
		}
		return (MultiObjectAdapter) getListAdapter();
	}

	protected void populate(List<BaseContact> data)
	{
		if (getView() != null)
		{
			MultiObjectAdapter adapter = getAdapter();

			for (BaseContact graphObject : data)
			{
				adapter.add(graphObject);
			}
			// adapter.addAll(data); is only available in api 11

			endLoading();
		}
	}

	protected void startLoading()
	{
		mLoading = true;
	}

	protected void endLoading()
	{
		mLoading = false;
		mFirstLoad = false;

		getAdapter().notifyDataSetChanged();

		setListVisible(true);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		setViewDestroyed(true);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		mLoadingView = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		if (getAdapter() != null)
		{
			getAdapter().notifyDataSetChanged();
		}
	}
}
