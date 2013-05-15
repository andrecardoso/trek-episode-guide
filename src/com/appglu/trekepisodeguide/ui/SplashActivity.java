package com.appglu.trekepisodeguide.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appglu.android.AppGlu;
import com.appglu.android.sync.SyncExceptionWrapper;
import com.appglu.android.sync.SyncIntentServiceRequest;
import com.appglu.android.sync.SyncListener;
import com.appglu.android.task.AppGluAsyncTask;
import com.appglu.trekepisodeguide.R;
import com.appglu.trekepisodeguide.model.EpisodeListOrder;
import com.appglu.trekepisodeguide.ui.detail.EpisodeDetailActivity;
import com.appglu.trekepisodeguide.ui.list.EpisodeListActivity;

import de.greenrobot.event.EventBus;

public class SplashActivity extends AbstractTrekActivity {
	
	public static final String INITIAL_EPISODE_CODE_STRING_EXTRA = "SplashActivity.INITIAL_EPISODE_CODE_STRING_EXTRA";
	
	private static final int NUMBER_OF_SPLASH_IMAGES_ON_ANIMATION = 5;
	private static final int NUMBER_OF_LOOPS_ON_ANIMATION = 2;
	
	private static final String DATABASE_SYNCED_BEFORE_PREFERENCES_KEY = "SplashActivity.DATABASE_SYNCED_BEFORE_PREFERENCES_KEY";

	private static final String SYNC_COMPLETE_BOOLEAN_EXTRA = "SplashActivity.SYNC_COMPLETE_BOOLEAN_EXTRA";
	private static final String SYNC_SUCCESS_BOOLEAN_EXTRA = "SplashActivity.SYNC_SUCCESS_BOOLEAN_EXTRA";
	private static final String MINIMUN_DISPLAY_TIME_ELAPSED_BOOLEAN_EXTRA = "SplashActivity.MINIMUN_DISPLAY_TIME_ELAPSED_BOOLEAN_EXTRA";
	private static final String SHOWING_ERROR_SCREEN_BOOLEAN_EXTRA = "SplashActivity.SHOWING_ERROR_SCREEN_BOOLEAN_EXTRA";
	
	private int minimunTimeToDisplayInMilliseconds;

    private boolean syncComplete = false;
    private boolean syncSuccess = false;
    private boolean minimumDisplayTimeElapsed = false;
    private boolean showErrorScreen = false;
    
    private TextView noConnectionTextView;
    private TextView retryLaterTextView;
    
    private ImageView contentImageBackground;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.splash_activity);
        
        int durationOfLoop = NUMBER_OF_SPLASH_IMAGES_ON_ANIMATION * getResources().getInteger(R.integer.splash_animation_duration);
		this.minimunTimeToDisplayInMilliseconds = durationOfLoop * NUMBER_OF_LOOPS_ON_ANIMATION;
        
		this.noConnectionTextView = (TextView) findViewById(R.id.no_connetion_text);
		this.retryLaterTextView = (TextView) findViewById(R.id.retry_later);
		
        this.contentImageBackground = (ImageView) findViewById(R.id.status_image_background);
        
        EventBus.getDefault().register(this);
        
        // Registers listener for synchronization events
        AppGlu.syncApi().registerSyncListener(syncListener);
        
        if (savedInstanceState == null) {
        	this.startAppGluSync();
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	outState.putBoolean(SYNC_COMPLETE_BOOLEAN_EXTRA, this.syncComplete);
    	outState.putBoolean(SYNC_SUCCESS_BOOLEAN_EXTRA, this.syncSuccess);
    	outState.putBoolean(MINIMUN_DISPLAY_TIME_ELAPSED_BOOLEAN_EXTRA, this.minimumDisplayTimeElapsed);
    	outState.putBoolean(SHOWING_ERROR_SCREEN_BOOLEAN_EXTRA, this.showErrorScreen);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	if (savedInstanceState == null) {
            return;
        }
    	this.syncComplete = savedInstanceState.getBoolean(SYNC_COMPLETE_BOOLEAN_EXTRA);
    	this.syncSuccess = savedInstanceState.getBoolean(SYNC_SUCCESS_BOOLEAN_EXTRA);
    	this.minimumDisplayTimeElapsed = savedInstanceState.getBoolean(MINIMUN_DISPLAY_TIME_ELAPSED_BOOLEAN_EXTRA);
    	this.showErrorScreen = savedInstanceState.getBoolean(SHOWING_ERROR_SCREEN_BOOLEAN_EXTRA);
    	
    	if (this.showErrorScreen) {
        	this.showErrorScreen();
        }
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	if (this.showErrorScreen && AppGlu.hasInternetConnection()) {
    		this.startAppGluSync();
    	}
    }

    private void startAppGluSync() {
    	this.showSyncingScreen();
    	
		this.handler.postDelayed(activityMinimumTimeWaiter, this.minimunTimeToDisplayInMilliseconds);
		
		// Starts content synchronization
		AppGlu.syncApi().startSyncIntentService(SyncIntentServiceRequest.syncDatabase());
	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	EventBus.getDefault().unregister(this);
    	
    	// Unregister synchronization listener 
    	AppGlu.syncApi().unregisterSyncListener(syncListener);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	this.startAnimation();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	this.stopAnimation();
    }
    
    @Override
    public void onBackPressed() {
    	Intent intent = new Intent(this, HomeActivity.class);
    	intent.putExtra(HomeActivity.SHOULD_CLOSE_APPLICATION_BOOLEAN_EXTRA, true);
    	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	this.startActivity(intent);
    	
    	super.onBackPressed();
    }
    
    private void startAnimation() {
		AnimationDrawable drawable = (AnimationDrawable) contentImageBackground.getBackground();
        drawable.start();
	}
    
    private void stopAnimation() {
		AnimationDrawable drawable = (AnimationDrawable) contentImageBackground.getBackground();
        drawable.stop();
	}
    
    public void onEventMainThread(MinimumTimerElapsedEvent event) {
    	minimumDisplayTimeElapsed = true;
    	if (syncComplete) {
    		syncComplete();
    	}
    }

    private void syncComplete() {
    	this.stopAnimation();
    	
    	this.handler.removeCallbacks(this.activityMinimumTimeWaiter);
    	
    	if (syncSuccess) {
    		this.saveDatabaseWasSyncedWithSuccess();
    	} else {
    		 if (!this.isDatabaseSyncedBefore()) {
	        	this.showErrorScreen();
	        	return;
	        }
    	}
    	
    	String initialEpisodeCode = getIntent().getStringExtra(INITIAL_EPISODE_CODE_STRING_EXTRA);
    	
    	Integer initialEpisodeId = null;
    	if (initialEpisodeCode != null) {
    		initialEpisodeId = getTrekService().findIdForEpisodeCode(initialEpisodeCode);
    	}
    	
    	if (initialEpisodeId == null) {
    		Intent episodeListIntent = new Intent(this, EpisodeListActivity.class);
    		this.startActivity(episodeListIntent);
    	} else {
    		if (this.isLargeScreen()) {
    			Intent episodeListIntent = new Intent(this, EpisodeListActivity.class);
    			episodeListIntent.putExtra(EpisodeListActivity.INITIAL_EPISODE_ID_INTEGER_EXTRA, initialEpisodeId);
        		this.startActivity(episodeListIntent);
    		} else {
    			Intent episodeDetailIntent = new Intent(this, EpisodeDetailActivity.class);
        		episodeDetailIntent.putExtra(EpisodeDetailActivity.EPISODE_ID_INTEGER_EXTRA, initialEpisodeId);
        		episodeDetailIntent.putExtra(EpisodeDetailActivity.EPISODE_LIST_ORDER_EXTRA, EpisodeListOrder.TITLE);
        		this.startActivity(episodeDetailIntent);
    		}
    	}
    }
    
    private void showSyncingScreen() {
    	this.showErrorScreen = false;
    	
    	noConnectionTextView.setVisibility(View.GONE);
    	retryLaterTextView.setVisibility(View.GONE);
    	
        contentImageBackground.setBackgroundResource(R.drawable.splash_syncing_animation);
        this.startAnimation();
        
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_progress);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showErrorScreen() {
    	this.showErrorScreen = true;
    	
    	if (this.isLargeScreen()) {
    		noConnectionTextView.setText(R.string.no_connection_retry_later);
    		noConnectionTextView.setVisibility(View.VISIBLE);
    	} else {
	    	noConnectionTextView.setVisibility(View.VISIBLE);
	    	retryLaterTextView.setVisibility(View.VISIBLE);
    	}
    	
        contentImageBackground.setBackgroundResource(R.drawable.splash_error_animation);
        this.startAnimation();
        
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading_progress);
        progressBar.setVisibility(View.GONE);
    }
    
    public void onClickVisitMemoryAlpha(View view) {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://en.memory-alpha.org/wiki/Portal:Main"));
    	this.startActivity(browserIntent);
    }
    
    public void onClickVisitAppGlu(View view) {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://appglu.com"));
    	this.startActivity(browserIntent);
    }
    
    public void onClickVisitArcTouch(View view) {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://arctouch.com"));
    	this.startActivity(browserIntent);
    }
    
    public boolean isDatabaseSyncedBefore() {
        SharedPreferences preferences = this.getSharedPreferences(this);
        return preferences.getBoolean(DATABASE_SYNCED_BEFORE_PREFERENCES_KEY, false);
    }

    public boolean saveDatabaseWasSyncedWithSuccess() {
        SharedPreferences.Editor editor = getSharedPreferences(this).edit();
        editor.putBoolean(DATABASE_SYNCED_BEFORE_PREFERENCES_KEY, true);
        return editor.commit();
    }

    public SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SyncListener syncListener = new SyncListener() {

        @Override
        public void onPreExecute() {
            syncComplete = false;
        }

        @Override
        public void onResult(boolean changesWereApplied) {
            logger.info("Synchronization finished with new changes: " + changesWereApplied);
        }

        @Override
        public void onException(SyncExceptionWrapper exceptionWrapper) {
            logger.error(exceptionWrapper.getException());
        }

        @Override
        public void onNoInternetConnection() {
            this.onFinish(false);
        }
        
        public void onFinish(boolean wasSuccessful) {
        	syncSuccess = wasSuccessful;
        	
        	LoadEpisodesAsyncTask task = new LoadEpisodesAsyncTask();
        	task.execute();
        }

    };
    
    private class LoadEpisodesAsyncTask extends AppGluAsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doExecuteInBackground(Void... params) throws Exception {
			getTrekService().loadAllEpisodes();
			return null;
		}

		@Override
		protected void onResult(Void result) {
			
		}
		
		@Override
		protected void onFinished(boolean wasSuccessful) {
			syncComplete = true;
			
			boolean syncFailureAndDatabaseNeverSynced = !syncSuccess && !isDatabaseSyncedBefore();
        	
			if (syncFailureAndDatabaseNeverSynced || minimumDisplayTimeElapsed) {
                syncComplete();
            }
		}
		
	}

    private Runnable activityMinimumTimeWaiter = new Runnable() {
        @Override
        public void run() {
        	EventBus.getDefault().post(new MinimumTimerElapsedEvent());
        }
    };
    
    private class MinimumTimerElapsedEvent { 
    	//used for publishing and listening for timer elapsed event
    }
    
}
