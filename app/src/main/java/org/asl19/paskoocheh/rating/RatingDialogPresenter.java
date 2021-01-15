package org.asl19.paskoocheh.rating;


import org.asl19.paskoocheh.data.source.AmazonDataSource;
import org.asl19.paskoocheh.data.source.AmazonReviewRequest;

public class RatingDialogPresenter implements RatingDialogContract.Presenter {

    private RatingDialogContract.RatingDialogView ratingDialogView;
    private AmazonDataSource amazonRepository;

    public RatingDialogPresenter(RatingDialogContract.RatingDialogView ratingDialogView, AmazonDataSource amazonRepository) {
        this.ratingDialogView = ratingDialogView;
        this.amazonRepository = amazonRepository;

        this.ratingDialogView.setPresenter(this);
    }

    @Override
    public void submitReview(AmazonReviewRequest amazonReviewRequest) {
        amazonRepository.onSubmitRequest(amazonReviewRequest, new AmazonDataSource.SubmitRequestCallback() {
            @Override
            public void onSubmitRequestSuccessful() {
                if (ratingDialogView.isActive()) {
                    ratingDialogView.onSubmitReviewSuccessful();
                }
            }

            @Override
            public void onSubmitRequestFailed() {
                if (ratingDialogView.isActive()) {
                    ratingDialogView.onSubmitReviewFailed();
                }
            }
        });
    }
}
