package com.aefyr.sai.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import androidx.preference.PreferenceManager;

public class PreferencesHelper {
    private static PreferencesHelper sInstance;

    private final SharedPreferences mPrefs;

    public static PreferencesHelper getInstance(Context c) {
        return sInstance != null ? sInstance : new PreferencesHelper(c);
    }

    private PreferencesHelper(Context c) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        sInstance = this;
    }

    public SharedPreferences getPrefs() {
        return mPrefs;
    }

    public String getHomeDirectory() {
        return mPrefs.getString(PreferencesKeys.HOME_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    public void setHomeDirectory(String homeDirectory) {
        mPrefs.edit().putString(PreferencesKeys.HOME_DIRECTORY, homeDirectory).apply();
    }

    public int getFilePickerRawSort() {
        return mPrefs.getInt(PreferencesKeys.FILE_PICKER_SORT_RAW, 0);
    }

    public void setFilePickerRawSort(int rawSort) {
        mPrefs.edit().putInt(PreferencesKeys.FILE_PICKER_SORT_RAW, rawSort).apply();
    }

    public int getFilePickerSortBy() {
        return mPrefs.getInt(PreferencesKeys.FILE_PICKER_SORT_BY, 0); // Sort by name.
    }

    public void setFilePickerSortBy(int sortBy) {
        mPrefs.edit().putInt(PreferencesKeys.FILE_PICKER_SORT_BY, sortBy).apply();
    }

    public int getFilePickerSortOrder() {
        return mPrefs.getInt(PreferencesKeys.FILE_PICKER_SORT_ORDER, 0); // Normal sort order.
    }

    public void setFilePickerSortOrder(int sortOrder) {
        mPrefs.edit().putInt(PreferencesKeys.FILE_PICKER_SORT_ORDER, sortOrder).apply();
    }

    public boolean shouldSignApks() {
        return mPrefs.getBoolean(PreferencesKeys.SIGN_APKS, false);
    }

    public void setShouldSignApks(boolean signApks) {
        mPrefs.edit().putBoolean(PreferencesKeys.SIGN_APKS, signApks).apply();
    }

    public boolean shouldExtractArchives() {
        return mPrefs.getBoolean(PreferencesKeys.EXTRACT_ARCHIVES, false);
    }

    public boolean shouldUseZipFileApi() {
        return mPrefs.getBoolean(PreferencesKeys.USE_ZIPFILE, false);
    }

    public void setInstaller(int installer) {
        mPrefs.edit().putInt(PreferencesKeys.INSTALLER, installer).apply();
    }

    public int getInstaller() {
        return mPrefs.getInt(PreferencesKeys.INSTALLER, PreferencesValues.INSTALLER_ROOTLESS);
    }

    public void setBackupFileNameFormat(String format) {
        mPrefs.edit().putString(PreferencesKeys.BACKUP_FILE_NAME_FORMAT, format).apply();
    }

    public String getBackupFileNameFormat() {
        return mPrefs.getString(PreferencesKeys.BACKUP_FILE_NAME_FORMAT, PreferencesValues.BACKUP_FILE_NAME_FORMAT_DEFAULT);
    }

    public void setInstallLocation(int installLocation) {
        mPrefs.edit().putString(PreferencesKeys.INSTALL_LOCATION, String.valueOf(installLocation)).apply();
    }

    public int getInstallLocation() {
        String rawInstallLocation = mPrefs.getString(PreferencesKeys.INSTALL_LOCATION, "0");
        try {
            return Integer.parseInt(rawInstallLocation);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean useOldInstaller() {
        return mPrefs.getBoolean(PreferencesKeys.USE_OLD_INSTALLER, false);
    }

    public boolean showInstallerDialogs() {
        return mPrefs.getBoolean(PreferencesKeys.SHOW_INSTALLER_DIALOGS, true);
    }

    public boolean shouldShowAppFeatures() {
        return mPrefs.getBoolean(PreferencesKeys.SHOW_APP_FEATURES, true);
    }

    public boolean shouldShowSafTip() {
        return !mPrefs.getBoolean(PreferencesKeys.SAF_TIP_SHOWN, false);
    }

    public void setSafTipShown() {
        mPrefs.edit().putBoolean(PreferencesKeys.SAF_TIP_SHOWN, true).apply();
    }

    public boolean isInstallerXEnabled() {
        return mPrefs.getBoolean(PreferencesKeys.USE_INSTALLERX, true);
    }

    public boolean isBruteParserEnabled() {
        return mPrefs.getBoolean(PreferencesKeys.USE_BRUTE_PARSER, true);
    }

    public boolean isAnalyticsEnabled() {
        return mPrefs.getBoolean(PreferencesKeys.ENABLE_ANALYTICS, true);
    }

    public void setAnalyticsEnabled(boolean enabled) {
        mPrefs.edit().putBoolean(PreferencesKeys.ENABLE_ANALYTICS, enabled).apply();
    }

    public boolean isInitialIndexingDone() {
        return mPrefs.getBoolean(PreferencesKeys.INITIAL_INDEXING_RUN, false);
    }

    public void setInitialIndexingDone(boolean done) {
        mPrefs.edit().putBoolean(PreferencesKeys.INITIAL_INDEXING_RUN, done).apply();
    }

    public boolean isSingleApkExportEnabled() {
        return mPrefs.getBoolean(PreferencesKeys.BACKUP_APK_EXPORT, false);
    }

    public void setSingleApkExportEnabled(boolean enabled) {
        mPrefs.edit().putBoolean(PreferencesKeys.BACKUP_APK_EXPORT, enabled).apply();
    }

}
