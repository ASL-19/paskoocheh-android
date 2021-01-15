package org.asl19.paskoocheh.baseactivities;


import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

public interface BaseNavigationContract {

    interface NavigationView extends BaseView<Presenter> {
        void getInstalledVersionsSuccessful(List<Version> versions);

        void getInstalledVersionsFailed();

        void getVersionsSuccessful(List<Version> versions);

        void getVersionsFailed();
    }

    interface Presenter extends BasePresenter {
        void getInstalledVersions();

        void getAndroidVersions();
    }
}