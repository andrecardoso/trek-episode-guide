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

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;

import com.appglu.trekepisodeguide.R;
import com.appglu.trekepisodeguide.model.EpisodeListOrder;
import com.appglu.trekepisodeguide.ui.AbstractTrekActivity;
import com.appglu.trekepisodeguide.ui.list.EpisodeListActivity;

public class EpisodeDetailActivity extends AbstractTrekActivity {

    public static final String EPISODE_ID_INTEGER_EXTRA = "EpisodeDetailActivity.EPISODE_ID_INTEGER_EXTRA";
    public static final String EPISODE_LIST_ORDER_EXTRA = "EpisodeDetailActivity.EPISODE_LIST_ORDER_EXTRA";

    private EpisodeDetailViewPagerFragment episodeDetailViewPagerFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.episode_detail_activity);
        
        this.initActionBar();

        if (savedInstanceState == null) {
        	int selectedEpisodeId = getIntent().getIntExtra(EPISODE_ID_INTEGER_EXTRA, 0);
        	EpisodeListOrder selectedListOrder = (EpisodeListOrder) getIntent().getSerializableExtra(EPISODE_LIST_ORDER_EXTRA);
	        
	        this.episodeDetailViewPagerFragment = new EpisodeDetailViewPagerFragment();
	    	
	        Bundle arguments = new Bundle();
	        arguments.putInt(EpisodeDetailViewPagerFragment.EPISODE_ID_INTEGER_EXTRA, selectedEpisodeId);
	        arguments.putSerializable(EpisodeDetailViewPagerFragment.EPISODE_LIST_ORDER_EXTRA, selectedListOrder);
	        episodeDetailViewPagerFragment.setArguments(arguments);
	        
	        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
	        fragmentTransaction.add(R.id.episode_detail_view_pager_fragment, episodeDetailViewPagerFragment);
	        fragmentTransaction.commit();
        } else {
        	this.episodeDetailViewPagerFragment = (EpisodeDetailViewPagerFragment) this.getSupportFragmentManager().findFragmentById(R.id.episode_detail_view_pager_fragment);
        }
        
    }

    private void initActionBar() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.details_action_bar);
    }

	public void onLikedClick(View view) {
		this.episodeDetailViewPagerFragment.onLikedClick();
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
    	this.episodeDetailViewPagerFragment.postOnEpisodeSelectedEvent();
    	
    	Intent intent = new Intent(this, EpisodeListActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	this.startActivity(intent);
    	this.finish();
    }

}