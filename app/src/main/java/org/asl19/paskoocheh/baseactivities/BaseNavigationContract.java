package org.asl19.paskoocheh.baseactivities;


import org.asl19.paskoocheh.pojo.AndroidTool;

import java.util.List;

public interface BaseNavigationContract {

    interface NavigationView extends BaseView<Presenter> {
        void getInstalledToolsSuccessful(List<AndroidTool> tools);

        void getInstalledToolsFailed();

        void getToolsSuccessful(List<AndroidTool> tools);

        void getToolsFailed();

        void onRegisterInstallSuccessful();

        void onRegisterInstallFailed();
    }

    interface Presenter extends BasePresenter {
        void getInstalledTools();

        void getAndroidTools();

        void registerInstall(String uuid, String tool);
    }
}