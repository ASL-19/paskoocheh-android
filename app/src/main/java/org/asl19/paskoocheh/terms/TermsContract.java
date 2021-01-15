package org.asl19.paskoocheh.terms;


import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.pojo.Text;

public class TermsContract {

    interface TermsView extends BaseView<Presenter> {
        void onGetTextSuccessful(Text text);

        void onGetTextFailed();
    }

    interface Presenter extends BasePresenter {
        void getText();
    }
}
