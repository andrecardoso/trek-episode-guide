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
package com.appglu.trekepisodeguide;

import android.app.Application;

import com.appglu.android.AppGlu;
import com.appglu.android.AppGluSettings;
import com.appglu.android.cache.FileSystemCacheManager;
import com.appglu.android.log.LoggerLevel;
import com.appglu.trekepisodeguide.model.TrekDatabaseHelper;
import com.appglu.trekepisodeguide.model.TrekSQLiteRepository;
import com.appglu.trekepisodeguide.model.TrekService;

public class TrekApplication extends Application {
	
	private static final int ONE_WEEK_IN_MILLISECONDS = 7 * 24 * 60 * 60 * 1000;
	private static final long FIFTY_MEGA_BYTES = 50 * 1024 * 1024;

	public static final java.lang.String LOG_TAG = "Trek";
	
	private TrekService trekService;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		String applicationKey = this.getString(R.string.appglu_app_key);
		String applicationSecret = this.getString(R.string.appglu_app_secret);
		String applicationEnvironment = this.getString(R.string.appglu_app_environment);
		
		AppGluSettings settings = new AppGluSettings(applicationKey, applicationSecret, applicationEnvironment);
		
		TrekDatabaseHelper databaseHelper = new TrekDatabaseHelper(this);
		
		settings.setDefaultSyncDatabaseHelper(databaseHelper);
		settings.setLoggerLevel(LoggerLevel.DEBUG);
		settings.setDefaultStorageCacheManager(new FileSystemCacheManager(this, FIFTY_MEGA_BYTES));
		settings.setStorageCacheTimeToLiveInMilliseconds(ONE_WEEK_IN_MILLISECONDS);
		
		AppGlu.initialize(this, settings);
		
		TrekSQLiteRepository trekSQLiteRepository = new TrekSQLiteRepository(databaseHelper);
		this.trekService = new TrekService(trekSQLiteRepository);
		
		String gcmSenderId = this.getResources().getString(R.string.gcm_sender_id);
		AppGlu.pushApi().registerForPushNotifications(this, gcmSenderId);
	}
	
	public TrekService getTrekService() {
		return trekService;
	}

}