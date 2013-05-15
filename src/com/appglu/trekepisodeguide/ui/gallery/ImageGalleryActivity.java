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
package com.appglu.trekepisodeguide.ui.gallery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.appglu.trekepisodeguide.R;
import com.appglu.trekepisodeguide.model.AnalyticsEventLogger;
import com.appglu.trekepisodeguide.model.Episode;
import com.appglu.trekepisodeguide.ui.AbstractTrekActivity;

public class ImageGalleryActivity extends AbstractTrekActivity {

    private static final int TIME_OF_INACTIVITY_IN_MILLISECONDS = 5 * 1000;

    public static final String EPISODE_SERIALIZABLE_EXTRA = "ImageGalleryActivity.EPISODE_SERIALIZABLE_EXTRA";
    public static final String SELECTED_IMAGE_INDEX = "ImageGalleryActivity.SELECTED_IMAGE_INDEX";

    private static final String CURRENT_OVERLAY_ALPHA_EXTRA = "ImageGalleryActivity.CURRENT_OVERLAY_ALPHA_EXTRA";

    private Episode selectedEpisode;
    private int selectedImageIndex;

    private int currentPagerItem;

    private ImageGalleryPagerAdapter imageGalleryPagerAdapter;

    private ViewPager galleryViewPager;
    private TextView imageNumber;
    private View topOverlay;
    private View bottomOverlay;
    private View closeButton;
    private TextView nextButton;
    private TextView previousButton;

    private Handler inactivityHandler = new Handler();

    private Runnable inactivityRunnable = new Runnable() {
        @Override
        public void run() {
            fadeOutOverlayIfNeeded();
        }
    };

    private ImageGalleryAnimationProvider imageGalleryAnimationProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.image_gallery_activity);

        this.selectedEpisode = (Episode) getIntent().getSerializableExtra(EPISODE_SERIALIZABLE_EXTRA);
        
        if (savedInstanceState == null) {
        	this.selectedImageIndex = getIntent().getIntExtra(SELECTED_IMAGE_INDEX, 0);
        } else {
        	this.selectedImageIndex = savedInstanceState.getInt(SELECTED_IMAGE_INDEX, 0);
        }
        
        this.initViews();
        this.initViewPagerAdapter();
    }
    
    @Override
    protected void setSupportedScreenOrientations() {
    	//don't do anything to support portrait and landscape
    }

    private void initViews() {
        this.imageNumber = (TextView) findViewById(R.id.image_number);

        this.closeButton = findViewById(R.id.close_button);
        this.nextButton = (TextView) findViewById(R.id.next_button);
        this.previousButton = (TextView) findViewById(R.id.previous_button);
        this.topOverlay = findViewById(R.id.top_overlay);
        this.bottomOverlay = findViewById(R.id.bottom_overlay);
        
        this.galleryViewPager = (ViewPager) findViewById(R.id.gallery_view_pager);
        
        this.imageGalleryAnimationProvider = new ImageGalleryAnimationProvider(this, topOverlay, bottomOverlay, galleryViewPager);
    }

    private void initViewPagerAdapter() {
        this.imageGalleryPagerAdapter = new ImageGalleryPagerAdapter(getSupportFragmentManager(), this.selectedEpisode);

        this.galleryViewPager.setOffscreenPageLimit(1);
        this.galleryViewPager.setAdapter(this.imageGalleryPagerAdapter);
        
        this.galleryViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int i) {
                onImageSelected(i);
            }
        });
        
        if (this.galleryViewPager.getCurrentItem() == this.selectedImageIndex) {
        	this.onImageSelected(this.selectedImageIndex);
        } else {
        	this.galleryViewPager.setCurrentItem(this.selectedImageIndex);
        }
        
        final GestureDetector tapGestureDetector = new GestureDetector(this, new TapGestureListener());
        this.galleryViewPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        float marginBetweenPhotos = getResources().getDimension(R.dimen.gallery_margin_between_photos);
        int marginInPixels = (int) marginBetweenPhotos;
        this.galleryViewPager.setPageMargin(marginInPixels);
    }
    
    private void onImageSelected(int i) {
    	this.selectedImageIndex = i;
    	this.changeImageNumberText();
    	this.checkEdgeStatesForNextAndPreviousButtons();
        if (this.imageGalleryAnimationProvider.isFinishedFadingOutPager()) {
        	this.imageGalleryAnimationProvider.fadeInPager();
        }
        AnalyticsEventLogger.logImagedViewedEvent(selectedEpisode, i);
	}
    
    @Override
    public void onStart() {
        super.onStart();
        inactivityHandler.postDelayed(inactivityRunnable, TIME_OF_INACTIVITY_IN_MILLISECONDS);

    }

    @Override
    public void onStop() {
        super.onStop();
        inactivityHandler.removeCallbacks(inactivityRunnable);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(CURRENT_OVERLAY_ALPHA_EXTRA, topOverlay.getAlpha());
        outState.putInt(SELECTED_IMAGE_INDEX, this.selectedImageIndex);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        float alpha = savedInstanceState.getFloat(CURRENT_OVERLAY_ALPHA_EXTRA);
        topOverlay.setAlpha(alpha);
        bottomOverlay.setAlpha(alpha);
        setButtonsClickable(alpha == 1f);
    }
    
    public void onOverlayClick(View view) {
    	this.restartInactivityHandler();
    }

    public void onCloseButtonClick(View view) {
        onBackPressed();
    }

    public void onPreviousClick(View view) {
        restartInactivityHandler();
        currentPagerItem = galleryViewPager.getCurrentItem();
        if (currentPagerItem > 0) {
            imageGalleryAnimationProvider.fadeOutPager(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    galleryViewPager.setCurrentItem(--currentPagerItem, false);
                }
            });
        }
    }

    public void onNextClick(View view) {
        restartInactivityHandler();
        currentPagerItem = galleryViewPager.getCurrentItem();
        if (currentPagerItem < galleryViewPager.getChildCount()) {
            imageGalleryAnimationProvider.fadeOutPager(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    galleryViewPager.setCurrentItem(++currentPagerItem, false);
                }
            });

        }
    }

    private void restartInactivityHandler() {
        inactivityHandler.removeCallbacks(inactivityRunnable);
        inactivityHandler.postDelayed(inactivityRunnable, TIME_OF_INACTIVITY_IN_MILLISECONDS);
    }

    private void fadeOutOverlayIfNeeded() {
        if (topOverlay.getAlpha() != 0f) {
            imageGalleryAnimationProvider.fadeOutOverlay();
            setButtonsClickable(false);
        }
    }
    
    private void setButtonsClickable(boolean isClickable) {
        closeButton.setClickable(isClickable);
        nextButton.setClickable(isClickable);
        previousButton.setClickable(isClickable);
        topOverlay.setClickable(isClickable);
        bottomOverlay.setClickable(isClickable);
    }

    private void changeImageNumberText() {
        imageNumber.setText(String.format("%02d", this.selectedImageIndex + 1));
    }

    private class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            boolean isScreenVisible = topOverlay.getAlpha() == 0f;
            if (isScreenVisible) {
                imageGalleryAnimationProvider.fadeInOverlay();
                restartInactivityHandler();
            } else {
                imageGalleryAnimationProvider.fadeOutOverlay();
            }
            setButtonsClickable(isScreenVisible);
            return true;
        }

    }

    private void checkEdgeStatesForNextAndPreviousButtons() {
        currentPagerItem = galleryViewPager.getCurrentItem();
        previousButton.setText(R.string.previous);
        nextButton.setText(R.string.next);
        previousButton.setEnabled(true);
        nextButton.setEnabled(true);
        if (bottomOverlay.getAlpha() == 1f) {
            nextButton.setClickable(true);
            previousButton.setClickable(true);
        }
        if (currentPagerItem == 0) {
            previousButton.setText("");
            previousButton.setClickable(false);
            previousButton.setEnabled(false);
        } else if (currentPagerItem == galleryViewPager.getAdapter().getCount() - 1) {
            nextButton.setText("");
            nextButton.setClickable(false);
            nextButton.setEnabled(false);
        }
    }

}
