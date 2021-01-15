package org.asl19.paskoocheh.data.source.Local;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.DownloadAndRatingDataSource;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.utils.AppExecutors;

import java.util.List;

import static com.fernandocejas.arrow.checks.Preconditions.checkNotNull;

public class DownloadAndRatingLocalDataSource implements DownloadAndRatingDataSource {

    private static volatile DownloadAndRatingLocalDataSource INSTANCE;

    private DownloadAndRatingDao downloadAndRatingDao;

    private AppExecutors appExecutors;

    private DownloadAndRatingLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull DownloadAndRatingDao downloadAndRatingDao) {
        this.appExecutors = appExecutors;
        this.downloadAndRatingDao = downloadAndRatingDao;
    }

    public static DownloadAndRatingLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull DownloadAndRatingDao downloadAndRatingDao) {
        if (INSTANCE == null) {
            synchronized (DownloadAndRatingLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DownloadAndRatingLocalDataSource(appExecutors, downloadAndRatingDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getToolDownloadAndRatings(final long toolId, final GetToolDownloadAndRatingListCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final DownloadAndRating downloadAndRating = downloadAndRatingDao.getToolVersionDownloadAndRating(toolId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadAndRating == null) {
                            callback.onGetDownloadAndRatingListFailed();
                        } else {
                            callback.onGetDownloadAndRatingListSuccessful(downloadAndRating);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getDownloadAndRatings(final GetDownloadAndRatingListCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<DownloadAndRating> downloadAndRatings = downloadAndRatingDao.getDownloadAndRatings();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadAndRatings.isEmpty()) {
                            callback.onGetDownloadAndRatingListFailed();
                        } else {
                            callback.onGetDownloadAndRatingListSuccessful(downloadAndRatings);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getDownloadAndRatingsDownloadCountDesc(final GetDownloadAndRatingListCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<DownloadAndRating> downloadAndRatings = downloadAndRatingDao.getDownloadAndRatingsDownloadCountDesc();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadAndRatings.isEmpty()) {
                            callback.onGetDownloadAndRatingListFailed();
                        } else {
                            callback.onGetDownloadAndRatingListSuccessful(downloadAndRatings);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    public void saveDownloadAndRating(final DownloadAndRating... downloadAndRatings) {
        checkNotNull(downloadAndRatings);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                downloadAndRatingDao.insertAll(downloadAndRatings);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void clearTable() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                downloadAndRatingDao.clearTable();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}