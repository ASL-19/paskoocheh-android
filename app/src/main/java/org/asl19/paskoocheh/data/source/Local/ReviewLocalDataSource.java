package org.asl19.paskoocheh.data.source.Local;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.ReviewDataSource;
import org.asl19.paskoocheh.pojo.Review;
import org.asl19.paskoocheh.utils.AppExecutors;

import java.util.List;

import static com.fernandocejas.arrow.checks.Preconditions.checkNotNull;

public class ReviewLocalDataSource implements ReviewDataSource {

    private static volatile ReviewLocalDataSource INSTANCE;

    private ReviewDao reviewDao;

    private AppExecutors appExecutors;

    private ReviewLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull ReviewDao reviewDao) {
        this.appExecutors = appExecutors;
        this.reviewDao = reviewDao;
    }

    public static ReviewLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull ReviewDao guideDao) {
        if (INSTANCE == null) {
            synchronized (ReviewLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ReviewLocalDataSource(appExecutors, guideDao);
                }
            }
        }
        return INSTANCE;
    }

    public void getReviewList(final int toolId, final GetReviewListCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Review> reviews = reviewDao.getToolReview(toolId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (reviews == null) {
                            callback.onGetReviewsFailed();
                        } else {
                            callback.onGetReviewsSuccessful(reviews);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    public void saveReview(final Review... reviews) {
        checkNotNull(reviews);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                reviewDao.insertAll(reviews);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    public void clearTable() {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                reviewDao.clearTable();
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }
}