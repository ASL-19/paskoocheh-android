package org.asl19.paskoocheh.data.source.Local;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.FaqDataSource;
import org.asl19.paskoocheh.pojo.Faq;
import org.asl19.paskoocheh.utils.AppExecutors;

import java.util.List;

import static com.fernandocejas.arrow.checks.Preconditions.checkNotNull;

public class FaqLocalDataSource implements FaqDataSource {

    private static volatile FaqLocalDataSource INSTANCE;

    private FaqDao faqDao;

    private AppExecutors appExecutors;

    private FaqLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull FaqDao faqDao) {
        this.appExecutors = appExecutors;
        this.faqDao = faqDao;
    }

    public static FaqLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull FaqDao faqDao) {
        if (INSTANCE == null) {
            synchronized (FaqLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FaqLocalDataSource(appExecutors, faqDao);
                }
            }
        }
        return INSTANCE;
    }

    public void getToolFaqs(final int toolId, final GetFaqListCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Faq> faqs = faqDao.getToolVersionFaqs(toolId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (faqs.isEmpty()) {
                            callback.onGetFaqsFailed();
                        } else {
                            callback.onGetFaqsSuccessful(faqs);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    public void saveFaqs(final Faq... faq) {
        checkNotNull(faq);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                faqDao.insertAll(faq);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void clearTable() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                faqDao.clearTable();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}