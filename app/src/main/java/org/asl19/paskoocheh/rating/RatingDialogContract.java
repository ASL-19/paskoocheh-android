package org.asl19.paskoocheh.rating;


import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.data.source.AmazonReviewRequest;

public interface RatingDialogContract {

    interface RatingDialogView extends BaseView<Presenter> {

        void onSubmitReviewSuccessful();

        void onSubmitReviewFailed();
    }

    interface Presenter extends BasePresenter {

        void submitReview(AmazonReviewRequest amazonReviewRequest);
    }
}
