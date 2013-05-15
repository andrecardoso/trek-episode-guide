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
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import com.appglu.trekepisodeguide.R;

public class ImageGalleryAnimationProvider {

    public static final String ALPHA_PROPERTY = "alpha";
    
    private final int duration;

    private View topOverlay;
    private View bottomOverlay;
    private View pager;

    private boolean isFinishedFadingOutPager;

    public boolean isFinishedFadingOutPager() {
        return isFinishedFadingOutPager;
    }

    public ImageGalleryAnimationProvider(Context context, View topOverlay, View bottomOverlay, View pager) {
        this.topOverlay = topOverlay;
        this.bottomOverlay = bottomOverlay;
        this.pager = pager;
        duration = context.getResources().getInteger(R.integer.animation_duration);
        isFinishedFadingOutPager = false;
    }


    public void fadeOutOverlayAndPagerView(Animator.AnimatorListener listener) {
        ObjectAnimator topFadeOutAnimator = ObjectAnimator.ofFloat(topOverlay, ALPHA_PROPERTY, 1f, 0f);
        ObjectAnimator bottomFadeOutAnimator = ObjectAnimator.ofFloat(bottomOverlay, ALPHA_PROPERTY, 1f, 0f);
        ObjectAnimator pagerFadeOutAnimator = ObjectAnimator.ofFloat(pager, ALPHA_PROPERTY, 1f, 0f);
        ObjectAnimator[] objectAnimators = new ObjectAnimator[]{topFadeOutAnimator, bottomFadeOutAnimator, pagerFadeOutAnimator};
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimators);
        animatorSet.setDuration(duration);
        animatorSet.addListener(listener);
        animatorSet.start();
    }

    public void fadeOutPager(Animator.AnimatorListener animatorListener) {
        ObjectAnimator pagerFadeOutAnimator = ObjectAnimator.ofFloat(pager, ALPHA_PROPERTY, 1f, 0f);
        pagerFadeOutAnimator.setDuration(duration);
        pagerFadeOutAnimator.addListener(animatorListener);
        pagerFadeOutAnimator.start();
        isFinishedFadingOutPager = true;
    }

    public void fadeInPager() {
        ObjectAnimator pagerFadeInAnimator = ObjectAnimator.ofFloat(pager, ALPHA_PROPERTY, 0f, 1f);
        pagerFadeInAnimator.setDuration(duration);
        pagerFadeInAnimator.start();
        isFinishedFadingOutPager = false;
    }

    public void fadeOutOverlay() {
        ObjectAnimator topFadeOutAnimator = ObjectAnimator.ofFloat(topOverlay, ALPHA_PROPERTY, 1f, 0f);
        ObjectAnimator bottomFadeOutAnimator = ObjectAnimator.ofFloat(bottomOverlay, ALPHA_PROPERTY, 1f, 0f);
        ObjectAnimator[] objectAnimators = new ObjectAnimator[]{topFadeOutAnimator, bottomFadeOutAnimator};
        animateSet(objectAnimators);
    }

    public void fadeInOverlay() {
        ObjectAnimator topFadeOutAnimator = ObjectAnimator.ofFloat(topOverlay, ALPHA_PROPERTY, 0f, 1f);
        ObjectAnimator bottomFadeOutAnimator = ObjectAnimator.ofFloat(bottomOverlay, ALPHA_PROPERTY, 0f, 1f);
        ObjectAnimator[] objectAnimators = new ObjectAnimator[]{topFadeOutAnimator, bottomFadeOutAnimator};
        animateSet(objectAnimators);
    }

    public void animateSet(ObjectAnimator[] objectAnimators) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimators);
        animatorSet.setDuration(duration);
        animatorSet.start();
    }


}
