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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.appglu.android.AppGlu;
import com.appglu.android.push.AppGluGCMBaseIntentService;
import com.appglu.android.push.PushNotification;
import com.appglu.impl.util.StringUtils;
import com.appglu.trekepisodeguide.ui.HomeActivity;

public class GCMIntentService extends AppGluGCMBaseIntentService {
	
	private static final int NOTIFICATION_ID = 1;
	
	private static final String TITLE_PARAM_KEY = "title";
	private static final String INFO_PARAM_KEY = "info";
	private static final String TICKER_PARAM_KEY = "ticker";
	
	private static final String EPISODE_ID_PARAM_KEY = "episode.code";
	private static final String EPISODE_URL_PARAM_KEY = "episode.url";
	private static final String AGCID_PARAM_KEY = "agcid";
	
	@Override
	protected String[] getSenderIds(Context context) {
		String gcmSenderId = context.getResources().getString(R.string.gcm_sender_id);
		return new String[] {gcmSenderId};
	}
	
	@Override
	public void onNotificationReceived(Context context, PushNotification pushNotification) {
		String notificationTitle = this.getNotificationTitle(context, pushNotification);
		
		Notification.Builder builder = new Notification.Builder(context)
	        .setContentTitle(notificationTitle)
	        .setContentText(pushNotification.getContent())
	        .setSmallIcon(R.drawable.notification_icon)
	        .setAutoCancel(true)
	        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
		
		String contentInfo = this.getContentInfo(pushNotification);
		if (StringUtils.isNotEmpty(contentInfo)) {
			if (StringUtils.isDigits(contentInfo)) {
				builder.setNumber(StringUtils.stringToInt(contentInfo));
			} else {
				builder.setContentInfo(contentInfo);
			}
		}
		
		String tickerText = this.getTickerText(pushNotification);
		if (StringUtils.isNotEmpty(tickerText)) {
			builder.setTicker(tickerText);
		}
		
		String episodeUrl = this.getEpisodeUrl(pushNotification);
		if (StringUtils.isNotEmpty(episodeUrl)) {
			Bitmap thumbnail = AppGlu.storageApi().downloadAsBitmap(episodeUrl, 100, 100);
			builder.setLargeIcon(thumbnail);
		}
		
		Intent resultIntent = new Intent(this, HomeActivity.class);
		resultIntent.putExtra(HomeActivity.LAUNCHED_FROM_NOTIFICATION_BOOLEAN_EXTRA, true);
		resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		resultIntent.setAction(Intent.ACTION_MAIN);
		resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		String episodeCode = this.getEpisodeCode(pushNotification);
		if (StringUtils.isNotEmpty(episodeCode)) {
			resultIntent.putExtra(HomeActivity.INITIAL_EPISODE_CODE_STRING_EXTRA, episodeCode);
		}
		
		String appGluCampaignId = this.getAppGluCampaignId(pushNotification);
		if (StringUtils.isNotEmpty(appGluCampaignId)) {
			resultIntent.putExtra(HomeActivity.APPGLU_CAMPAIGN_ID_STRING_EXTRA, appGluCampaignId);
		}
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pendingIntent);
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, builder.getNotification());
	}
	
	private String getNotificationTitle(Context context, PushNotification pushNotification) {
		String paramTitle = pushNotification.getParameters().get(TITLE_PARAM_KEY);
		
		if (StringUtils.isNotEmpty(paramTitle)) {
			return paramTitle;
		}
		
		return context.getResources().getString(R.string.app_name);
	}
	
	private String getContentInfo(PushNotification pushNotification) {
		return pushNotification.getParameters().get(INFO_PARAM_KEY);
	}
	
	private String getTickerText(PushNotification pushNotification) {
		return pushNotification.getParameters().get(TICKER_PARAM_KEY);
	}
	
	private String getEpisodeUrl(PushNotification pushNotification) {
		return pushNotification.getParameters().get(EPISODE_URL_PARAM_KEY);
	}
	
	private String getEpisodeCode(PushNotification pushNotification) {
		return pushNotification.getParameters().get(EPISODE_ID_PARAM_KEY);
	}
	
	private String getAppGluCampaignId(PushNotification pushNotification) {
		return pushNotification.getParameters().get(AGCID_PARAM_KEY);
	}

}