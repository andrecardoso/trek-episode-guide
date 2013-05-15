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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.appglu.trekepisodeguide.R;
import com.appglu.trekepisodeguide.model.AnalyticsEventLogger;
import com.appglu.trekepisodeguide.model.EpisodeListOrder;
import com.appglu.trekepisodeguide.ui.AbstractTrekActivity;
import com.appglu.trekepisodeguide.ui.HomeActivity;
import com.appglu.trekepisodeguide.ui.detail.EpisodeDetailViewPagerFragment;

public class EpisodeListActivity extends AbstractTrekActivity {
	
	public static final String INITIAL_EPISODE_ID_INTEGER_EXTRA = "EpisodeListActivity.INITIAL_EPISODE_ID_INTEGER_EXTRA";
	
	private static final String SELECTED_LIST_ORDER_SERIALIZABLE_EXTRA = "EpisodeListActivity.SELECTED_LIST_ORDER_SERIALIZABLE_EXTRA";
	
	private EpisodeListFragment listFragment;
	
	private Button titleTab;
	private Button stardateTab;
	private Button airdateTab;
	
	private EpisodeListOrder selectedListOrder;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.episode_list_activity);
        
        this.listFragment = (EpisodeListFragment) this.getSupportFragmentManager().findFragmentById(R.id.episode_list_fragment);
        
        int initialEpisodeId = getIntent().getIntExtra(INITIAL_EPISODE_ID_INTEGER_EXTRA, 0);
		this.listFragment.setInitialEpisodeId(initialEpisodeId);
        
        getActionBar().setDisplayShowCustomEnabled(true);
        
        getActionBar().setCustomView(R.layout.episode_list_action_bar);
        View actionBarCustomView = getActionBar().getCustomView();
        
        if (this.isLargeScreen()) {
        	getActionBar().setDisplayShowHomeEnabled(false);
        }
        
        this.titleTab = (Button) actionBarCustomView.findViewById(R.id.title_tab);
        this.stardateTab = (Button) actionBarCustomView.findViewById(R.id.stardate_tab);
        this.airdateTab = (Button) actionBarCustomView.findViewById(R.id.airdate_tab);
        
        if (savedInstanceState != null) {
    		this.selectedListOrder = (EpisodeListOrder) savedInstanceState.getSerializable(SELECTED_LIST_ORDER_SERIALIZABLE_EXTRA);
    	}
        
        if (this.selectedListOrder == null) {
        	this.selectedListOrder = EpisodeListOrder.TITLE;
        }
    	
        if (this.selectedListOrder == EpisodeListOrder.TITLE) {
        	this.selectTitleTab();
        }
        if (this.selectedListOrder == EpisodeListOrder.STARDATE) {
        	this.selectStardateTab();
        }
        if (this.selectedListOrder == EpisodeListOrder.AIRDATE) {
        	this.selectAirdateTab();
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putSerializable(SELECTED_LIST_ORDER_SERIALIZABLE_EXTRA, this.selectedListOrder);
    }
    
    private void unselectAllTabs() {
		this.titleTab.setSelected(false);
    	this.stardateTab.setSelected(false);
    	this.airdateTab.setSelected(false);
    	this.listFragment.onTabUnselected();
	}
    
    public void onTabTitleSelected(View view) {
    	this.unselectAllTabs();
    	this.selectTitleTab();
    }

    public void onTabStardateSelected(View view) {
    	this.unselectAllTabs();
    	this.selectStardateTab();
    }
    
    public void onTabAirdateSelected(View view) {
    	this.unselectAllTabs();
    	this.selectAirdateTab();
    }
    
    public void onLikedClick(View view) {
    	EpisodeDetailViewPagerFragment episodeDetailViewPagerFragment = this.getEpisodeDetailViewPagerFragment();
    	if (episodeDetailViewPagerFragment != null) {
    		episodeDetailViewPagerFragment.onLikedClick();
    	}
    }

	private EpisodeDetailViewPagerFragment getEpisodeDetailViewPagerFragment() {
		return (EpisodeDetailViewPagerFragment) this.getSupportFragmentManager().findFragmentById(R.id.episode_detail_fragment);
	}
    
    private void selectTitleTab() {
		this.titleTab.setSelected(true);
    	this.selectedListOrder = EpisodeListOrder.TITLE;
    	this.listFragment.onTabSelected(this.selectedListOrder);

    	AnalyticsEventLogger.logListViewedEvent(this.selectedListOrder);
	}
    
    private void selectStardateTab() {
		this.stardateTab.setSelected(true);
    	this.selectedListOrder = EpisodeListOrder.STARDATE;
    	this.listFragment.onTabSelected(this.selectedListOrder);

    	AnalyticsEventLogger.logListViewedEvent(this.selectedListOrder);
	}
    
    private void selectAirdateTab() {
		this.airdateTab.setSelected(true);
    	this.selectedListOrder = EpisodeListOrder.AIRDATE;
    	this.listFragment.onTabSelected(this.selectedListOrder);

    	AnalyticsEventLogger.logListViewedEvent(this.selectedListOrder);
	}
    
    @Override
    public void onBackPressed() {
    	Intent intent = new Intent(this, HomeActivity.class);
    	intent.putExtra(HomeActivity.SHOULD_CLOSE_APPLICATION_BOOLEAN_EXTRA, true);
    	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	this.startActivity(intent);
    	
    	super.onBackPressed();
    }

}
