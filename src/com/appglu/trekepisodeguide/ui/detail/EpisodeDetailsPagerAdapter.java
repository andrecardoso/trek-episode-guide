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

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.appglu.trekepisodeguide.R;
import com.appglu.trekepisodeguide.model.Episode;
import com.appglu.trekepisodeguide.util.MathUtils;

public class EpisodeDetailsPagerAdapter extends FragmentStatePagerAdapter {
	
	private int lastVisibleEpisodeId;
	private int lastVisiblePosition;
	
	public static final int NUMBER_OF_LAYOUTS = 3;

	public static final int FIRST_LAYOUT = 0;
    public static final int SECOND_LAYOUT = 1;
    public static final int THIRD_LAYOUT = 2;
    
    private int layoutOffset = 0;

    private final List<Episode> episodes;

    public EpisodeDetailsPagerAdapter(FragmentManager fm, List<Episode> episodes) {
        super(fm);
        this.episodes = episodes;
    }
    
    public int getLayoutOffset() {
		return layoutOffset;
	}

	public void setLayoutOffset(int layoutOffset) {
		this.layoutOffset = layoutOffset;
	}

	@Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
    	super.setPrimaryItem(container, position, object);
    	
    	EpisodeDetailFragment currentFragment = (EpisodeDetailFragment) object;
    	
    	this.lastVisibleEpisodeId = currentFragment.getEpisodeId();
    	this.lastVisiblePosition = position;
    }
    
    @Override
    public int getItemPosition(Object object) {
    	EpisodeDetailFragment currentFragment = (EpisodeDetailFragment) object;
    	
    	int currentEpisodeId = currentFragment.getEpisodeId();
    	int currentPosition = episodes.indexOf(new Episode(currentEpisodeId));
		
    	if (this.lastVisibleEpisodeId == currentEpisodeId) {
			int modOldPosition = this.lastVisiblePosition % NUMBER_OF_LAYOUTS;
			int modCurrentPosition = currentPosition % NUMBER_OF_LAYOUTS;
			
			int newOffset = this.layoutOffset - (modCurrentPosition - modOldPosition);
			this.layoutOffset = MathUtils.modPositive(newOffset, NUMBER_OF_LAYOUTS);
    	}
    	
    	return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
    	Episode episode = episodes.get(position);
    	
    	EpisodeDetailFragment fragment = new EpisodeDetailFragment();
        
        Bundle arguments = new Bundle();
        arguments.putInt(EpisodeDetailFragment.EPISODE_ID_EXTRA, episode.getId());
		arguments.putSerializable(EpisodeDetailFragment.EPISODE_SERIALIZABLE_EXTRA, episode);
        arguments.putInt(EpisodeDetailFragment.EPISODE_LAYOUT_EXTRA, getEpisodeLayout(position));
        fragment.setArguments(arguments);
        
        return fragment;
    }
    
    private int getEpisodeLayout(int position) {
    	int positionIndex = position + this.layoutOffset;
    	
    	int mod = MathUtils.modPositive(positionIndex, NUMBER_OF_LAYOUTS);
    	
        if (mod == FIRST_LAYOUT) {
            return R.layout.episode_details_first_fragment;
        } 
        
        if (mod == SECOND_LAYOUT) {
            return R.layout.episode_details_second_fragment;
        }
        
        if (mod == THIRD_LAYOUT) {
            return R.layout.episode_details_third_fragment;
        }
        
        return R.layout.episode_details_first_fragment;
    }

    @Override
    public int getCount() {
        return episodes.size();
    }

}
