package com.aefyr.sai.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.aefyr.sai.adapters.selection.Selection;
import com.aefyr.sai.adapters.selection.SimpleKeyStorage;
import com.aefyr.sai.installer.ApkSourceBuilder;
import com.aefyr.sai.installer2.base.model.SaiPiSessionParams;
import com.aefyr.sai.installer2.impl.FlexSaiPackageInstaller;
import com.aefyr.sai.installer2.impl.rootless.RootlessSaiPiBroadcastReceiver;
import com.aefyr.sai.installerx.common.SplitApkSourceMeta;
import com.aefyr.sai.installerx.common.SplitPart;
import com.aefyr.sai.installerx.postprocessing.DeviceInfoAwarePostprocessor;
import com.aefyr.sai.installerx.postprocessing.HugeAppWarningPostprocessor;
import com.aefyr.sai.installerx.postprocessing.SortPostprocessor;
import com.aefyr.sai.installerx.resolver.appmeta.DefaultAppMetaExtractor;
import com.aefyr.sai.installerx.resolver.meta.impl.DefaultSplitApkSourceMetaResolver;
import com.aefyr.sai.installerx.resolver.urimess.SourceType;
import com.aefyr.sai.installerx.resolver.urimess.UriHost;
import com.aefyr.sai.installerx.resolver.urimess.UriMessResolutionError;
import com.aefyr.sai.installerx.resolver.urimess.UriMessResolutionResult;
import com.aefyr.sai.installerx.resolver.urimess.UriMessResolver;
import com.aefyr.sai.installerx.resolver.urimess.impl.AndroidUriHost;
import com.aefyr.sai.installerx.resolver.urimess.impl.DefaultUriMessResolver;
import com.aefyr.sai.model.apksource.ApkSource;
import com.aefyr.sai.utils.PreferencesHelper;
import com.aefyr.sai.utils.SimpleAsyncTask;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstallerXDialogViewModel {
    private static final String TAG = "InstallerXVM";

    private final Context mContext;

    private UriHost mUriHost;
    private final FlexSaiPackageInstaller mInstaller;
    private final PreferencesHelper mPrefsHelper;

    private final MutableLiveData<State> mState = new MutableLiveData<>(State.NO_DATA);
    private final MutableLiveData<SplitApkSourceMeta> mMeta = new MutableLiveData<>();
    private Warning mWarning;

    private LoadMetaTask mLoadMetaTask;

    private final Selection<String> mPartsSelection = new Selection<>(new SimpleKeyStorage());
    private List<UriMessResolutionResult> mResolutionResults;

    public InstallerXDialogViewModel(@NonNull Context appContext, @Nullable UriHost uriHost) {
        mContext = appContext;
        mUriHost = uriHost;
        if (mUriHost == null)
            mUriHost = new AndroidUriHost(mContext);

        mInstaller = FlexSaiPackageInstaller.getInstance(mContext);
        mPrefsHelper = PreferencesHelper.getInstance(mContext);
    }

    public LiveData<State> getState() {
        return mState;
    }

    public LiveData<SplitApkSourceMeta> getMeta() {
        return mMeta;
    }

    public Warning getWarning() {
        return mWarning;
    }

    public Selection<String> getPartsSelection() {
        return mPartsSelection;
    }

    // Here's the code to split the ZIP.
    public void setApkSourceFiles(List<File> apkSourceFiles) {
        if (mLoadMetaTask != null)
            mLoadMetaTask.cancel();

        //mState.setValue(State.LOADING);
        mResolutionResults = null;
        mLoadMetaTask = new LoadMetaTask(new LoadMetaTaskInput(apkSourceFiles, null)).execute();
    }

    public void setApkSourceUris(List<Uri> apkSourceUris) {
        if (mLoadMetaTask != null)
            mLoadMetaTask.cancel();

        //mState.setValue(State.LOADING);
        mResolutionResults = null;
        mLoadMetaTask = new LoadMetaTask(new LoadMetaTaskInput(null, apkSourceUris)).execute();
    }

    public void cancelParsing() {// if the App is closed or goes in the background then cancel the splitting zip task.
        if (mLoadMetaTask == null || !mLoadMetaTask.isOngoing())
            return;

        mLoadMetaTask.cancel();
        //mState.setValue(State.NO_DATA);
    }

    // Call this to install the extracted APK after the the Zip has been splitted.
    public void enqueueInstallation() {
        if (mResolutionResults == null)
            // TODO - jay - error handler here
            return;

        if (mResolutionResults.size() == 1) {
            enqueueSingleFiltered(mResolutionResults.get(0));
            return;
        }

        for (UriMessResolutionResult resolutionResult : mResolutionResults) {
            if (!resolutionResult.isSuccessful() && !resolutionResult.error().doesTryingToInstallNonethelessMakeSense())
                continue;

            ApkSourceBuilder apkSourceBuilder = null;

            if (resolutionResult.sourceType().equals(SourceType.ZIP)) {
                apkSourceBuilder = new ApkSourceBuilder(mContext)
                        .fromZipContentUri(resolutionResult.uris().get(0));

            } else if (resolutionResult.sourceType().equals(SourceType.APK_FILES)) {
                apkSourceBuilder = new ApkSourceBuilder(mContext)
                        .fromApkContentUris(resolutionResult.uris());
            }

            if (apkSourceBuilder != null) {
                apkSourceBuilder.setZipExtractionEnabled(mPrefsHelper.shouldExtractArchives())
                        .setReadZipViaZipFileEnabled(mPrefsHelper.shouldUseZipFileApi())
                        .setSigningEnabled(mPrefsHelper.shouldSignApks());

                install(apkSourceBuilder.build());
            }
        }
    }

    private void enqueueSingleFiltered(UriMessResolutionResult result) {
        ApkSourceBuilder apkSourceBuilder = null;

        if (result.sourceType() == SourceType.ZIP) {
            apkSourceBuilder = new ApkSourceBuilder(mContext)
                    .fromZipContentUri(result.uris().get(0));

        } else if (result.sourceType() == SourceType.APK_FILES) {
            apkSourceBuilder = new ApkSourceBuilder(mContext)
                    .fromApkContentUris(result.uris());
        }

        if (apkSourceBuilder != null) {
            apkSourceBuilder.setZipExtractionEnabled(mPrefsHelper.shouldExtractArchives())
                    .setReadZipViaZipFileEnabled(mPrefsHelper.shouldUseZipFileApi())
                    .setSigningEnabled(mPrefsHelper.shouldSignApks());

            if (result.isSuccessful())
                apkSourceBuilder.filterApksByLocalPath(new HashSet<>(mPartsSelection.getSelectedKeys()), false);

            install(apkSourceBuilder.build());
        }

    }

    private void install(ApkSource apkSource) {
        Log.d(TAG, "install - calling FlexInstaller.enqueueSession");
        mInstaller.enqueueSession(mInstaller.createSessionOnInstaller(mPrefsHelper.getInstaller(), new SaiPiSessionParams(apkSource)));
    }

    public enum State {
        NO_DATA, LOADING, LOADED, WARNING, ERROR
    }

    private static class LoadMetaTaskInput {
        List<File> apkSourceFiles;
        List<Uri> apkSourceContentUris;

        private LoadMetaTaskInput(@Nullable List<File> apkSourceFiles, @Nullable List<Uri> apkSourceContentUris) {
            this.apkSourceFiles = apkSourceFiles;
            this.apkSourceContentUris = apkSourceContentUris;
        }
    }

    private static class LoadMetaTaskResult {
        SplitApkSourceMeta meta;
        Set<String> splitsToSelect;
        List<UriMessResolutionResult> resolutionResults;

        private LoadMetaTaskResult(@Nullable SplitApkSourceMeta meta, @Nullable Set<String> splitsToSelect, @NonNull List<UriMessResolutionResult> resolutionResults) {
            this.meta = meta;
            this.splitsToSelect = splitsToSelect;
            this.resolutionResults = resolutionResults;
        }
    }

    private class LoadMetaTask extends SimpleAsyncTask<LoadMetaTaskInput, LoadMetaTaskResult> {

        private LoadMetaTask(LoadMetaTaskInput input) {
            super(input);
        }

        @Override
        protected LoadMetaTaskResult doWork(LoadMetaTaskInput input) {
            List<Uri> apkSourceUris = flattenInputToUris(input);
            if (apkSourceUris.size() == 0)
                throw new IllegalArgumentException("Expected at least 1 file in input"); // TODO - jay - FirebaseCrashalytocs.recordException.

            DefaultSplitApkSourceMetaResolver metaResolver = new DefaultSplitApkSourceMetaResolver(mContext, new DefaultAppMetaExtractor(mContext));
            metaResolver.addPostprocessor(new DeviceInfoAwarePostprocessor(mContext));
            metaResolver.addPostprocessor(new HugeAppWarningPostprocessor(mContext));
            metaResolver.addPostprocessor(new SortPostprocessor());

            UriMessResolver uriMessResolver = new DefaultUriMessResolver(mContext, metaResolver);
            List<UriMessResolutionResult> resolutionResults = uriMessResolver.resolve(apkSourceUris, mUriHost);

            if (resolutionResults.size() != 1) {
                return new LoadMetaTaskResult(null, null, resolutionResults);
            }

            UriMessResolutionResult resolutionResult = resolutionResults.get(0);
            if (resolutionResult.isSuccessful()) {
                SplitApkSourceMeta meta = resolutionResult.meta();
                HashSet<String> splitsToSelect = new HashSet<>();

                for (SplitPart part : meta.flatSplits()) {
                    if (part.isRecommended())
                        splitsToSelect.add(part.localPath());
                }

                return new LoadMetaTaskResult(meta, splitsToSelect, resolutionResults);
            }

            return new LoadMetaTaskResult(null, null, resolutionResults);
        }

        private List<Uri> flattenInputToUris(LoadMetaTaskInput input) {
            List<Uri> uris = new ArrayList<>();

            if (input.apkSourceContentUris != null)
                uris.addAll(input.apkSourceContentUris);

            if (input.apkSourceFiles != null) {
                for (File file : input.apkSourceFiles)
                    uris.add(Uri.fromFile(file));
            }

            return uris;
        }

        @Override
        protected void onWorkDone(LoadMetaTaskResult result) {
            mResolutionResults = result.resolutionResults;
            boolean isSuccessfulChosingOfApk = false;
            String errorMessage = null;
            if (mResolutionResults == null || mResolutionResults.size() == 0) {
                // error condition mState.setValue(State.WARNING);
                errorMessage = "No split APKs found";
            } else if (mResolutionResults.size() == 1) {
                UriMessResolutionResult uriMessResolutionResult = mResolutionResults.get(0);
                if (uriMessResolutionResult.isSuccessful()) {
                    mPartsSelection.clear();
                    mPartsSelection.batchSetSelected(result.splitsToSelect, true);
                    isSuccessfulChosingOfApk = true;
                    enqueueInstallation();
                } else {
                    // error condition mState.setValue(State.WARNING);
                    UriMessResolutionError error = uriMessResolutionResult.error();
                    errorMessage = error != null ? error.message() : null;
                }
            } else {
                errorMessage = "More than one result found";
                // error condition mState.setValue(State.WARNING);
            }

            if (!isSuccessfulChosingOfApk) {
                sendErrorIntent(errorMessage);
            }
        }

        @Override
        protected void onError(Exception exception) {
            Log.w(TAG, "Error while parsing meta for an apk", exception);
            sendErrorIntent("Error while parsing meta for an apk");
            mResolutionResults = null;
            FirebaseCrashlytics.getInstance().recordException(exception);

            //mState.setValue(State.ERROR);
        }
    }

    private void sendErrorIntent(String errorMessage) {
        FirebaseCrashlytics.getInstance().log("Failed to install errorMessage = " + errorMessage);
        Intent intent = new Intent();
        intent.setAction(ToolInfoFragment.INTENT_ACTION_APP_DOWNLOAD_AND_INSTALL_STATUS);
        intent.putExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_IS_INSTALL_SUCCESS, false);
        intent.putExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_SHORT_ERROR, errorMessage);
        intent.putExtra(RootlessSaiPiBroadcastReceiver.INTENT_EXTRA_FULL_ERROR, errorMessage);

        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
    public static class Warning {
        String mMessage;
        boolean mCanInstallAnyway;

        private Warning(String message, boolean canInstallAnyway) {
            mMessage = message;
            mCanInstallAnyway = canInstallAnyway;
        }

        public String message() {
            return mMessage;
        }

        public boolean canInstallAnyway() {
            return mCanInstallAnyway;
        }
    }


}
