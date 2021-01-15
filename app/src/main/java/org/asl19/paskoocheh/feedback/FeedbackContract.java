package org.asl19.paskoocheh.feedback;


import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.data.AmazonContentBodyRequest;

public interface FeedbackContract {

    interface FeedbackView extends BaseView<Presenter> {

        void onSubmitFeedbackSuccessful();

        void onSubmitFeedbackFailed();
    }

    interface Presenter extends BasePresenter {

        void submitFeedback(AmazonContentBodyRequest feedback);
    }
}