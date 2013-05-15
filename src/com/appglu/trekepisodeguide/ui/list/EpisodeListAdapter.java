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
package com.appglu.trekepisodeguide.ui.list;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appglu.android.AppGlu;
import com.appglu.android.task.AsyncTaskExecutor;
import com.appglu.android.task.ImageViewAsyncCallback;
import com.appglu.impl.util.StringUtils;
import com.appglu.trekepisodeguide.R;
import com.appglu.trekepisodeguide.model.Episode;
import com.appglu.trekepisodeguide.model.EpisodeListOrder;

public class EpisodeListAdapter extends ArrayAdapter<Episode> {
	
	private static final Executor SERIAL_STACK_EXECUTOR = AsyncTaskExecutor.createSerialStackExecutor(15);
	private AsyncTaskExecutor serialExecutor = new AsyncTaskExecutor(SERIAL_STACK_EXECUTOR, false);
	
	private LayoutInflater layoutInflater;
	
	private EpisodeListOrder listOrder;

	public EpisodeListAdapter(Context context, List<Episode> episodes, EpisodeListOrder listOrder) {
		super(context, R.layout.episode_list_item, episodes);
		this.layoutInflater = LayoutInflater.from(context);
		
		this.listOrder = listOrder;
		if (this.listOrder == null) {
			this.listOrder = EpisodeListOrder.TITLE;
		}
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null || convertView.getTag() == null) {
			holder = new ViewHolder();
			convertView = this.layoutInflater.inflate(R.layout.episode_list_item, parent, false);
			
			holder.thumbnail = (ImageView) convertView.findViewById(R.id.image);
			holder.progress = convertView.findViewById(R.id.progress);
			holder.placeholder = convertView.findViewById(R.id.placeholder);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.bubble = (TextView) convertView.findViewById(R.id.bubble);
			
			View thumbnailLayout = convertView.findViewById(R.id.thumbnail_layout);
			this.calculateListItemHeight(thumbnailLayout, parent);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.progress.setVisibility(View.VISIBLE);
		holder.placeholder.setVisibility(View.GONE);
		
		Episode episode = this.getItem(position);
		
		this.retrieveBitmapFromCacheManager(episode.getGridImageUrl(), holder.thumbnail, holder.progress, holder.placeholder);
		
		holder.title.setText(episode.getTitleDisplay());
		
		if (listOrder == EpisodeListOrder.TITLE) {
			holder.bubble.setText(null);
			holder.bubble.setBackgroundDrawable(null);
		}
		
		if (listOrder == EpisodeListOrder.STARDATE) {
			holder.bubble.setText(episode.getStardateDisplay());
			holder.bubble.setBackgroundResource(R.drawable.star_date_shape);
			
		}
		
		if (listOrder == EpisodeListOrder.AIRDATE) {
			holder.bubble.setText(episode.getAirdateDisplay());
			holder.bubble.setBackgroundResource(R.drawable.air_date_shape);
		}
		
		return convertView;
	}
	
	private void calculateListItemHeight(View thumbnailLayout, ViewGroup parent) {
		float screenWidth = parent.getWidth();
		
		float left = getContext().getResources().getDimension(R.dimen.list_item_margin_left);
		float right = getContext().getResources().getDimension(R.dimen.list_item_margin_right);
		
		float thumbnailWidth = screenWidth - left - right;
		float thumbnailHeight = (thumbnailWidth * 3) / 4;
		
		thumbnailLayout.getLayoutParams().height = (int) thumbnailHeight;
	}

	private void retrieveBitmapFromCacheManager(final String url, final ImageView imageView, final View inProgressView, final View placeholderView) {
		if (StringUtils.isNotEmpty(url) && url.equals(imageView.getTag())) {
			return;
		}
		
		imageView.setTag(url);
		inProgressView.setTag(url);
		placeholderView.setTag(url);

		Bitmap cachedBitmap = AppGlu.storageApi().retrieveBitmapFromCacheManager(url, 440, 330);
		if (cachedBitmap == null) {
			this.downloadAsBitmap(url, imageView, inProgressView, placeholderView);
		} else {
			imageView.setImageBitmap(cachedBitmap);
		}
	}
	
	private void downloadAsBitmap(final String url, ImageView imageView, View inProgressView, View placeholderView) {
		ImageViewAsyncCallback asyncCallback = new ImageViewAsyncCallback(url, imageView, inProgressView, placeholderView);
		
		serialExecutor.execute(asyncCallback, new Callable<Bitmap>() {
			public Bitmap call() throws Exception {
				// Loads image from the cache or download if not cached
				return AppGlu.storageApi().downloadAsBitmap(url, 440, 330);
			}
		});
	}
	
	static class ViewHolder {
		ImageView thumbnail;
		View progress;
		View placeholder;
		TextView title;
		TextView bubble;
	}
	
}
