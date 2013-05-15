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
package com.appglu.trekepisodeguide.ui.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appglu.AsyncCallback;
import com.appglu.android.AppGlu;
import com.appglu.android.task.AppGluAsyncTask;
import com.appglu.trekepisodeguide.R;
import com.appglu.trekepisodeguide.model.AnalyticsEventLogger;
import com.appglu.trekepisodeguide.model.Episode;
import com.appglu.trekepisodeguide.model.EpisodeListOrder;
import com.appglu.trekepisodeguide.ui.AbstractTrekFragment;
import com.appglu.trekepisodeguide.ui.list.EpisodeListFragment;

import de.greenrobot.event.EventBus;

public class EpisodeDetailViewPagerFragment extends AbstractTrekFragment {
	
	public static final String LIKE_PREFERENCES_KEY = "EpisodeDetailViewPagerFragment.LIKE_PREFERENCES_KEY";
	
	public static final String EPISODE_ID_INTEGER_EXTRA = "EpisodeDetailViewPagerFragment.EPISODE_ID_INTEGER_EXTRA";
    public static final String EPISODE_LIST_ORDER_EXTRA = "EpisodeDetailViewPagerFragment.EPISODE_LIST_ORDER_EXTRA";
    public static final String LAYOUT_OFFSET_INTEGER_EXTRA = "EpisodeDetailViewPagerFragment.LAYOUT_OFFSET_INTEGER_EXTRA";

    private List<Episode> episodes;
    private ViewPager episodeDetailsViewPager;
    private EpisodeDetailsPagerAdapter episodeDetailsPagerAdapter;

    private int selectedEpisodeId;
    private EpisodeListOrder selectedListOrder;
    
    private TextView likeButton;
    private TextView episodeTitle;
    
    private View loadingLayout;
    private View emptyView;
    
    private SharedPreferences sharedPreferences;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
        	this.selectedEpisodeId = getArguments().getInt(EPISODE_ID_INTEGER_EXTRA, 0);
        	this.selectedListOrder = (EpisodeListOrder) getArguments().getSerializable(EPISODE_LIST_ORDER_EXTRA);
        } else {
        	this.selectedEpisodeId = savedInstanceState.getInt(EPISODE_ID_INTEGER_EXTRA, 0);
        	this.selectedListOrder = (EpisodeListOrder) savedInstanceState.getSerializable(EPISODE_LIST_ORDER_EXTRA);
        }
        
        this.episodes = new ArrayList<Episode>();
    }
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	
    	this.sharedPreferences = activity.getSharedPreferences(LIKE_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.episode_detail_view_pager_fragment, container, false);

    	this.initActionBarItems();
    	this.initViewPager(rootView, savedInstanceState);
    	
    	this.loadingLayout = rootView.findViewById(R.id.loading_layout);
    	this.emptyView = rootView.findViewById(R.id.empty);

    	return rootView;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	
        EventBus.getDefault().register(this);
        
    	LoadEpisodesAsyncTask loadEpisodesTask = new LoadEpisodesAsyncTask();
        loadEpisodesTask.execute();
    }
    
    @Override
    public void onDestroyView() {
    	super.onDestroyView();
    	EventBus.getDefault().unregister(this);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	if (outState == null) {
    		return;
    	}
    	outState.putInt(EPISODE_ID_INTEGER_EXTRA, this.selectedEpisodeId);
    	outState.putSerializable(EPISODE_LIST_ORDER_EXTRA, this.selectedListOrder);
    	if (this.episodeDetailsPagerAdapter != null) {
    		outState.putInt(LAYOUT_OFFSET_INTEGER_EXTRA, this.episodeDetailsPagerAdapter.getLayoutOffset());
    	}
    }
    
    private void initActionBarItems() {
		View customActionContent = getActivity().getActionBar().getCustomView();
        this.likeButton = (TextView) customActionContent.findViewById(R.id.like_button);
        this.episodeTitle = (TextView) customActionContent.findViewById(R.id.episode_title);
    }

    private void initViewPager(View rootView, Bundle savedInstanceState) {
        this.episodeDetailsViewPager = (ViewPager) rootView.findViewById(R.id.episode_details_view_pager);
        this.episodeDetailsViewPager.setOffscreenPageLimit(1);
        this.episodeDetailsViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int i) {
                onEpisodePageSelected();
            }
        });
        this.episodeDetailsPagerAdapter = new EpisodeDetailsPagerAdapter(getFragmentManager(), this.episodes);
        if (savedInstanceState != null) {
        	this.episodeDetailsPagerAdapter.setLayoutOffset(savedInstanceState.getInt(LAYOUT_OFFSET_INTEGER_EXTRA));
        }
        this.episodeDetailsViewPager.setAdapter(episodeDetailsPagerAdapter);
    }

	private void updatePagerAdapterAndSelectItem(List<Episode> episodes) {
		this.episodes.clear();
		this.episodes.addAll(episodes);
        this.episodeDetailsPagerAdapter.notifyDataSetChanged();
        
        int selectedCurrentItem = getTrekService().findIndexForEpisodeId(this.selectedListOrder, this.selectedEpisodeId);
        
        if (this.episodeDetailsViewPager.getCurrentItem() == selectedCurrentItem) {
        	this.onEpisodePageSelected();
        } else {
        	this.episodeDetailsViewPager.setCurrentItem(selectedCurrentItem);
        }
    }
	
	public void onEventMainThread(EpisodeListFragment.EpisodeSelectedOnListViewEvent event) {
		int episodeId = event.getEpisodeId();
		if (episodeId != 0) {
			this.selectedEpisodeId = episodeId;
			int selectedCurrentItem = getTrekService().findIndexForEpisodeId(this.selectedListOrder, this.selectedEpisodeId);
			if (this.episodeDetailsViewPager.getCurrentItem() != selectedCurrentItem) {
				this.episodeDetailsViewPager.setCurrentItem(selectedCurrentItem);
			}
		}
	}
	
	public void onEventMainThread(EpisodeListFragment.OnTabFinishedLoadingEvent event) {
		this.selectedListOrder = event.getEpisodeListOrder();
		List<Episode> allEpisodes = this.getTrekService().getAllEpisodes(this.selectedListOrder);
		this.updatePagerAdapterAndSelectItem(allEpisodes);
	}
	
	private void onEpisodePageSelected() {
        Episode episode = this.getCurrentEpisode();
        this.selectedEpisodeId = episode.getId();
        
        this.setLikeButtonStatus(episode);
        this.episodeTitle.setText(episode.getTitleDisplay());
		this.episodeTitle.setSelected(true);
		
		if (this.isLargeScreen()) {
			this.postOnEpisodeSelectedEvent();
		}
		
		AnalyticsEventLogger.logEpisodeViewedEvent(episode);
    }
    
    public void postOnEpisodeSelectedEvent() {
		EventBus.getDefault().postSticky(new EpisodeSelectedOnViewPagerEvent(this.selectedEpisodeId));
	}
    
    public void onLikedClick() {
    	Episode episode = this.getCurrentEpisode();
    	if (episode != null) {
    		this.likePressedForEpisode(episode);
        	this.setLikeButtonStatus(episode);
        	
        	AnalyticsEventLogger.logEpisodeLikedEvent(episode);
        	
			this.sendListOfLikedEpisodesToAppGLu();
    	}
    }

	private void sendListOfLikedEpisodesToAppGLu() {
		Map<String, ?> allLikedEpisodes = this.sharedPreferences.getAll();
		
		Map<String, Object> userData = new HashMap<String, Object>();
		userData.put("favorites", allLikedEpisodes.keySet().toArray());
		
		AppGlu.userApi().writeDataInBackground(userData, new AsyncCallback<Void>() {
			public void onResult(Void result) {
				logger.info("All liked episodes were sent to AppGlu");
			}
		});
	}

    public Episode getCurrentEpisode() {
    	if (episodes == null || episodeDetailsViewPager == null) {
    		return null;
    	}
        int currentItemIndex = episodeDetailsViewPager.getCurrentItem();
        return episodes.get(currentItemIndex);
    }
    
    public void setLikeButtonStatus(Episode episode) {
        if (this.isLikePressedForEpisode(episode)) {
            likeButton.setText(R.string.liked);
            likeButton.setEnabled(false);
            likeButton.setClickable(false);
        } else {
            likeButton.setText(R.string.like);
            likeButton.setEnabled(true);
            likeButton.setClickable(true);
        }
    }

    public boolean isLikePressedForEpisode(Episode episode) {
        String episodeCode = String.valueOf(episode.getCode());
        return this.sharedPreferences.getBoolean(episodeCode, false);
    }

    public boolean likePressedForEpisode(Episode episode) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        String episodeCode = String.valueOf(episode.getCode());
        editor.putBoolean(episodeCode, true);
        return editor.commit();
    }

    private void showLoadingView(boolean show, boolean empty) {
        if (getActivity() == null) {
            return;
        }

        this.likeButton.setVisibility(View.GONE);
        
        if (show) {
            loadingLayout.setVisibility(View.VISIBLE);
            this.episodeDetailsViewPager.setVisibility(View.GONE);
        } else {
            loadingLayout.setVisibility(View.GONE);
            this.episodeDetailsViewPager.setVisibility(View.VISIBLE);
        }
        
        if (empty) {
        	emptyView.setVisibility(View.VISIBLE);
		} else {
			emptyView.setVisibility(View.GONE);
		}
        
        if (!show && !empty) {
        	this.likeButton.setVisibility(View.VISIBLE);
        }
    }

    private class LoadEpisodesAsyncTask extends AppGluAsyncTask<Void, Void, List<Episode>> {

        @Override
        protected void onPreExecute() {
            showLoadingView(true, false);
        }

        @Override
        protected List<Episode> doExecuteInBackground(Void... params) throws Exception {
            return getTrekService().getAllEpisodes(selectedListOrder);
        }

        @Override
        protected void onResult(List<Episode> result) {
        	if (getActivity() == null) {
        		return;
        	}
        	
        	if (result.isEmpty()) {
        		showLoadingView(false, true);
        	} else {
                updatePagerAdapterAndSelectItem(result);
                showLoadingView(false, false);	
        	}
        }

        @Override
        public void onException(Exception exception) {
            logger.error(exception);
            showLoadingView(false, true);
        }

    }
    
    /*
     * Posted when an episode was selected by the view pager
     */
    public class EpisodeSelectedOnViewPagerEvent {
    	
    	private int episodeId;

    	public EpisodeSelectedOnViewPagerEvent(int episodeId) {
    		this.episodeId = episodeId;
    	}

    	public int getEpisodeId() {
    		return episodeId;
    	};
    	
    }

}
