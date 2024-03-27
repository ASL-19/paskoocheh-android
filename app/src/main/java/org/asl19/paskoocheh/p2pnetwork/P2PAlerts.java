package org.asl19.paskoocheh.p2pnetwork;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.dialogs.PaskoochehDialog;

public class P2PAlerts {
    private static final String LOGTAG = "P2PAlerts";

    PaskoochehDialog dialogP2PConnecting, dialogP2PConnectSuccess, dialogP2PConnectFail;
    PaskoochehDialog dialogP2PDisconnecting;
    PaskoochehDialog dialogAppNotAvailableInP2PCache;

    public P2PAlerts(Context context) {
        dialogP2PConnecting = new PaskoochehDialog(context.getString(R.string.p2p_connecting_alert_title), context.getColor(android.R.color.black),
                context.getString(R.string.p2p_connecting_alert_description), true);
        dialogP2PDisconnecting = new PaskoochehDialog(context.getString(R.string.p2p_disconnecting_alert_title), context.getColor(android.R.color.black),
                context.getString(R.string.p2p_disconnecting_alert_description), true);
        dialogP2PConnectSuccess = new PaskoochehDialog(context.getString(R.string.p2p_connection_success_alert_title), context.getColor(android.R.color.holo_green_dark),
                context.getString(R.string.p2p_connection_success_alert_description), context.getString(R.string.one_button_alert_button_caption), R.drawable.p2p_success_connect);
        dialogP2PConnectFail = new PaskoochehDialog(context.getString(R.string.p2p_connection_fail_alert_title), context.getColor(android.R.color.holo_red_dark),
                context.getString(R.string.p2p_connection_fail_alert_description), context.getString(R.string.one_button_alert_button_caption), R.drawable.p2p_fail_connect);
        dialogAppNotAvailableInP2PCache = new PaskoochehDialog(context.getString(R.string.p2p_app_not_available_in_cache_dialog_title),
                context.getColor(android.R.color.black), context.getString(R.string.p2p_app_not_available_in_cache_dialog_description),
                context.getString(R.string.p2p_app_not_available_in_cache_dialog_button_caption));
    }

    public void showP2PConnectingAlert(FragmentManager fragmentManager) {
        Log.d(LOGTAG, "showP2PConnectingAlert");
        if (!dialogP2PConnecting.isVisible()) {
            dialogP2PConnecting.show(fragmentManager, "p2p_connecting");
        }
    }

    public void hideP2PConnectingAlert() {
        Log.d(LOGTAG, "hideP2PConnectingAlert - checking if to hide");
        if (dialogP2PConnecting.isVisible()) {
            Log.d(LOGTAG, "hideP2PConnectingAlert -  hiding");
            dialogP2PConnecting.dismiss();
        }
    }

    public void showP2PDisconnectingAlert(FragmentManager fragmentManager) {
        Log.d(LOGTAG, "showP2PDisconnectingAlert");
        if (!dialogP2PDisconnecting.isVisible()) {
            dialogP2PDisconnecting.show(fragmentManager, "p2p_disconnecting");
        }
    }

    public void hideP2PDisconnectingAlert() {
        Log.d(LOGTAG, "hideP2PDisconnectingAlert - checking if to hide");
        if (dialogP2PDisconnecting.isVisible()) {
            Log.d(LOGTAG, "hideP2PDisconnectingAlert -  hiding");
            dialogP2PDisconnecting.dismiss();
        }
    }


    public void showP2PConnectSuccessAlert(FragmentManager fragmentManager) {
        Log.d(LOGTAG, "showP2PConnectSuccessAlert");
        if (!dialogP2PConnectSuccess.isVisible()) {
            dialogP2PConnectSuccess.show(fragmentManager, "p2p_connect_success");
        }
    }

    public void hideP2PConnectSuccessAlert() {
        if (dialogP2PConnectSuccess.isVisible()) {
            dialogP2PConnectSuccess.dismiss();
        }
    }

    public void showP2PConnectFailAlert(FragmentManager fragmentManager) {
        Log.d(LOGTAG, "showP2PConnectFailAlert");
        if (!dialogP2PConnectFail.isVisible()) {
            dialogP2PConnectFail.show(fragmentManager, "p2p_connect_fail");
        }
    }

    public void hideP2PConnectFailAlert() {
        if (dialogP2PConnectFail.isVisible()) {
            dialogP2PConnectFail.dismiss();
        }
    }

    public void showP2PAppNotFoundInCacheAlert(FragmentManager fragmentManager) {
        Log.d(LOGTAG, "showP2PAppNotFoundInCacheAlert");
        if (!dialogAppNotAvailableInP2PCache.isVisible()) {
            dialogAppNotAvailableInP2PCache.show(fragmentManager, "p2p_app_not_found_in_cache");
        }
    }

    public void hideP2PAppNotFoundInCacheAlert() {
        if (dialogAppNotAvailableInP2PCache.isVisible()) {
            dialogAppNotAvailableInP2PCache.dismiss();
        }
    }
}
