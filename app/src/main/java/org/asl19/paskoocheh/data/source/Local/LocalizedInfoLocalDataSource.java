package org.asl19.paskoocheh.data.source.Local;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.utils.AppExecutors;

import java.util.List;

import static com.fernandocejas.arrow.checks.Preconditions.checkNotNull;
import static org.asl19.paskoocheh.Constants.FA;

public class LocalizedInfoLocalDataSource implements LocalizedInfoDataSource {

    private static volatile LocalizedInfoLocalDataSource INSTANCE;

    private LocalizedInfoDao localizedInfoDao;

    private AppExecutors appExecutors;

    private LocalizedInfoLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull LocalizedInfoDao localizedInfoDao) {
        this.appExecutors = appExecutors;
        this.localizedInfoDao = localizedInfoDao;
    }

    public static LocalizedInfoLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull LocalizedInfoDao localizedInfoDao) {
        if (INSTANCE == null) {
            synchronized (LocalizedInfoLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalizedInfoLocalDataSource(appExecutors, localizedInfoDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getLocalizedInfo(final long toolId, final GetLocalizedInfoCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<LocalizedInfo> localizedInfoList = localizedInfoDao.getToolLocalizedInfo(toolId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (localizedInfoList.isEmpty()) {
                            callback.onGetLocalizedInfoFailed();
                        } else {
                            LocalizedInfo localizedInfoTemp = new LocalizedInfo();
                            for (LocalizedInfo localizedInfo: localizedInfoList) {
                                if (localizedInfo.getLocale().equals(FA)) {
                                    if (!localizedInfo.getName().isEmpty()) {
                                        localizedInfoTemp.setName(localizedInfo.getName());
                                    }

                                    if (!localizedInfo.getDescription().isEmpty()) {
                                        localizedInfoTemp.setDescription(localizedInfo.getDescription());
                                    }
                                } else {
                                    if (localizedInfoTemp.getName().isEmpty()) {
                                        localizedInfoTemp.setName(localizedInfo.getName());
                                    }

                                    if (localizedInfo.getDescription().isEmpty()) {
                                        localizedInfoTemp.setDescription(localizedInfo.getDescription());
                                    }
                                }
                            }
                            callback.onGetLocalizedInfoSuccessful(localizedInfoTemp);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getLocalizedInfoList(final GetLocalizedInfoListCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<LocalizedInfo> localizedInfo = localizedInfoDao.getLocalizedInfoList();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (localizedInfo == null) {
                            callback.onGetLocalizedInfoListFailed();
                        } else {
                            callback.onGetLocalizedInfoListSuccessful(localizedInfo);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveLocalizedInfo(final LocalizedInfo localizedInfo) {
        checkNotNull(localizedInfo);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                localizedInfoDao.insertAll(localizedInfo);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void clearTable() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                localizedInfoDao.clearTable();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}
