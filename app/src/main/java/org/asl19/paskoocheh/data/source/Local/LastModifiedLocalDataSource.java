package org.asl19.paskoocheh.data.source.Local;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.LastModifiedDataSource;
import org.asl19.paskoocheh.pojo.LastModified;
import org.asl19.paskoocheh.utils.AppExecutors;

import static com.fernandocejas.arrow.checks.Preconditions.checkNotNull;

public class LastModifiedLocalDataSource implements LastModifiedDataSource {

    private static volatile LastModifiedLocalDataSource INSTANCE;

    private LastModifiedDao lastModifiedDao;

    private AppExecutors appExecutors;

    private LastModifiedLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull LastModifiedDao lastModifiedDao) {
        this.appExecutors = appExecutors;
        this.lastModifiedDao = lastModifiedDao;
    }

    public static LastModifiedLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull LastModifiedDao lastModifiedDao) {
        if (INSTANCE == null) {
            synchronized (LastModifiedLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LastModifiedLocalDataSource(appExecutors, lastModifiedDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getLastModified(final String configFile, final GetLastModifiedCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final LastModified lastModified = lastModifiedDao.getLastModified(configFile);
                appExecutors.networkIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (lastModified == null) {
                            callback.onGetLastModifiedFailed();
                        } else {
                            callback.onGetLastModifiedSuccessful(lastModified);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveLastModified(@NonNull final LastModified... lastModifieds) {
        checkNotNull(lastModifieds);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                lastModifiedDao.insertAll(lastModifieds);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void clearTable() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                lastModifiedDao.clearTable();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}
