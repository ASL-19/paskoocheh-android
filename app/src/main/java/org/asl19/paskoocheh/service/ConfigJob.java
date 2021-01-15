package org.asl19.paskoocheh.service;


import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.evernote.android.job.Job;

import static org.asl19.paskoocheh.Constants.APPS;
import static org.asl19.paskoocheh.Constants.DOWNLOADS_AND_RATINGS;
import static org.asl19.paskoocheh.Constants.FAQS;
import static org.asl19.paskoocheh.Constants.GUIDES_AND_TUTORIALS;
import static org.asl19.paskoocheh.Constants.REVIEWS;
import static org.asl19.paskoocheh.service.PaskoochehConfigService.CONFIG;

public class ConfigJob extends Job {

    public static final String TAG = "config_job";

    private Context context;

    public ConfigJob(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent service = new Intent(context, PaskoochehConfigService.class);
        service.putExtra(CONFIG, APPS);
        context.startService(service);
        service.putExtra(CONFIG, DOWNLOADS_AND_RATINGS);
        context.startService(service);
        service.putExtra(CONFIG, FAQS);
        context.startService(service);
        service.putExtra(CONFIG, GUIDES_AND_TUTORIALS);
        context.startService(service);
        service.putExtra(CONFIG, REVIEWS);
        context.startService(service);
        return Result.SUCCESS;
    }
}
