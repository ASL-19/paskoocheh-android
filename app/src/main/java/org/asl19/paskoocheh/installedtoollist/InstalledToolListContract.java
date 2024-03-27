package org.asl19.paskoocheh.installedtoollist;


import android.content.Context;
import android.view.View;

import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

public interface InstalledToolListContract {

    interface ListView extends BaseView<Presenter> {
        void onGetInstalledVersionListSuccessful(List<Version> versionList);

        void onGetInstalledVersionListFailed();

        void onGetLocalizedInfoListSuccessful(List<LocalizedInfo> localizedInfoList);

        void onGetLocalizedInfoListFailed();

        void onGetImageListSuccessful(List<Images> imagesList);

        void onGetImageListFailed();
    }

    interface ToolListAdapter {
        Context getContext();
        void onUpdateButtonClick(Version version, View updateButton);
    }

    interface Presenter extends BasePresenter {

        void getInstalledTools();

        void getLocalizedInfoList();

        void getImages();
    }
}


