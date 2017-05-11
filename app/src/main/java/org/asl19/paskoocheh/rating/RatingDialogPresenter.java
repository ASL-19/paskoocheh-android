package org.asl19.paskoocheh.rating;


import org.asl19.paskoocheh.data.SendReviewRequest;
import org.asl19.paskoocheh.data.source.ReviewDataSource;

public class RatingDialogPresenter implements RatingDialogContract.Presenter {

    private RatingDialogContract.RatingDialogView ratingDialogView;
    private ReviewDataSource reviewRepository;

    public RatingDialogPresenter(RatingDialogContract.RatingDialogView ratingDialogView, ReviewDataSource reviewRepository) {
        this.ratingDialogView = ratingDialogView;
        this.reviewRepository = reviewRepository;

        this.ratingDialogView.setPresenter(this);
    }

    @Override
    public void submitReview(SendReviewRequest sendReviewRequest) {
        reviewRepository.submitReview(sendReviewRequest, new ReviewDataSource.SendReviewCallback() {
            @Override
            public void onSendReviewSuccessful() {
                if (ratingDialogView.isActive()) {
                    ratingDialogView.onSubmitReviewSuccessful();
                }
            }

            @Override
            public void onSendReviewFailed() {
                if (ratingDialogView.isActive()) {
                    ratingDialogView.onSubmitReviewFailed();
                }
            }
        });
    }
}
