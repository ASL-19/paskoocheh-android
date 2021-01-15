package org.asl19.paskoocheh.guide;


import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.pojo.Guide;

public interface GuideContract {

    interface GuideView extends BaseView<Presenter> {

        void onGetGuideSuccessful(Guide guide);

        void onGetGuideFailed();
    }

    interface Presenter extends BasePresenter {

        void getGuide(int id);
    }
}