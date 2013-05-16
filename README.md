Trek Episode Guide
==================

The Trek Episode Guide is an Android sample app that shows of the capabilities of the AppGlu platform. The app is also available in the Google Play store.

To learn more about AppGlu visit us at http://appglu.com, email sales@appglu.com, or call 1-415-457-1111.

Follow the steps below to get the app running:

1. [Download](https://github.com/appglu/trek-episode-guide/archive/master.zip) the source code.
2. Make sure you have the [Android SDK](http://developer.android.com/sdk/index.html) installed (version 4.0+ Api Level 14).
3. Open the project in Eclipse or your favorite IDE.
3. Set up the project to use the API key/secret of your private AppGlu app.
    1. Under the `res/values` folder of the project open the file `environment.xml`.
    2. Replace the API key/secret placeholders with the information of your own private AppGlu environment.
4. Run the app in a device or emulator.

The app should automatically synchronize with AppGlu at startup. Content changes you make in AppGlu will get automatically synchronized everytime the app is launched. Analytics are captured and submitted to AppGlu everytime the app closes. The app also registers at GCM (Google Cloud Message) to receive push notifications. You can experiment sending notification messages to your own devices after you have it integrated with your instance of the Trek AppGlu demo environment. 

Below we highlight the integration points between the app and the AppGlu SDK. For more information check out the [Android SDK Integration Guide](https://github.com/appglu/appglu-androidsdk/wiki/Android-SDK-Integration-Guide), the SDK reference [Javadocs](http://appglu.github.com/appglu-androidsdk/javadoc/index.html), or the [AppGlu Documentation Page](http://docs.appglu.com).

### SDK initialization

The SDK is initialized in the `onCreate()` method of the `TrekApplication` class (application main class). There we setup the API key and secret (which is read from the XML file mentioned above) and also provide an instance of the `DatabaseHelper` that is used to configure the database.  

The `TrekDatabaseHelper` is a subclass of `SyncDatabaseHelper`, which in turn is a subclass of `SQLiteOpenHelper` that developers are familiar with when using SQLite databases. The subclass of `SyncDatabaseHelper` is very similar to subclasses of `SQLiteOpenHelper`, the only exception is that the `onCreate()` method is called `onCreateAppDatabase()` and the `onUpgrade()` method is called `onUpgradeAppDatabase()`.

### Content synchronization  

The database synchronization gets triggered in the `startAppGluSync()` method of `SplashActivity`, the app's first activity. The synchronization progress is monitored by `syncListener`, which is also created and registered in the same class. Once the synchronization is completed the app proceeds to the next activity.

```java
// Synchronization is started
AppGlu.syncApi().startSyncIntentService(SyncIntentServiceRequest.syncDatabase());  
    
// Register listener for sync progress events
AppGlu.syncApi().registerSyncListener(syncListener);
```

When the synchronization listener `onFinish()` callback gets called the database is ready to be used. In this app, after the sync finishes, we load the list of episodes in an async task:

```java
// Handle sychronization progress events
private SyncListener syncListener = new SyncListener() {

    public void onFinish(boolean wasSuccessful) {
        ...
        
        LoadEpisodesAsyncTask task = new LoadEpisodesAsyncTask();
        task.execute();
    }

};
```

The synchronization has the option to synchronize just the tables or the tables and files. In this sample app we're synchronizing just the tables, the files are downloaded on-the-fly as they're displayed. The SDK provides helper methods to assist on the download of the files and keep them in a local cache.

In the `EpisodeListAdapter` class we make a call to `AppGlu.storageApi().retrieveBitmapFromCacheManager()` to check if the image is in cache, if it's not we add it to a queue that processes the download of the files by making a call to `AppGlu.storageApi().downloadAsBitmap()`.


### Analytics

The Android SDK provides two ways to manage analytics sessions lifecycle and make sure sessions are kept alive as the user navigates through the app's activities. In this app we use the more convinient approach of extending fragments from the `AppGluAnalyticsFragmentActivity` base class that already takes care of that.  

Analytics events are logged in the `AnalyticsEventLogger` class, which acts as a Facade for all the other classes that trigger analytics events and simplifies the reuse and maintenance of events names and properties.

The example below shows the event logged when an episode is viewed:

```java
public static void logEpisodeViewedEvent(Episode episode) {
    AnalyticsSessionEvent event = new AnalyticsSessionEvent("episode.viewed");
    event.addParameter("episode.id", episode.getCode());
        
    AppGlu.analyticsApi().logEvent(event);
}
```

### Push Notifications

The app registers for push notification at startup, when the `onCreate` method of Application class is called. By calling the `registerForPushNotifications()` method the SDK automatically registers in the GCM server and stores the device identifier in AppGlu. 

```java
String gcmSenderId = this.getResources().getString(R.string.gcm_sender_id);
AppGlu.pushApi().registerForPushNotifications(this, gcmSenderId);
```

Messages received are handled by a custom subclass of the `GCMBaseIntentService` class, similar to the way it's explained in the [GCM Getting Started](http://developer.android.com/google/gcm/gs.html) docs. 
The AppGlu SDK has a convenient subclass called `AppGluGCMBaseIntentService` that handles things like device identifier registration and provides the `onNotificationReceived()` callback with easy access to the message payload.

```java
public class GCMIntentService extends AppGluGCMBaseIntentService {
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
            
        ...
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	notificationManager.notify(NOTIFICATION_ID, builder.getNotification());
    }
}
```


