package org.asl19.paskoocheh.service;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

public class ConfigJobCreator implements JobCreator {

    private Context context;

    public ConfigJobCreator(Context context) {
        this.context = context;
    }
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        return new ConfigJob(context);
    }

    public static void scheduleJob() {
        if (JobManager.instance().getAllJobRequestsForTag(ConfigJob.TAG).size() < 1) {
            new JobRequest.Builder(ConfigJob.TAG)
                    .setPeriodic(TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(4))
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .setRequiresDeviceIdle(true)
                    .setRequirementsEnforced(true)
                    .build()
                    .schedule();
        }
    }
}
