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
package com.appglu.trekepisodeguide.ui.detail;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appglu.android.AppGlu;
import com.appglu.android.task.AppGluAsyncTask;
import com.appglu.android.task.AsyncTaskExecutor;
import com.appglu.android.task.ImageViewAsyncCallback;
import com.appglu.trekepisodeguide.R;
import com.appglu.trekepisodeguide.model.Episode;
import com.appglu.trekepisodeguide.ui.AbstractTrekFragment;
import com.appglu.trekepisodeguide.ui.gallery.ImageGalleryActivity;

import de.greenrobot.event.EventBus;

public class EpisodeDetailFragment extends AbstractTrekFragment {
	
	private static final Executor SERIAL_QUEUE_EXECUTOR = AsyncTaskExecutor.createSerialQueueExecutor(20);
	private AsyncTaskExecutor serialExecutor = new AsyncTaskExecutor(SERIAL_QUEUE_EXECUTOR, false);

	public static final String EPISODE_ID_EXTRA = "EpisodeDetailFragment.EPISODE_ID_EXTRA";
    public static final String EPISODE_SERIALIZABLE_EXTRA = "EpisodeDetailFragment.EPISODE_SERIALIZABLE_EXTRA";
    public static final String EPISODE_LAYOUT_EXTRA = "EpisodeDetailFragment.EPISODE_LAYOUT_EXTRA";

    private int episodeId;
    
    private Episode episode;
    private int episodeLayout;
    
    private View rootView;
    private TextView descriptionTextView;

    public EpisodeDetailFragment() {

    }
    
    public int getEpisodeId() {
		return episodeId;
	}

	private boolean isFirstScreen() {
    	return episodeLayout == R.layout.episode_details_first_fragment;
    }
    
    private boolean isSecondScreen() {
    	return episodeLayout == R.layout.episode_details_second_fragment;
    }
    
    private boolean isThirdScreen() {
    	return episodeLayout == R.layout.episode_details_third_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	this.episodeId = getArguments().getInt(EPISODE_ID_EXTRA);
        this.episode = (Episode) getArguments().getSerializable(EPISODE_SERIALIZABLE_EXTRA);
        this.episodeLayout = getArguments().getInt(EPISODE_LAYOUT_EXTRA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(episodeLayout, container, false);

        this.initViews();
        this.initThumbnails();
        
        return this.rootView;
    }
    
	@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	
    	EventBus.getDefault().register(this);
    	
    	LoadEpisodeDescriptionAsyncTask loadEpisodeDescriptionTask = new LoadEpisodeDescriptionAsyncTask();
        loadEpisodeDescriptionTask.execute();
    }
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (!this.isLargeScreen()) {
			this.startAnimations(this.rootView);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (!this.isLargeScreen()) {
			this.stopAnimations(this.rootView);
		}
	}
	
	public void onEventMainThread(EpisodeDetailViewPagerFragment.EpisodeSelectedOnViewPagerEvent event) {
		if (this.isLargeScreen()) {
			if (event.getEpisodeId() == episode.getId()) {
				this.startAnimations(this.rootView);
			} else {
				this.stopAnimations(this.rootView);
			}
		}
	}

    private void initViews() {
    	this.descriptionTextView = (TextView) rootView.findViewById(R.id.description);
    	
        TextView starDate = (TextView) rootView.findViewById(R.id.star_date);
        starDate.setText(episode.getStardateDisplay());

        if (this.isSecondScreen()) {
        	TextView seriesNumber = (TextView) rootView.findViewById(R.id.series_number);
        	TextView seasonNumber = (TextView) rootView.findViewById(R.id.season_number);
        	TextView episodeNumber = (TextView) rootView.findViewById(R.id.episode_number);
        	
        	seriesNumber.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        	seasonNumber.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        	episodeNumber.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        	
        	seriesNumber.setText(episode.getSeries());
        	seasonNumber.setText(episode.getSeason());
        	episodeNumber.setText(episode.getEpisode());
        } else {
        	TextView seriesSeasonEpisodeNumber = (TextView) rootView.findViewById(R.id.series_season_episode_number);
        	seriesSeasonEpisodeNumber.setText(episode.getCode());
        }
        
        TextView airDate = (TextView) rootView.findViewById(R.id.air_date);
        airDate.setText(episode.getAirdateDisplay());
        
        //disable hardware acceleration for scroll views with long texts to avoid slowness
        ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.text_scroll_view);
        scrollView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void initThumbnails() {
    	int[] imageResourcesIds = new int[]{R.id.thumbnail_first, R.id.thumbnail_second, R.id.thumbnail_third};
    	int[] progressResourcesIds = new int[]{R.id.progress_first, R.id.progress_second, R.id.progress_third};
        int[] placeholderResourcesIds = new int[]{R.id.placeholder_first, R.id.placeholder_second, R.id.placeholder_third};
        int[] clickableContainer = new int[]{R.id.container_first, R.id.container_second, R.id.container_third};

        for (int i = 0; i < progressResourcesIds.length; i++) {
        	ImageView imageView = (ImageView) rootView.findViewById(imageResourcesIds[i]);
        	View inProgressView = rootView.findViewById(progressResourcesIds[i]);
            View placeholderView = rootView.findViewById(placeholderResourcesIds[i]);
            
            if (episode.hasThumbnailUrlAtIndex(i)) {
            	this.downloadToImageViewInBackground(episode.getThumbnailUrlAtIndex(i), imageView, inProgressView, placeholderView);
            } else {
            	inProgressView.setVisibility(View.GONE);
            	placeholderView.setVisibility(View.VISIBLE);
            }
            
            View container = rootView.findViewById(clickableContainer[i]);
            container.setTag(i);

            container.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Integer galleryIndex = (Integer) view.getTag();
                    openImageGalleryAtIndex(galleryIndex);
                }
            });
        }
    }
    
    private void downloadToImageViewInBackground(final String url, ImageView imageView, View inProgressView, View placeholderView) {
		ImageViewAsyncCallback asyncCallback = new ImageViewAsyncCallback(imageView, inProgressView, placeholderView);
		
		serialExecutor.execute(asyncCallback, new Callable<Bitmap>() {
			public Bitmap call() throws Exception {
				return AppGlu.storageApi().downloadAsBitmap(url, 200, 150);
			}
		});
	}
    
    private void startAnimations(View rootView) {
    	if (this.isFirstScreen()) {
			this.startAnimationForView(rootView, R.id.first_screen_digits_animation);
			this.startAnimationForView(rootView, R.id.top_delimiter_animation);
			this.startAnimationForView(rootView, R.id.stardate_holder_animation);
			this.startAnimationForView(rootView, R.id.bottom_right_image_animation);
			this.startAnimationForView(rootView, R.id.globe_animation);
			this.startAnimationForView(rootView, R.id.first_screen_airdate_holder_animation);
    	}
		
    	if (this.isSecondScreen()) {
	    	this.startAnimationForView(rootView, R.id.season_number);
			this.startAnimationForView(rootView, R.id.episode_number);
			this.startAnimationForView(rootView, R.id.second_screen_bottom_digits);
			this.startAnimationForView(rootView, R.id.second_screen_radar);
			this.startAnimationForView(rootView, R.id.second_screen_tree_image);
    	}

    	if (this.isThirdScreen()) {
			this.startAnimationForView(rootView, R.id.third_screen_digits_animation);
			this.startAnimationForView(rootView, R.id.top_corner_animation);
			this.startAnimationForView(rootView, R.id.bottom_bar_chart_animation);
			this.startAnimationForView(rootView, R.id.third_screen_top_delimeter);
    	}
	}
    
    private void stopAnimations(View rootView) {
    	if (this.isFirstScreen()) {
			this.stopAnimationForView(rootView, R.id.first_screen_digits_animation);
			this.stopAnimationForView(rootView, R.id.top_delimiter_animation);
			this.stopAnimationForView(rootView, R.id.stardate_holder_animation);
			this.stopAnimationForView(rootView, R.id.bottom_right_image_animation);
			this.stopAnimationForView(rootView, R.id.globe_animation);
			this.stopAnimationForView(rootView, R.id.first_screen_airdate_holder_animation);
    	}
		
    	if (this.isSecondScreen()) {
			this.stopAnimationForView(rootView, R.id.season_number);
			this.stopAnimationForView(rootView, R.id.episode_number);
			this.stopAnimationForView(rootView, R.id.second_screen_bottom_digits);
			this.stopAnimationForView(rootView, R.id.second_screen_radar);
			this.stopAnimationForView(rootView, R.id.second_screen_tree_image);
    	}
		
    	if (this.isThirdScreen()) {
			this.stopAnimationForView(rootView, R.id.third_screen_digits_animation);
			this.stopAnimationForView(rootView, R.id.top_corner_animation);
			this.stopAnimationForView(rootView, R.id.bottom_bar_chart_animation);
			this.stopAnimationForView(rootView, R.id.third_screen_top_delimeter);
    	}
    }

	private void startAnimationForView(View rootView, int id) {
		View view = rootView.findViewById(id);
		if (view == null || !(view.getBackground() instanceof AnimationDrawable)) {
			return;
		}
		AnimationDrawable drawable = (AnimationDrawable) view.getBackground();
		drawable.start();
	}
	
	private void stopAnimationForView(View rootView, int id) {
		View view = rootView.findViewById(id);
		if (view == null || !(view.getBackground() instanceof AnimationDrawable)) {
			return;
		}
		AnimationDrawable drawable = (AnimationDrawable) view.getBackground();
		drawable.stop();
	}

    public void openImageGalleryAtIndex(Integer galleryIndex) {
        if (galleryIndex != null) {
            Intent intent = new Intent(getActivity(), ImageGalleryActivity.class);
            intent.putExtra(ImageGalleryActivity.EPISODE_SERIALIZABLE_EXTRA, episode);
            intent.putExtra(ImageGalleryActivity.SELECTED_IMAGE_INDEX, galleryIndex);
            this.startActivity(intent);
        }
    }
    
    private class LoadEpisodeDescriptionAsyncTask extends AppGluAsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
        	
        }

        @Override
        protected String doExecuteInBackground(Void... params) throws Exception {
            return getTrekService().getEpisodeDescription(episode.getId());
        }

        @Override
        protected void onResult(String description) {
        	if (getActivity() == null) {
        		return;
        	}
            descriptionTextView.setText(description);
        }

        @Override
        public void onException(Exception exception) {
            logger.error(exception);
        }

    }

}
