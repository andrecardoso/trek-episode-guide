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

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.appglu.android.AppGlu;
import com.appglu.android.task.AsyncTaskExecutor;
import com.appglu.android.task.ImageViewAsyncCallback;
import com.appglu.impl.util.StringUtils;
import com.appglu.trekepisodeguide.R;
import com.appglu.trekepisodeguide.ui.AbstractTrekFragment;

public class ImageGalleryItemFragment extends AbstractTrekFragment {
	
	private static final Executor SERIAL_QUEUE_EXECUTOR = AsyncTaskExecutor.createSerialQueueExecutor(3);
	
	private static final String IMAGE_BITMAP_PARCELABLE_EXTRA = "ImageGalleryItemFragment.IMAGE_BITMAP_PARCELABLE_EXTRA";
    public static final String IMAGE_URL_EXTRA = "ImageGalleryItemFragment.IMAGE_URL_EXTRA";

    private String urlToDownLoad;

    private ImageView imageView;
    private ProgressBar progressBar;
    private View placeholderView;
    
    private Bitmap imageBitmap;

    public ImageGalleryItemFragment() {
    	
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(IMAGE_URL_EXTRA)) {
            this.urlToDownLoad = getArguments().getString(IMAGE_URL_EXTRA);
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putParcelable(IMAGE_BITMAP_PARCELABLE_EXTRA, imageBitmap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View view = inflater.inflate(R.layout.image_gallery_item_fragment, null);
        
        this.imageView = (ImageView) view.findViewById(R.id.image_view);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        this.placeholderView = (View) view.findViewById(R.id.placeholder_view);
        
        if (savedState == null || savedState.getParcelable(IMAGE_BITMAP_PARCELABLE_EXTRA) == null) {
        	this.downloadToImageViewInBackground();
        } else {
        	this.imageBitmap = savedState.getParcelable(IMAGE_BITMAP_PARCELABLE_EXTRA);
        	this.imageView.setImageBitmap(this.imageBitmap);
        }
        
        return view;
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	this.imageBitmap = null;
    }

    private void downloadToImageViewInBackground() {
    	if (StringUtils.isEmpty(urlToDownLoad)) {
    		progressBar.setVisibility(View.GONE);
    		placeholderView.setVisibility(View.VISIBLE);
    		return;
    	}
    	
        ImageViewAsyncCallback.DefaultImageDownloadListener imageListener = new ImageViewAsyncCallback.DefaultImageDownloadListener(imageView, progressBar, placeholderView) {

            @Override
            public void onImageLoaded(Bitmap bitmap) {
            	imageBitmap = bitmap;
                super.onImageLoaded(bitmap);
                fadeInThumbnail();
            }
            
        };
        
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        final int smallerSize = displayMetrics.heightPixels < displayMetrics.widthPixels ? displayMetrics.heightPixels : displayMetrics.widthPixels;
        
        ImageViewAsyncCallback imageViewCallback = new ImageViewAsyncCallback(imageListener);
		
		AsyncTaskExecutor executor = new AsyncTaskExecutor(SERIAL_QUEUE_EXECUTOR, false);
		executor.execute(imageViewCallback, new Callable<Bitmap>() {
			public Bitmap call() throws Exception {
				return AppGlu.storageApi().downloadAsBitmap(urlToDownLoad, smallerSize, smallerSize);
			}
		});
    }

    private void fadeInThumbnail() {
        Activity activity = getActivity();
        if (activity != null) {
            Animation fadeInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
            imageView.startAnimation(fadeInAnimation);
        }
    }
    
}
