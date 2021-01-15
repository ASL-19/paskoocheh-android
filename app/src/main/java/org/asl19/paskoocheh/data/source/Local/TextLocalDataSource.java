package org.asl19.paskoocheh.data.source.Local;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.TextDataSource;
import org.asl19.paskoocheh.pojo.Text;
import org.asl19.paskoocheh.utils.AppExecutors;

import java.util.List;

import static com.fernandocejas.arrow.checks.Preconditions.checkNotNull;

public class TextLocalDataSource implements TextDataSource {

    private static volatile TextLocalDataSource INSTANCE;

    private TextDao textDao;

    private AppExecutors appExecutors;

    private TextLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull TextDao textDao) {
        this.appExecutors = appExecutors;
        this.textDao = textDao;
    }

    public static TextLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull TextDao textDao) {
        if (INSTANCE == null) {
            synchronized (TextLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TextLocalDataSource(appExecutors, textDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getTexts(final GetTextsCallback callback) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    final List<Text> texts = textDao.getPaskoochehTexts();
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (texts.isEmpty()) {
                                callback.onGetTextsFailed();
                            } else {
                                callback.onGetTextsSuccessful(texts);
                            }
                        }
                    });
                }
            };
            appExecutors.diskIO().execute(runnable);
    }

    public void saveTexts(@NonNull final Text... texts) {
        checkNotNull(texts);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                textDao.insertAll(texts);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void clearTable() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                textDao.clearTable();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}
