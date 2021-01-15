package org.asl19.paskoocheh.data.source.Local;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.TutorialDataSource;
import org.asl19.paskoocheh.pojo.Tutorial;
import org.asl19.paskoocheh.utils.AppExecutors;

import java.util.List;

import static com.fernandocejas.arrow.checks.Preconditions.checkNotNull;

public class TutorialLocalDataSource implements TutorialDataSource {

    private static volatile TutorialLocalDataSource INSTANCE;

    private TutorialDao tutorialDao;

    private AppExecutors appExecutors;

    private TutorialLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull TutorialDao tutorialDao) {
        this.appExecutors = appExecutors;
        this.tutorialDao = tutorialDao;
    }

    public static TutorialLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull TutorialDao tutorialDao) {
        if (INSTANCE == null) {
            synchronized (TutorialLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TutorialLocalDataSource(appExecutors, tutorialDao);
                }
            }
        }
        return INSTANCE;
    }

    public void getToolTutorial(final int toolId, final GetTutorialListCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Tutorial> tutorials = tutorialDao.getToolTutorial(toolId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tutorials.isEmpty()) {
                            callback.onGetTutorialsFailed();
                        } else {
                            callback.onGetTutorialsSuccessful(tutorials);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    public void saveTutorial(final Tutorial... tutorials) {
        checkNotNull(tutorials);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                tutorialDao.insertAll(tutorials);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    public void clearTable() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                tutorialDao.clearTable();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}