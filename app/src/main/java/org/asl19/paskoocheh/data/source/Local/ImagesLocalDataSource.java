package org.asl19.paskoocheh.data.source.Local;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.utils.AppExecutors;

import java.util.List;

import static com.fernandocejas.arrow.checks.Preconditions.checkNotNull;

public class ImagesLocalDataSource implements ImagesDataSource {

    private static volatile ImagesLocalDataSource INSTANCE;

    private ImagesDao imagesDao;

    private AppExecutors appExecutors;

    private ImagesLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull ImagesDao imagesDao) {
        this.appExecutors = appExecutors;
        this.imagesDao = imagesDao;
    }

    public static ImagesLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull ImagesDao imagesDao) {
        if (INSTANCE == null) {
            synchronized (ImagesLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ImagesLocalDataSource(appExecutors, imagesDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getVersionImages(final long toolId, final ImagesDataSource.GetImagesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Images images = imagesDao.getVersionImages(toolId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (images == null) {
                            callback.onGetImagesFailed();
                        } else {
                            callback.onGetImagesSuccessful(images);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getToolImages(final long toolId, final GetImagesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Images images = imagesDao.getToolImages(toolId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (images == null) {
                            callback.onGetImagesFailed();
                        } else {
                            callback.onGetImagesSuccessful(images);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getImages(final GetImageListCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Images> images = imagesDao.getImages();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (images == null) {
                            callback.onGetImageListFailed();
                        } else {
                            callback.onGetImageListSuccessful(images);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveImages(@NonNull final Images... images) {
        checkNotNull(images);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                imagesDao.insertAll(images);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void clearTable() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                imagesDao.clearTable();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}
