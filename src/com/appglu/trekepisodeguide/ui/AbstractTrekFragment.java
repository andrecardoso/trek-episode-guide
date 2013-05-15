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
package com.appglu.trekepisodeguide.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.appglu.android.log.Logger;
import com.appglu.android.log.LoggerFactory;
import com.appglu.trekepisodeguide.R;
import com.appglu.trekepisodeguide.TrekApplication;
import com.appglu.trekepisodeguide.model.TrekService;

public class AbstractTrekFragment extends Fragment {
	
	protected Logger logger = LoggerFactory.getLogger(TrekApplication.LOG_TAG);
	
	private TrekService trekService;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		TrekApplication trekApplication = (TrekApplication) activity.getApplication();
		this.trekService = trekApplication.getTrekService();
	}
	
	public TrekService getTrekService() {
		return this.trekService;
	}
	
	protected boolean isLargeScreen() {
        return getResources().getBoolean(R.bool.is_large_screen);
    }

}