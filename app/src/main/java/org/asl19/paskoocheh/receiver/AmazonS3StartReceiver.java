package org.asl19.paskoocheh.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import org.asl19.paskoocheh.service.PaskoochehConfigService;

/**
 * Amazon S3 Service Start Receiver.
 */
public class AmazonS3StartReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, PaskoochehConfigService.class);
        startWakefulService(context, service);
    }
}
