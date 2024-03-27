package org.asl19.paskoocheh;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.asl19.paskoocheh.p2pnetwork.P2PActivity;

import ie.equalit.ouinet.Config;
import ie.equalit.ouinet.Ouinet;

public class OuinetService extends Service {
    public static final String PROXY_HOST = "127.0.0.1";
    public static final int PROXY_PORT = 8077;

    private static final String TAG = "OuinetService";
    public static final String CONFIG_EXTRA = "config";
    public static final String CONFIG_VERSION_EXTRA = "config-version";
    private static final String SHOW_PURGE_EXTRA = "show-purge";
    private static final String HIDE_PURGE_EXTRA = "hide-purge";
    public static final String ACTION_OUINET_STATUS_BROADCAST = "org.asl19.paskoocheh.ACTION_SEND_OUINET_STATUS";
    public static final String EXTRA_IS_OUINET_START_SUCCESSFUL = "EXTRA_IS_OUINET_START_SUCCESSFUL";
    public static final String EXTRA_IS_OUINET_STOP_COMPLETE = "EXTRA_IS_OUINET_STOP_COMPLETE";
    public static final String EXTRA_STOP_SERVICE_COMMAND = "EXTRA_STOP_SERVICE_COMMAND";

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ouinet-notification-channel";

    private static final int MAX_OUINET_START_WAIT_IN_MILLISECS = 90000; // Wait upto 1 minute 30 secs.
    private static final int MAX_OUINET_STOP_WAIT_IN_MILLISECS = 12000; // Wait upto 12 secs. On an average stop is taking about 5 seconds.
    private static final int SLEEP_INTERVAL_MILLISECS = 100;

    private Ouinet mOuinet;

    private volatile ServiceHandler mServiceHandler;
    private volatile Looper mServiceLooper;
    private boolean mRedelivery;

    private int lastConfigVersion = -1;
    private volatile boolean initiatedServiceDestruction = false;
    private volatile boolean onDestroyCalledByAndroid = false;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Intent intent = (Intent)msg.obj;
            handleIntent(intent);
        }
    }

    private void handleIntent(Intent intent) {
        boolean isStop = intent.getBooleanExtra(EXTRA_STOP_SERVICE_COMMAND, false);
        if (isStop) {
            Log.d(TAG, "handleIntent - Intent to stop the Ouinet and destroy the OuinetService has arrived");
            stopOuinet(true, true, false);
            return;
        }

        if (intent.hasExtra(HIDE_PURGE_EXTRA)) {
            Log.d(TAG, "handleIntent - Hiding purge action, intent:" + intent);
            startForeground(NOTIFICATION_ID, createNotification(false));
            return;
        }

        if (intent.hasExtra(SHOW_PURGE_EXTRA)) {
            Log.d(TAG, "handleIntent - Showing purge action, intent:" + intent);
            startForeground(NOTIFICATION_ID, createNotification(true));

            // Show notification without purge action after some time.
            PendingIntent hidePurgePIntent = PendingIntent.getService(OuinetService.this, 0,
                    createHidePurgeIntent(OuinetService.this),
                    PendingIntent.FLAG_CANCEL_CURRENT);
            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        hidePurgePIntent.send();
                    } catch (PendingIntent.CanceledException ce) {
                    }
                }
            }, 3000);

            return;
        }

        if (!intent.hasExtra(CONFIG_VERSION_EXTRA)) {
            throw new IllegalArgumentException("Service intent missing config-version extra");
        }

        int configVersion = intent.getIntExtra(CONFIG_VERSION_EXTRA, -1);

        if (configVersion <= lastConfigVersion) {
            Log.d(TAG, "handleIntent - Service ignoring received old (duplicate?) config version");
            return;
        }

        lastConfigVersion = configVersion;

        Log.d(TAG, "Service starting, intent:" + intent);
        if (!intent.hasExtra(CONFIG_EXTRA)) {
            throw new IllegalArgumentException("Service intent missing config extra");
        }

        Config config = intent.getParcelableExtra(CONFIG_EXTRA);

        startForeground(NOTIFICATION_ID, createNotification(false));
        startOuinet(config);
        return;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("OuinetService");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        Log.d(TAG, "OuinetService created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }

    @WorkerThread
    private void startOuinet(Config config) {
        if (mOuinet != null) {
            Log.d(TAG, "startOuinet - Stopping and destroying existing running instance of Ouinet");
            stopOuinet(false, false, false);
        }

        mOuinet = new Ouinet(this, config);
        Ouinet ouinet = mOuinet;
        ouinet.start();

        int timeToStart = 0;
        boolean startSuccess = true;

        Ouinet.RunningState state = ouinet.getState();

        Log.d(TAG, "startOuinet - Created and started new instance of Ouinet. Will now loop to check the status of Ouinet");

        // Loop for MAX_OUINET_START_WAIT_IN_MILLISECS or until the Ouinet state turns to Started/Degraded/Failed, which ever happens earliest.
        while(!onDestroyCalledByAndroid && mOuinet != null && state != Ouinet.RunningState.Started) {
            try {
                if (timeToStart >= MAX_OUINET_START_WAIT_IN_MILLISECS ||
                        state == Ouinet.RunningState.Degraded ||
                        state == Ouinet.RunningState.Failed) {
                    startSuccess = false;
                    break;
                }
                timeToStart = timeToStart + SLEEP_INTERVAL_MILLISECS;
                Thread.sleep(SLEEP_INTERVAL_MILLISECS);
                Log.d(TAG, "startOuinet - Ouinet Status = " + ouinet.getState());
                state = ouinet.getState();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG, "startOuinet - Thread interrupted. Ouinet Status = " + ouinet.getState());
                startSuccess = false;
            }
        }

        Log.d(TAG, "startOuinet - Total time (in milli-secs) to complete Ouinet.start call = " + timeToStart);

        if (startSuccess) {
            Log.d(TAG, "startOuinet - Ouinet started successfully");
            sendOuinetStartCompletionStatus(true);
        } else {
            Log.e(TAG, "startOuinet - Ouinet started un-successfully. So stopping the ouinet instance");
            stopOuinet(false, true, true);
        }
    }

    @WorkerThread
    private void stopOuinet(boolean processingStopIntent, boolean destroyServiceAfterStop, boolean stopCalledAfterUnsuccessfulStart) {
        if (mOuinet == null) {
            Log.e(TAG, "stopOuinet - ouinet instance is null so return");
            if (processingStopIntent) {
                sendStopCompletionIntent();
            }
            return;
        }

        initiatedServiceDestruction = true;
        Ouinet ouinet = mOuinet;
        mOuinet = null;

        new Thread(() -> {

            int timeToStop = 0;
            while(timeToStop < MAX_OUINET_STOP_WAIT_IN_MILLISECS && ouinet.getState() != Ouinet.RunningState.Stopped) {
                Log.d(TAG, "stopOuinet - Ouinet Status = " + ouinet.getState());
                try {
                    Thread.sleep(SLEEP_INTERVAL_MILLISECS);
                    timeToStop = timeToStop + SLEEP_INTERVAL_MILLISECS;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(TAG, "stopOuinet - Thread interrupted. Ouinet Status = " + ouinet.getState());
                }
            }

            // If ever this error message is printed, immediately report the bug to the eQualite team - this was decided as per meeting with eQualite (Ivan and Peter) on 13th Oct 2021, 11:30 AM EST.
            if (ouinet.getState() != Ouinet.RunningState.Stopped) {
                FirebaseCrashlytics.getInstance().log("OuinetService:stopOuinet - Cannot stop Ouinet in millisecs = "
                        + MAX_OUINET_STOP_WAIT_IN_MILLISECS
                        + ". Report the bug to the eQualite Ouinet team. Current stop status=" + ouinet.getState());
            } else {
                Log.d(TAG, "stopOuinet - Stopped Ouinet successfully in millisecs = " + timeToStop);
            }

            if (stopCalledAfterUnsuccessfulStart) {
                Log.d(TAG, "stopOuinet - Ouinet start not successful. Stopped the Ouinet. Send back broadcast to the fragment/activity that original sent the start intent");
                sendOuinetStartCompletionStatus(false);
            } else if(processingStopIntent) {
                Log.d(TAG, "stopOuinet - Stop complete. Send back broadcast to the fragment/activity that original sent the stop intent");
                sendStopCompletionIntent();
            }

            if (destroyServiceAfterStop) {
                Log.d(TAG, "stopOuinet - Stop and destroy the OuinetService");
                this.stopSelf();
            }
        }).start();

        Log.d(TAG, "stopOuinet - Calling Ouinet.stop()");
        ouinet.stop();
    }

    private void sendStopCompletionIntent() {
        Log.d(TAG, "sendStopCompleteIntent - Send back broadcast to the fragment/activity that original sent the stop intent");
        Intent intent = new Intent(ACTION_OUINET_STATUS_BROADCAST);
        intent.putExtra(EXTRA_IS_OUINET_STOP_COMPLETE, true);
        sendBroadcast(intent);
    }

    private void sendOuinetStartCompletionStatus(boolean success) {
        Log.d(TAG, "sendStartCompleteStatus - sending ouinet start status back to activity/fragment. isStartSuccess = " + success);
        Intent intent = new Intent(ACTION_OUINET_STATUS_BROADCAST);
        intent.putExtra(EXTRA_IS_OUINET_START_SUCCESSFUL, success);
        sendBroadcast(intent);
    }

    private Intent createHomeIntent(Context context) {
        // Intent characteristics from `PaskoochehApp.launchOrBringToFront`.
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://paskoocheh.com"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //intent.setClassName(Constants.PASKOOCHEH_PACKAGE,OuinetService.class.getCanonicalName());
        return intent;
    }

    private Intent createShowPurgeIntent(Context context) {
        Intent intent = new Intent(context, OuinetService.class);
        intent.putExtra(SHOW_PURGE_EXTRA, 1);
        return intent;
    }

    private Intent createHidePurgeIntent(Context context) {
        Intent intent = new Intent(context, OuinetService.class);
        intent.putExtra(HIDE_PURGE_EXTRA, 1);
        return intent;
    }

    @SuppressLint("NewApi")
    private Notification createNotification(boolean showRealPurgeAction) {
        String channel_id = CHANNEL_ID;

            // Create a notification channel for Ouinet notifications. Recreating a notification
            // that already exists has no effect.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.about_paskoocheh),
                    NotificationManager.IMPORTANCE_LOW);
            channel_id = channel.getId();
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        int requestCode = 0;
        PendingIntent p2pIntent = PendingIntent.getActivity(this, requestCode++,
                new Intent(this, P2PActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent homePIntent = PendingIntent.getActivity(this, requestCode++,
                createHomeIntent(this),
                PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent showPurgePIntent = PendingIntent.getService(this, requestCode++,
                createShowPurgeIntent(this),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifb = new NotificationCompat.Builder(this, channel_id)
                .setSmallIcon(R.drawable.notification_paskoocheh)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tap_to_stop))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(p2pIntent)
                .setAutoCancel(true);// Close on tap.
        //Commentint out panic buttons
//                .addAction(R.drawable.ic_folder_icon,
//                        getString(R.string.title_about),
//                        homePIntent)
//                .addAction(R.drawable.ic_check,
//                        getString(R.string.yes),
//                        showPurgePIntent);

        if (showRealPurgeAction) {
            PendingIntent purgePIntent = PendingIntent.getBroadcast(this, requestCode++,
                    OuinetBroadcastReceiver.createPurgeIntent(this),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notifb.addAction(R.drawable.ic_check,
                    getString(R.string.yes),
                    purgePIntent);
        }

        return notifb.build();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        onDestroyCalledByAndroid = true;

        if (mOuinet != null && !initiatedServiceDestruction) {
            // The probablility of a Foreground service like this one being killed by the Android OS is extremely low;
            // but it does happen in extreme situations where memory is needed elsewhere.
            // Need to run in a new thread since Ouinet.stop() doesn't create a new thread.
            Log.d(TAG, "onDestroy - The Ouinet is still running. Will stop it now");
            new Thread(() -> {
                stopOuinet(false, false, false);
            }).start();
        } else {
            Log.d(TAG, "onDestroy - The Ouinet was already stopped before this onDestroy call");
        }

        mServiceLooper.quit(); // clear all the intents queued.
        Log.d(TAG, "onDestroy - end of onDestroy");
    }
}
