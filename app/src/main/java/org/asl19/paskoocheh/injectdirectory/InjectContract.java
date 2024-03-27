package org.asl19.paskoocheh.injectdirectory;

import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;

public class InjectContract {
    interface InjectView extends BaseView<InjectContract.Presenter> {

    }
    interface Presenter extends BasePresenter {
    }
}
