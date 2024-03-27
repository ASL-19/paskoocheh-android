package org.asl19.paskoocheh.p2pnetwork;

import android.util.Log;

import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.TextDataSource;
import org.asl19.paskoocheh.data.source.VersionDataSource;
import org.asl19.paskoocheh.installedtoollist.InstalledToolListContract;
import org.asl19.paskoocheh.p2pnetwork.P2PContract.ListView;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Text;
import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

import lombok.NonNull;

public class P2PPresenter implements P2PContract.Presenter {
        private final ListView p2pView;
        private final VersionDataSource versionRepository;


public P2PPresenter(@NonNull P2PContract.ListView p2pView, @NonNull VersionDataSource versionRepository) {
        this.p2pView = (ListView) p2pView;
        this.versionRepository = versionRepository;
        this.p2pView.setPresenter(this);
        }

        @Override
        public void getAllTools() {
                versionRepository.getAndroidVersions(new VersionDataSource.GetVersionsCallback() {
                        @Override
                        public void onGetVersionsSuccessful(List<Version> versions) {
                                if (p2pView.isActive()) {
                                        p2pView.onGetVersionsListSuccessful(versions);
                                }
                        }

                        @Override
                        public void onGetVersionsFailed() {
                                if (p2pView.isActive()) {
                                        p2pView.onGetVersionsListFailed();
                                }
                        }
                });
        }



}

