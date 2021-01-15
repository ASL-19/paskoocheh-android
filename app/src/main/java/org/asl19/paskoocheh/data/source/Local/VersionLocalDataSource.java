package org.asl19.paskoocheh.data.source.Local;


import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.VersionDataSource;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.utils.AppExecutors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.fernandocejas.arrow.checks.Preconditions.checkNotNull;

public class VersionLocalDataSource implements VersionDataSource {

    private static volatile VersionLocalDataSource INSTANCE;

    private VersionDao versionDao;

    private AppExecutors appExecutors;

    private Context context;

    private VersionLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull VersionDao versionDao, @NonNull Context context) {
        this.appExecutors = appExecutors;
        this.versionDao = versionDao;
        this.context = context;
    }

    public static VersionLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull VersionDao versionDao, Context context) {
        if (INSTANCE == null) {
            synchronized (VersionLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new VersionLocalDataSource(appExecutors, versionDao, context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getUpdatedAndroidVersion(final GetVersionsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Version> versions = versionDao.getUpdatedAndroidVersions();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (versions.isEmpty()) {
                            callback.onGetVersionsFailed();
                        } else {
                            callback.onGetVersionsSuccessful(versions);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getCategoryVersions(final Integer categoryId, final GetVersionsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Version> versions = versionDao.getAllAndroidVersions();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (versions.isEmpty()) {
                            callback.onGetVersionsFailed();
                        } else {
                            List<Version> categoryVersions = new ArrayList<>();
                            for (Version version: versions) {
                                if (version.getCategories().contains(String.valueOf(categoryId))) {
                                    categoryVersions.add(version);
                                }
                            }
                            callback.onGetVersionsSuccessful(categoryVersions);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getAndroidVersions(final VersionDataSource.GetVersionsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Version> versions = versionDao.getAllAndroidVersions();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (versions.isEmpty()) {
                            callback.onGetVersionsFailed();
                        } else {
                            callback.onGetVersionsSuccessful(versions);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getSearchAndroidVersions(final HashSet<Integer> setOfTools, final HashSet<String> categories, final String query, final GetVersionsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Version> versions = versionDao.getAllAndroidVersions();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (versions.isEmpty()) {
                            callback.onGetVersionsFailed();
                        } else {
                            List<Version> searchVersions = new ArrayList<>();
                            for (Version version: versions) {
                                if (setOfTools.contains(version.getToolId()) || version.getAppName().toLowerCase().contains(query)) {
                                    searchVersions.add(version);
                                } else {
                                    for (String category: categories) {
                                        if (version.getCategories().contains(category)) {
                                            searchVersions.add(version);
                                        }
                                    }
                                }
                            }
                            callback.onGetVersionsSuccessful(searchVersions);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getInstalledVersions(final GetVersionsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Version> versions = versionDao.getAllAndroidVersions();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (versions.isEmpty()) {
                            callback.onGetVersionsFailed();
                        } else {
                            List<Version> installedVersions = new ArrayList<>();
                            for (Version version: versions) {
                                try {
                                    int installedVersionCode = context.getApplicationContext().getPackageManager().getPackageInfo(version.getPackageName(), 0).versionCode;
                                    version.setInstalled(true);
                                    if (version.getVersionCode() > installedVersionCode) {
                                        version.setUpdateAvailable(true);
                                    }
                                    installedVersions.add(version);
                                } catch (PackageManager.NameNotFoundException e) {
                                }
                            }
                            callback.onGetVersionsSuccessful(installedVersions);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getVersion(final long toolId, final GetVersionCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Version version = versionDao.getVersion(toolId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (version == null) {
                            callback.onGetVersionFailed();
                        } else {
                            callback.onGetVersionSuccessful(version);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    public void saveVersion(@NonNull final Version... version) {
        checkNotNull(version);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                versionDao.insertAll(version);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void clearTable() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                versionDao.clearTable();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}
