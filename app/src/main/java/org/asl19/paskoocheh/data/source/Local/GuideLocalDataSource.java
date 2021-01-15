package org.asl19.paskoocheh.data.source.Local;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.GuideDataSource;
import org.asl19.paskoocheh.pojo.Guide;
import org.asl19.paskoocheh.utils.AppExecutors;

import java.util.List;

import static com.fernandocejas.arrow.checks.Preconditions.checkNotNull;

public class GuideLocalDataSource implements GuideDataSource {

    private static volatile GuideLocalDataSource INSTANCE;

    private GuideDao guideDao;

    private AppExecutors appExecutors;

    private GuideLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull GuideDao guideDao) {
        this.appExecutors = appExecutors;
        this.guideDao = guideDao;
    }

    public static GuideLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull GuideDao guideDao) {
        if (INSTANCE == null) {
            synchronized (GuideLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GuideLocalDataSource(appExecutors, guideDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getGuide(final int guideId, final GetGuideCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Guide guide = guideDao.getGuide(guideId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (guide == null) {
                            callback.onGetGuideFailed();
                        } else {
                            callback.onGetGuideSuccessful(guide);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    public void getToolGuides(final int toolId, final GetGuideListCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Guide> guides = guideDao.getToolGuide(toolId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (guides.isEmpty()) {
                            callback.onGetGuidesFailed();
                        } else {
                            callback.onGetGuidesSuccessful(guides);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    public void saveGuides(final Guide... guides) {
        checkNotNull(guides);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                guideDao.insertAll(guides);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void clearTable() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                guideDao.clearTable();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}