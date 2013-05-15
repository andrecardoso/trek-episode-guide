/*******************************************************************************
 * Copyright 2013 AppGlu, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.appglu.trekepisodeguide.ui.list;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.appglu.android.task.AppGluAsyncTask;
import com.appglu.trekepisodeguide.R;
import com.appglu.trekepisodeguide.model.Episode;
import com.appglu.trekepisodeguide.model.EpisodeListOrder;
import com.appglu.trekepisodeguide.ui.AbstractTrekListFragment;
import com.appglu.trekepisodeguide.ui.detail.EpisodeDetailActivity;
import com.appglu.trekepisodeguide.ui.detail.EpisodeDetailViewPagerFragment;

import de.greenrobot.event.EventBus;

public class EpisodeListFragment extends AbstractTrekListFragment {
	
	public static final String INITIAL_EPISODE_ID_INTEGER_EXTRA = "EpisodeListFragment.INITIAL_EPISODE_ID_INTEGER_EXTRA";
	
	private static final String FIRST_VISIBLE_EPISODE_ID_EXTRA = "EpisodeListFragment.FIRST_VISIBLE_EPISODE_ID_EXTRA";
	private static final int EPISODE_DETAIL_REQUEST_CODE = 1;
	private static final int EPISODE_NOT_FOUND = -1;
	
	private SectionHeaderListAdapter listAdapter;
	
	private View scrollIndexLinearLayout;
	
	private View scrollIndexTopImageView;
	
	private View scrollIndexMiddleImageView;
	
	private View scrollIndexBottomImageView;
	
	private int firstVisibleEpisodeId;
	
	private int initialEpisodeId;
	
	private EpisodeListOrder selectedListOrder;
	
	public EpisodeListFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.episode_list_fragment, container, false);
		
		this.scrollIndexLinearLayout = rootView.findViewById(R.id.scroll_index);
		this.scrollIndexTopImageView = rootView.findViewById(R.id.scroll_index_top);
		this.scrollIndexMiddleImageView = rootView.findViewById(R.id.scroll_index_middle);
		this.scrollIndexBottomImageView = rootView.findViewById(R.id.scroll_index_bottom);
		
		this.scrollIndexLinearLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		this.scrollIndexTopImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		this.scrollIndexMiddleImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		this.scrollIndexBottomImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		if (savedInstanceState != null) {
			this.firstVisibleEpisodeId = savedInstanceState.getInt(FIRST_VISIBLE_EPISODE_ID_EXTRA, 0);
		}

        this.initAnimations(rootView);

		return rootView;
	}
	
	public void setInitialEpisodeId(int initialEpisodeId) {
		this.initialEpisodeId = initialEpisodeId;
	}

	private void initAnimations(View rootView) {
        this.startAnimationForView(rootView, R.id.home_bottom_bar);
        this.startAnimationForView(rootView, R.id.home_top_bar);
    }

    private void startAnimationForView(View rootView, int id) {
        View view = rootView.findViewById(id);
        if (view == null) {
            return;
        }
        AnimationDrawable drawable = (AnimationDrawable) view.getBackground();
        drawable.start();
    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		HeaderListAdapter headerAdapter = new HeaderListAdapter(getActivity());
		this.listAdapter = new SectionHeaderListAdapter(getActivity(), headerAdapter);
		this.getListView().setAdapter(this.listAdapter);
		
		if (this.isLargeScreen()) {
			this.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		} else {
			this.getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().registerSticky(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState == null) {
			return;
		}
		
		this.onTabUnselected();
		outState.putInt(FIRST_VISIBLE_EPISODE_ID_EXTRA, this.firstVisibleEpisodeId);
	}
	
	public void onEventMainThread(EpisodeDetailViewPagerFragment.EpisodeSelectedOnViewPagerEvent event) {
		EventBus.getDefault().removeStickyEvent(event);
		
		int episodeId = event.getEpisodeId();
		if (episodeId != 0) {
			this.firstVisibleEpisodeId = episodeId;
			this.selectFirstVisibleEpisode();
		}
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		Object object = this.listAdapter.getItem(position);
		
		if (object == null || !(object instanceof Episode)) {
			return;
		}
		
		Episode episode = (Episode) object;
		
		int episodeId = episode.getId();
		
		if (this.isLargeScreen()) {
			this.selectEpisodeForDetailFragment(episodeId);
		} else {
			Intent detailIntent = new Intent(getActivity(), EpisodeDetailActivity.class);
			detailIntent.putExtra(EpisodeDetailActivity.EPISODE_ID_INTEGER_EXTRA, episodeId);
			detailIntent.putExtra(EpisodeDetailActivity.EPISODE_LIST_ORDER_EXTRA, this.selectedListOrder);
			this.startActivityForResult(detailIntent, EPISODE_DETAIL_REQUEST_CODE);
		}
	}
	
	private void selectEpisodeForDetailFragment(int episodeId) {
		EpisodeDetailViewPagerFragment episodeDetailViewPagerFragment = this.getEpisodeDetailViewPagerFragment();
		
		if (episodeDetailViewPagerFragment == null) {
			this.addEpisodeDetailFragment(episodeId);
		} else {
			EventBus.getDefault().post(new EpisodeSelectedOnListViewEvent(episodeId));
		}
	}

	private void addEpisodeDetailFragment(int episodeId) {
		EpisodeDetailViewPagerFragment fragment = new EpisodeDetailViewPagerFragment();
		
		Bundle arguments = new Bundle();
		arguments.putInt(EpisodeDetailViewPagerFragment.EPISODE_ID_INTEGER_EXTRA, episodeId);
		arguments.putSerializable(EpisodeDetailViewPagerFragment.EPISODE_LIST_ORDER_EXTRA, this.selectedListOrder);
		fragment.setArguments(arguments);
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.episode_detail_fragment, fragment);
		fragmentTransaction.commit();
	}

	private void updateDetailFragmentForLargeScreens() {
		EpisodeDetailViewPagerFragment episodeDetailViewPagerFragment = this.getEpisodeDetailViewPagerFragment();
		
		if (episodeDetailViewPagerFragment == null) {
			if (this.initialEpisodeId != 0) {
				this.addEpisodeDetailFragment(this.initialEpisodeId);
				return;
			}
			
			List<Episode> allEpisodes = this.getTrekService().getAllEpisodes(EpisodeListOrder.TITLE);
			
			if (allEpisodes != null && !allEpisodes.isEmpty()) {
				Episode firstEpisode = allEpisodes.get(0);
				this.addEpisodeDetailFragment(firstEpisode.getId());
			}
		} else {
			EventBus.getDefault().post(new OnTabFinishedLoadingEvent(this.selectedListOrder));
		}
	}
	
	private void showLoadingView(boolean show, boolean empty) {
		if (getActivity() == null) {
			return;
		}
		
		RelativeLayout loadingLayout = (RelativeLayout) getActivity().findViewById(R.id.loading_layout);
		
		if (loadingLayout == null || this.scrollIndexLinearLayout == null) {
			return;
		}
		
		this.getListView().getEmptyView().setVisibility(View.GONE);
		
		if (show) {
			loadingLayout.setVisibility(View.VISIBLE);
			getListView().setVisibility(View.GONE);
			this.scrollIndexLinearLayout.setVisibility(View.GONE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			getListView().setVisibility(View.VISIBLE);
			this.scrollIndexLinearLayout.setVisibility(View.VISIBLE);
		}
		
		if (empty) {
			loadingLayout.setVisibility(View.GONE);
			getListView().setVisibility(View.GONE);
			this.scrollIndexLinearLayout.setVisibility(View.GONE);
			this.getListView().getEmptyView().setVisibility(View.VISIBLE);
		}
	}
	
	private void updateList(Map<String, List<Episode>> result) {
		if (getActivity() == null) {
			return;
		}
		
		this.listAdapter.clear();
		
		if (result != null) {
			for (String header : result.keySet()) {
				EpisodeListAdapter episodeAdapter = new EpisodeListAdapter(getActivity(), result.get(header), this.selectedListOrder);
				this.listAdapter.addSection(header, episodeAdapter);
			}
		}
		
		this.listAdapter.notifyDataSetChanged();
		
		//this is necessary to invalidate the previous SectionIndexer
		this.getListView().setFastScrollEnabled(false);
		this.getListView().setFastScrollEnabled(true);
		this.getListView().setFastScrollAlwaysVisible(false);
		this.getListView().setFastScrollAlwaysVisible(true);
		
		this.selectFirstVisibleEpisode();
		
		if (this.isLargeScreen()) {
			this.updateDetailFragmentForLargeScreens();
		}
	}
	
	private EpisodeDetailViewPagerFragment getEpisodeDetailViewPagerFragment() {
		return (EpisodeDetailViewPagerFragment) this.getFragmentManager().findFragmentById(R.id.episode_detail_fragment);
	}

	private void selectFirstVisibleEpisode() {
		if (this.firstVisibleEpisodeId != 0) {
			int index = this.findIndexForEpisodeId(this.firstVisibleEpisodeId);
			if (index != EPISODE_NOT_FOUND) {
				this.getListView().setItemChecked(index, true);
				this.getListView().setSelectionFromTop(index, 0);
			}
		}
	}
	
	public int findIndexForEpisodeId(int id) {
		if (this.listAdapter == null) {
			return EPISODE_NOT_FOUND;
		}
		
		for (int i = 0; i < this.listAdapter.getCount(); i++) {
			Object object = this.listAdapter.getItem(i);
			
			if (object instanceof Episode) {
				Episode episode = (Episode) object;
				if (id == episode.getId()) {
					return i;
				}
			}
		}
		return EPISODE_NOT_FOUND;
	}
	
	public void onTabSelected(EpisodeListOrder listOrder) {
		this.selectedListOrder = listOrder;
		
		this.scrollIndexTopImageView.setBackgroundResource(this.selectedListOrder.getScrollIndexTopDrawableResource());
		this.scrollIndexMiddleImageView.setBackgroundResource(this.selectedListOrder.getScrollIndexMiddleDrawableResource());
		this.scrollIndexBottomImageView.setBackgroundResource(this.selectedListOrder.getScrollIndexBottomDrawableResource());
		
		if (getTrekService().hasLoadedEpisodes()) {
			Map<String, List<Episode>> result = getTrekService().getAllEpisodesGroupedByListOrder(listOrder);
			if (result.isEmpty()) {
				showLoadingView(false, true);
			} else {
				this.updateList(result);
			}
		} else {
			LoadEpisodesAsyncTask task = new LoadEpisodesAsyncTask();
			task.execute();
		}
	}
	
	public void onTabUnselected() {
		if (this.listAdapter == null) {
			return;
		}
		
		int firstVisiblePosition = getListView().getFirstVisiblePosition();
		if (this.listAdapter.getItemViewType(firstVisiblePosition) == SectionHeaderListAdapter.TYPE_SECTION_HEADER) {
			firstVisiblePosition++;
		}
		
		Object object = listAdapter.getItem(firstVisiblePosition);
		
		if (object == null || !(object instanceof Episode)) {
			return;
		}
		
		Episode episode = (Episode) object;
		this.firstVisibleEpisodeId = episode.getId();
	}
	
	private class LoadEpisodesAsyncTask extends AppGluAsyncTask<Void, Void, Map<String, List<Episode>>> {
		
		@Override
		protected void onPreExecute() {
			showLoadingView(true, false);
		}

		@Override
		protected Map<String, List<Episode>> doExecuteInBackground(Void... params) throws Exception {
			return getTrekService().getAllEpisodesGroupedByListOrder(selectedListOrder);
		}

		@Override
		protected void onResult(Map<String, List<Episode>> result) {
			updateList(result);
			showLoadingView(false, result.isEmpty());
		}
		
		@Override
		public void onException(Exception exception) {
			logger.error(exception);
			updateList(null);
			showLoadingView(false, true);
		}
		
	}
	
	/*
	 * Posted when an episode was selected by the list view
	 */
	public class EpisodeSelectedOnListViewEvent {
		
		private int episodeId;

    	public EpisodeSelectedOnListViewEvent(int episodeId) {
    		this.episodeId = episodeId;
    	}

    	public int getEpisodeId() {
    		return episodeId;
    	};
    	
	}
	
	/*
	 * Posted when all episodes were loaded after a tab was changed
	 */
	public class OnTabFinishedLoadingEvent {
		
		private EpisodeListOrder episodeListOrder;

		public OnTabFinishedLoadingEvent(EpisodeListOrder episodeListOrder) {
			this.episodeListOrder = episodeListOrder;
		}

		public EpisodeListOrder getEpisodeListOrder() {
			return episodeListOrder;
		}
		
	}

}