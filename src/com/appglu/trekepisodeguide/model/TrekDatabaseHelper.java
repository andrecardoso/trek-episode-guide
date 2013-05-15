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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.appglu.android.sync.SyncDatabaseHelper;

public class TrekDatabaseHelper extends SyncDatabaseHelper {
	
	public TrekDatabaseHelper(Context context) {
		super(context, "trek.sqlite", 1, false);
	}

	@Override
	public void onCreateAppDatabase(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE episode ( " +
			  "id INT(11) NOT NULL , " +
			  "code VARCHAR(15) NULL , " +
			  "stardate_section VARCHAR(30) NULL , " +
			  "stardate_display VARCHAR(30) NULL , " +
			  "stardate_sort FLOAT NULL , " +
			  "title_sort VARCHAR(100) NULL , " +
			  "title_display VARCHAR(100) NULL , " +
			  "airdate_section VARCHAR(30) NULL , " +
			  "airdate_display VARCHAR(30) NULL , " +
			  "airdate_sort DATE NULL , " +
			  "description TEXT NULL , " +
			  "grid_image VARCHAR(200) NULL , " +
			  "thumbnail_url_1 VARCHAR(200) NULL , " +
			  "thumbnail_url_2 VARCHAR(200) NULL , " +
			  "thumbnail_url_3 VARCHAR(200) NULL , " +
			  "image_url_1 VARCHAR(200) NULL , " +
			  "image_url_2 VARCHAR(200) NULL , " +
			  "image_url_3 VARCHAR(200) NULL , " +
			  "PRIMARY KEY (id))");
	}

	@Override
	public void onUpgradeAppDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}