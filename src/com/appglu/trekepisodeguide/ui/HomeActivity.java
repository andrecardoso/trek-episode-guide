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

import com.appglu.impl.util.StringUtils;
import com.appglu.trekepisodeguide.model.AnalyticsEventLogger;

import android.content.Intent;
import android.os.Bundle;

/* 
 * This Activity has the purpose of controlling the restart logic used for the application.
 * Every time the application is opened from the home screen, it should start again from the splash screen.
 */
public class HomeActivity extends AbstractTrekActivity {
	
	public static final String LAUNCHED_FROM_NOTIFICATION_BOOLEAN_EXTRA = "HomeActivity.LAUNCHED_FROM_NOTIFICATION_BOOLEAN_EXTRA";
	public static final String INITIAL_EPISODE_CODE_STRING_EXTRA = "HomeActivity.INITIAL_EPISODE_CODE_STRING_EXTRA";
	public static final String APPGLU_CAMPAIGN_ID_STRING_EXTRA = "HomeActivity.APPGLU_CAMPAIGN_ID_STRING_EXTRA";
	
	public static final String SHOULD_CLOSE_APPLICATION_BOOLEAN_EXTRA = "HomeActivity.SHOULD_CLOSE_APPLICATION_BOOLEAN_EXTRA";

	private boolean shouldCloseApplication;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.shouldCloseApplication = getIntent().getBooleanExtra(SHOULD_CLOSE_APPLICATION_BOOLEAN_EXTRA, false);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.shouldCloseApplication = intent.getBooleanExtra(SHOULD_CLOSE_APPLICATION_BOOLEAN_EXTRA, false);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (this.shouldCloseApplication) {
			this.finish();
		} else {
			String episodeCode = getIntent().getStringExtra(INITIAL_EPISODE_CODE_STRING_EXTRA);
			
			/* clean up Intent so it will not be used if the application is opened again */
			
			if (StringUtils.isNotEmpty(episodeCode)) {
				getIntent().putExtra(INITIAL_EPISODE_CODE_STRING_EXTRA, "");
			}
			
			if (getIntent().getBooleanExtra(LAUNCHED_FROM_NOTIFICATION_BOOLEAN_EXTRA, false)) {
				getIntent().putExtra(LAUNCHED_FROM_NOTIFICATION_BOOLEAN_EXTRA, false);
				
				String appGluCampaignId = getIntent().getStringExtra(APPGLU_CAMPAIGN_ID_STRING_EXTRA);
				if (StringUtils.isNotEmpty(appGluCampaignId)) {
					getIntent().putExtra(APPGLU_CAMPAIGN_ID_STRING_EXTRA, "");
				}
				
				AnalyticsEventLogger.logLaunchedFromNotitification(episodeCode, appGluCampaignId);
			}
			
			Intent intent = new Intent(this, SplashActivity.class);
			intent.putExtra(SplashActivity.INITIAL_EPISODE_CODE_STRING_EXTRA, episodeCode);
			this.startActivity(intent);
		}
	}

}
