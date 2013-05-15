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

import com.appglu.AnalyticsSessionEvent;
import com.appglu.android.AppGlu;
import com.appglu.impl.util.StringUtils;

public final class AnalyticsEventLogger {
	
	public static void logLaunchedFromNotitification(String episodeCode, String appGluCampaignId) {
		if (StringUtils.isNotEmpty(appGluCampaignId)) {
			AppGlu.analyticsApi().setSessionParameter("push_source", appGluCampaignId);
		}
		
		AnalyticsSessionEvent event = new AnalyticsSessionEvent("launched.from.notification");
		
		if (StringUtils.isNotEmpty(episodeCode)) {
			event.addParameter("episode.code", episodeCode);
		}
		
        AppGlu.analyticsApi().logEvent(event);
	}
	
	public static void logListViewedEvent(EpisodeListOrder order) {
		AnalyticsSessionEvent event = new AnalyticsSessionEvent("list.viewed");
        event.addParameter("list.order", order.toString());
        
        AppGlu.analyticsApi().logEvent(event);
	}
	
	public static void logEpisodeViewedEvent(Episode episode) {
		AnalyticsSessionEvent event = new AnalyticsSessionEvent("episode.viewed");
        event.addParameter("episode.code", episode.getCode());
        
        AppGlu.analyticsApi().logEvent(event);
	}

	public static void logEpisodeLikedEvent(Episode episode) {
		AnalyticsSessionEvent event = new AnalyticsSessionEvent("episode.liked");
        event.addParameter("episode.code", episode.getCode());
        
        AppGlu.analyticsApi().logEvent(event);
	}
	
	public static void logImagedViewedEvent(Episode episode, int i) {
		String url = episode.getImageUrlAtIndex(i);
		
		if (StringUtils.isNotEmpty(url)) {
			AnalyticsSessionEvent event = new AnalyticsSessionEvent("image.viewed");
			
	        event.addParameter("episode.code", episode.getCode());
			event.addParameter("url", url);
	        
	        AppGlu.analyticsApi().logEvent(event);
		}
	}

}