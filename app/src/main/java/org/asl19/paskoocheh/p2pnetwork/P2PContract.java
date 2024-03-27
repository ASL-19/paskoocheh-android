package org.asl19.paskoocheh.p2pnetwork;

import org.asl19.paskoocheh.about.AboutContract;
import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.installedtoollist.InstalledToolListContract;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Text;
import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

public class P2PContract {
    interface ListView extends BaseView<Presenter> {
        void onGetVersionsListSuccessful(List<Version> versionList);
        void onGetVersionsListFailed();
    }

    interface Presenter extends BasePresenter {
        void getAllTools();
    }
}
