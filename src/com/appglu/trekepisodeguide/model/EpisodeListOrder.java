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
package com.appglu.trekepisodeguide.model;

import java.util.Locale;

import com.appglu.trekepisodeguide.R;

public enum EpisodeListOrder {
	
	TITLE     (R.drawable.home_vertical_bar_abc_01, R.drawable.home_vertical_bar_abc_02, R.drawable.home_vertical_bar_abc_03),
	STARDATE  (R.drawable.home_vertical_bar_stardate_01, R.drawable.home_vertical_bar_stardate_02, R.drawable.home_vertical_bar_stardate_03),
	AIRDATE   (R.drawable.home_vertical_bar_airdate_01, R.drawable.home_vertical_bar_airdate_02, R.drawable.home_vertical_bar_airdate_03);
	
	private int scrollIndexTopDrawableResource;
	private int scrollIndexMiddleDrawableResource;
	private int scrollIndexBottomDrawableResource;
	
	EpisodeListOrder(int scrollIndexTopDrawableResource, int scrollIndexMiddleDrawableResource, int scrollIndexBottomDrawableResource) {
		this.scrollIndexTopDrawableResource = scrollIndexTopDrawableResource;
		this.scrollIndexMiddleDrawableResource = scrollIndexMiddleDrawableResource;
		this.scrollIndexBottomDrawableResource = scrollIndexBottomDrawableResource;
	}

	public int getScrollIndexTopDrawableResource() {
		return scrollIndexTopDrawableResource;
	}
	
	public int getScrollIndexMiddleDrawableResource() {
		return scrollIndexMiddleDrawableResource;
	}

	public int getScrollIndexBottomDrawableResource() {
		return scrollIndexBottomDrawableResource;
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.US);
	}

}