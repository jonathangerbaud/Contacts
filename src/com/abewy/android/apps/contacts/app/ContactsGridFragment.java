package com.abewy.android.apps.contacts.app;

import java.util.List;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import com.abewy.android.apps.contacts.R;
import com.abewy.android.apps.contacts.adapter.MultiObjectAdapter;
import com.abewy.android.apps.contacts.model.BaseContact;
import com.abewy.android.apps.contacts.widget.ListEmptyView;
import com.haarman.listviewanimations.BaseAdapterDecorator;

public class ContactsGridFragment extends BaseFragment
{
	// Layout Views
	private GridView	mGridView;
	private View		mLoadingView;

	private boolean		mLoading		= false;
	private boolean		mFirstLoad		= true;
	private boolean		mViewDestroyed	= false;

	// Used if custom layout
	private boolean		mGridVisible	= false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Log.d("KlyphFragment2", "onCreateView");
		View view = inflater.inflate(getCustomLayout(), container, false);

		mGridView = (GridView) view.findViewById(R.id.grid);

		View emptyView = getEmptyView();

		if (emptyView != null)
		{
			emptyView.setId(android.R.id.empty);

			((ViewGroup) mGridView.getParent()).addView(emptyView);

			mGridView.setEmptyView(emptyView);
		}

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		if (mGridView == null)
			throw new IllegalStateException("KlyphFragment2 : There is no KlyphGridView with id \"grid\" defined in the layout");

		mGridView.setDrawSelectorOnTop(true);
		mGridView.setNumColumns(getNumColumn());
		mGridView.setFadingEdgeLength(0);
		mGridView.setVerticalFadingEdgeEnabled(false);

		mGridView.setVisibility(View.GONE);
		((View) mGridView.getParent()).setVisibility(View.GONE);
		mLoadingView = view.findViewById(android.R.id.progress);

		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> gridView, View view, int position, long id)
			{
				onGridItemClick((GridView) gridView, view, position, id);
			}
		});
	}

	@Override
	protected int getLayout()
	{
		return 0;
	}

	protected boolean updateNumColumnOnOrientationChange()
	{
		return true;
	}

	protected int getNumColumn()
	{
		return 3;
	}

	@Override
	public void onConfigurationChanged(Configuration myConfig)
	{
		super.onConfigurationChanged(myConfig);

		if (getGridView() != null)
		{
			int pos = getGridView().getFirstVisiblePosition();
			getGridView().setNumColumns(getNumColumn());
			getGridView().setSelection(pos);

			if (getAdapter() != null)
				getAdapter().notifyDataSetChanged();
		}
	}

	/**
	 * Alias for getGridView()
	 * 
	 * @see getGridView()
	 */
	public GridView getListView()
	{
		return getGridView();
	}

	protected GridView getGridView()
	{
		if (mGridView == null && getView() != null)
			mGridView = (GridView) getView().findViewById(R.id.grid);

		return mGridView;
	}

	protected ListAdapter getListAdadapter()
	{
		return getGridAdapter();
	}

	protected ListAdapter getGridAdapter()
	{
		if (getGridView() != null)
			return getGridView().getAdapter();

		return null;
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
		return R.layout.grid;
	}

	protected void setGridVisible(boolean visible)
	{
		setGridVisibility(visible, true);
	}

	/**
	 * Alias for setGridVisible()
	 * 
	 * @see setGridVisible()
	 */
	protected void setListVisible(boolean visible)
	{
		setGridVisible(visible);
	}

	private void setGridVisibility(boolean visible, boolean animate)
	{
		ensureList();

		if (mGridVisible == visible)
		{
			return;
		}

		mGridVisible = visible;

		View parent = (View) getGridView().getParent();

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

			if (parent != null)
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

			if (parent != null)
				parent.setVisibility(View.GONE);
		}
	}

	protected void ensureList()
	{
		// getGridView().setEmptyView(getView().findViewById(android.R.id.empty));
	}

	protected void setEmptyText(int resId)
	{
		if (getGridView().getEmptyView() != null)
		{
			((ListEmptyView) getGridView().getEmptyView()).setText(resId);
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

	protected void defineEmptyText(int resId)
	{
		setEmptyText(resId);
	}

	protected MultiObjectAdapter getAdapter()
	{
		if (getGridView() != null && getGridView().getAdapter() != null)
		{
			if (getGridView().getAdapter() instanceof BaseAdapterDecorator)
			{
				return (MultiObjectAdapter) ((BaseAdapterDecorator) getGridView().getAdapter()).getDecoratedBaseAdapter();
			}
			else
			{
				return (MultiObjectAdapter) getGridView().getAdapter();
			}
		}

		return null;
	}

	protected void setGridAdapter(ListAdapter adapter)
	{
		if (getGridView() != null)
			getGridView().setAdapter(adapter);
	}

	protected void setListAdapter(ListAdapter adapter)
	{
		setGridAdapter(adapter);
	}

	protected void onGridItemClick(GridView gridView, View view, int position, long id)
	{

	}

	protected void populate(List<BaseContact> data)
	{
		if (getView() != null && getAdapter() != null)
		{
			MultiObjectAdapter adapter = getAdapter();

			for (BaseContact graphObject : data)
			{
				adapter.add(graphObject);
			}

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
		// Log.i(TAG, "onDestroyView");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		// if (getAdapter() != null)
		// getAdapter().setData(new ArrayList<GraphObject>());

		mLoadingView = null;
		// gridView = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		if (getAdapter() != null)
		{
			getAdapter().notifyDataSetChanged();
		}
		// Log.d(TAG, "onResume");
		/*
		 * if (!isLoading() && isFirstLoad() && getShouldLoadOnResume()) load();
		 */
	}

}