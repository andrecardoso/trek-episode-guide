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
package com.appglu.trekepisodeguide.ui.gallery;

import com.appglu.trekepisodeguide.model.Episode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ImageGalleryPagerAdapter extends FragmentStatePagerAdapter {
	
	public static final int EPISODE_NUMBER_OF_IMAGES = 3;

	private Episode episode;
	
    public ImageGalleryPagerAdapter(FragmentManager fragmentManager, Episode episode) {
        super(fragmentManager);
        this.episode = episode;
    }

    @Override
    public int getCount() {
        return EPISODE_NUMBER_OF_IMAGES;
    }

    @Override
    public Fragment getItem(int position) {
    	ImageGalleryItemFragment fragment = new ImageGalleryItemFragment();
        
        Bundle arguments = new Bundle();
        
        if (this.episode != null) {
	        String imageUrl = this.episode.getImageUrlAtIndex(position);
	        arguments.putString(ImageGalleryItemFragment.IMAGE_URL_EXTRA, imageUrl);
        }
        
        fragment.setArguments(arguments);
        
        return fragment;
    }
}
