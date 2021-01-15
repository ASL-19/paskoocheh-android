package org.asl19.paskoocheh.feedback;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.AmazonContentBodyRequest;
import org.asl19.paskoocheh.data.source.AmazonDataSource;

public class FeedbackPresenter implements FeedbackContract.Presenter {

    private final FeedbackContract.FeedbackView feedbackView;
    private AmazonDataSource amazonRepository;

    public FeedbackPresenter(@NonNull FeedbackContract.FeedbackView feedbackView, @NonNull AmazonDataSource amazonRepository) {
        this.feedbackView = feedbackView;
        this.amazonRepository = amazonRepository;

        this.feedbackView.setPresenter(this);
    }


    @Override
    public void submitFeedback(AmazonContentBodyRequest feedback) {
        amazonRepository.onSubmitRequest(feedback, new AmazonDataSource.SubmitRequestCallback() {
            @Override
            public void onSubmitRequestSuccessful() {
                if (feedbackView.isActive()) {
                    feedbackView.onSubmitFeedbackSuccessful();
                }
            }

            @Override
            public void onSubmitRequestFailed() {
                if (feedbackView.isActive()) {
                    feedbackView.onSubmitFeedbackFailed();
                }
            }
        });
    }
}
