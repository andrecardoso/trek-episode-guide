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

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.appglu.android.analytics.activity.AppGluAnalyticsFragmentActivity;
import com.appglu.android.log.Logger;
import com.appglu.android.log.LoggerFactory;
import com.appglu.trekepisodeguide.R;
import com.appglu.trekepisodeguide.TrekApplication;
import com.appglu.trekepisodeguide.model.TrekService;

public abstract class AbstractTrekActivity extends AppGluAnalyticsFragmentActivity {
	
	protected Logger logger = LoggerFactory.getLogger(TrekApplication.LOG_TAG);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setSupportedScreenOrientations();
	}
	
	protected void setSupportedScreenOrientations() {
		if (this.isLargeScreen()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
	}

	@Override
	public void onResume() {
		super.onResume();
		
		View view = getWindow().getDecorView().findViewById(android.R.id.content);
		view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}
	
	public TrekService getTrekService() {
		TrekApplication trekApplication = (TrekApplication) this.getApplication();
		return trekApplication.getTrekService();
	}
	
	protected boolean isLargeScreen() {
        return getResources().getBoolean(R.bool.is_large_screen);
    }

}