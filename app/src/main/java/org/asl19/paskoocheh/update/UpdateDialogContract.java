package org.asl19.paskoocheh.update;


import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;

public interface UpdateDialogContract {

    interface UpdateDialogView extends BaseView<Presenter> {

        void onRegisterInstallSuccessful();

        void onRegisterInstallFailed();
    }

    interface Presenter extends BasePresenter {

        void registerInstall(String uuid, String tool);
    }
}