package org.asl19.paskoocheh.about;


import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.pojo.Text;

public class AboutContract {

    interface AboutView extends BaseView<Presenter> {
        void onGetTextSuccessful(Text text);

        void onGetTextFailed();
    }

    interface Presenter extends BasePresenter {
        void getText();
    }
}
