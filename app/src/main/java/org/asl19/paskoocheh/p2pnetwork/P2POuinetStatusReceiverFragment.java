package org.asl19.paskoocheh.p2pnetwork;

import static org.asl19.paskoocheh.Constants.OUINET_PREF;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import org.asl19.paskoocheh.OuinetService;

public abstract class P2POuinetStatusReceiverFragment extends Fragment {
    private static final String LOGTAG = "P2POuinetStatusReceiver";

    private OuinetStatusReceiver mOuinetStatusReceiver;

    public P2POuinetStatusReceiverFragment() {
    }

    protected abstract void onOuinetStatusUpdate(boolean operationSuccessful); // Will be status of whatever current operation user performed i.e. start or stop of ouinet.

    public class OuinetStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean startSuccess =
                    intent.getBooleanExtra(OuinetService.EXTRA_IS_OUINET_START_SUCCESSFUL, false);
            boolean stopComplete =
                    intent.getBooleanExtra(OuinetService.EXTRA_IS_OUINET_STOP_COMPLETE, false);
            Log.d(LOGTAG, "onReceive: startSuccess = " + startSuccess + "; stopComplete = " + stopComplete);
            onOuinetStatusUpdate(startSuccess || stopComplete);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOuinetStatusReceiver = new OuinetStatusReceiver();
        getActivity().registerReceiver(mOuinetStatusReceiver, new IntentFilter(OuinetService.ACTION_OUINET_STATUS_BROADCAST));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mOuinetStatusReceiver);
    }
}