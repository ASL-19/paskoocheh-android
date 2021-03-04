package org.asl19.paskoocheh;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class OuinetBroadcastReceiver extends BroadcastReceiver {
    // The value constants also force us to use
    // the right type check for the extras bundle.
    public static final String EXTRA_ACTION_STOP = "org.asl19.paskoocheh.OuinetBroadcastReceiver.STOP";
    public static final String EXTRA_ACTION_PURGE = "org.asl19.paskoocheh.OuinetBroadcastReceiver.PURGE";
    private static final String TAG = "OuinetBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received intent: "+ intent +", shutting down Ouinet service");
        boolean doStop = intent.hasExtra(EXTRA_ACTION_STOP);
        boolean doPurge = intent.hasExtra(EXTRA_ACTION_PURGE);

        if (!doStop) {
            return;  // purging only is not allowed
        }

        killPackageProcesses(context);
        if (doPurge) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    am.clearApplicationUserData();
                }
            }
        }
        Process.killProcess(Process.myPid());
    }

    public static Intent createStopIntent(Context context) {
        Intent intent = new Intent(context, OuinetBroadcastReceiver.class);
        intent.putExtra(EXTRA_ACTION_STOP, 1);
        return intent;
    }

    public static Intent createPurgeIntent(Context context) {
        Intent intent = createStopIntent(context);
        intent.putExtra(EXTRA_ACTION_PURGE, 1);
        return intent;
    }

    private void killPackageProcesses(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return;
        }
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        if (processes == null) {
            return;
        }
        int myPid = Process.myPid();
        String thisPkg = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (process.pid == myPid || process.pkgList == null) {
                // Current process will be killed last
                continue;
            }

            List<String> pkgs = Arrays.asList(process.pkgList);
            if (pkgs.contains(thisPkg)) {
                Log.i(TAG, "Killing process: " + process.processName + " (" + process.pid + ")");
                Process.killProcess(process.pid);
            }
        }
    }
}