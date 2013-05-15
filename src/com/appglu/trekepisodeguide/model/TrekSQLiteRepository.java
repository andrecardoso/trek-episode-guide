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

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TrekSQLiteRepository {

	private static final int ID = 0;
	private static final int CODE = 1;
	private static final int STARDATE_SECTION = 2;
	private static final int STARDATE_DISPLAY = 3;
	private static final int STARDATE_SORT = 4;
	private static final int TITLE_SORT = 5;
	private static final int TITLE_DISPLAY = 6;
	private static final int AIRDATE_SECTION = 7;
	private static final int AIRDATE_DISPLAY = 8;
	private static final int AIRDATE_SORT = 9;
	private static final int GRID_IMAGE = 10;
	private static final int THUMBNAIL_URL_1 = 11;
	private static final int THUMBNAIL_URL_2 = 12;
	private static final int THUMBNAIL_URL_3 = 13;
	private static final int IMAGE_URL_1 = 14;
	private static final int IMAGE_URL_2 = 15;
	private static final int IMAGE_URL_3 = 16;
	
	private TrekDatabaseHelper databaseHelper;
	
	public TrekSQLiteRepository(TrekDatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
	public List<Episode> getAllEpisodes() {
		SQLiteDatabase database = this.databaseHelper.getReadableDatabase();
		Cursor cursor = null;
		
		try {
			String sql = "SELECT id, code, stardate_section, stardate_display, stardate_sort, title_sort, title_display, " +
				"airdate_section, airdate_display, airdate_sort, grid_image, thumbnail_url_1, thumbnail_url_2, thumbnail_url_3, " +
				"image_url_1, image_url_2, image_url_3 FROM episode";
			
			cursor = database.rawQuery(sql, null);
		    cursor.moveToFirst();
		    
		    List<Episode> episodes = new ArrayList<Episode>(cursor.getCount());
		    
		    for (int i = 0; i < cursor.getCount(); i++) {
		    	Episode episode = this.mapRow(cursor);
		    	if (episode.isValid()) {
		    		episodes.add(episode);
		    	}
		    	cursor.moveToNext();
		    }
		    
		    return episodes;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			database.close();
		}
	}
	
	public String getEpisodeDescription(int id) {
		SQLiteDatabase database = this.databaseHelper.getReadableDatabase();
		Cursor cursor = null;
		
		try {
			String sql = "SELECT description FROM episode where id = ?";
			
			cursor = database.rawQuery(sql, new String[] {String.valueOf(id)});
		    cursor.moveToFirst();
		    
		    if (cursor.getCount() > 0) {
		    	return cursor.getString(0);
		    }
		    
		    return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			database.close();
		}
	}

	private Episode mapRow(Cursor cursor) {
		Episode episode = new Episode();
		
		episode.setId(cursor.getInt(ID));
		episode.setCode(cursor.getString(CODE));
		episode.setStardateSection(cursor.getString(STARDATE_SECTION));
		episode.setStardateDisplay(cursor.getString(STARDATE_DISPLAY));
		episode.setStardateSort(cursor.getFloat(STARDATE_SORT));
		episode.setTitleSort(cursor.getString(TITLE_SORT));
		episode.setTitleDisplay(cursor.getString(TITLE_DISPLAY));
		episode.setAirdateSection(cursor.getString(AIRDATE_SECTION));
		episode.setAirdateDisplay(cursor.getString(AIRDATE_DISPLAY));
		episode.setAirdateSort(cursor.getLong(AIRDATE_SORT));
		episode.setGridImageUrl(cursor.getString(GRID_IMAGE));
		episode.setThumbnailUrl1(cursor.getString(THUMBNAIL_URL_1));
		episode.setThumbnailUrl2(cursor.getString(THUMBNAIL_URL_2));
		episode.setThumbnailUrl3(cursor.getString(THUMBNAIL_URL_3));
		episode.setImageUrl1(cursor.getString(IMAGE_URL_1));
		episode.setImageUrl2(cursor.getString(IMAGE_URL_2));
		episode.setImageUrl3(cursor.getString(IMAGE_URL_3));
		
		return episode;
	}

}
