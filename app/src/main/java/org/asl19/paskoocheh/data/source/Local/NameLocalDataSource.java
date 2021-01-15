package org.asl19.paskoocheh.data.source.Local;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.NameDataSource;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.utils.AppExecutors;

import java.util.List;

import static com.fernandocejas.arrow.checks.Preconditions.checkNotNull;

public class NameLocalDataSource implements NameDataSource {

    private static volatile NameLocalDataSource INSTANCE;

    private NameDao nameDao;

    private AppExecutors appExecutors;

    private NameLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull NameDao nameDao) {
        this.appExecutors = appExecutors;
        this.nameDao = nameDao;
    }

    public static NameLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull NameDao nameDao) {
        if (INSTANCE == null) {
            synchronized (NameLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NameLocalDataSource(appExecutors, nameDao);
                }
            }
        }
        return INSTANCE;
    }

    public void getNames(final GetNameListCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Name> names = nameDao.getNames();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (names.isEmpty()) {
                            callback.onGetNamesFailed();
                        } else {
                            callback.onGetNamesSuccessful(names);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    public void saveNames(final Name... names) {
        checkNotNull(names);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                nameDao.insertAll(names);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void clearTable() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                nameDao.clearTable();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}