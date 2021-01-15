package org.asl19.paskoocheh.installedtoollist;


import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.VersionDataSource;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

import lombok.NonNull;

public class InstalledToolListPresenter implements InstalledToolListContract.Presenter {

    private final InstalledToolListContract.ListView toolListView;
    private final VersionDataSource versionRepository;
    private final LocalizedInfoDataSource localedInfoRepository;
    private final ImagesDataSource imagesRepository;

    public InstalledToolListPresenter(@NonNull InstalledToolListContract.ListView toolListView, @NonNull VersionDataSource versionRepository, @NonNull LocalizedInfoDataSource localedInfoRepository, @NonNull ImagesDataSource imagesRepository) {
        this.toolListView = toolListView;
        this.versionRepository = versionRepository;
        this.localedInfoRepository = localedInfoRepository;
        this.imagesRepository = imagesRepository;

        this.toolListView.setPresenter(this);
    }

    @Override
    public void getInstalledTools() {
        versionRepository.getInstalledVersions(new VersionDataSource.GetVersionsCallback() {
            @Override
            public void onGetVersionsSuccessful(List<Version> versions) {
                if (toolListView.isActive()) {
                    toolListView.onGetInstalledVersionListSuccessful(versions);
                }
            }

            @Override
            public void onGetVersionsFailed() {
                if (toolListView.isActive()) {
                    toolListView.onGetInstalledVersionListFailed();
                }
            }
        });
    }

    @Override
    public void getLocalizedInfoList() {
        localedInfoRepository.getLocalizedInfoList(new LocalizedInfoDataSource.GetLocalizedInfoListCallback() {
            @Override
            public void onGetLocalizedInfoListSuccessful(List<LocalizedInfo> toolList) {
                if (toolListView.isActive()) {
                    toolListView.onGetLocalizedInfoListSuccessful(toolList);
                }
            }

            @Override
            public void onGetLocalizedInfoListFailed() {
                if (toolListView.isActive()) {
                    toolListView.onGetLocalizedInfoListFailed();
                }
            }
        });
    }

    @Override
    public void getImages() {
        imagesRepository.getImages(new ImagesDataSource.GetImageListCallback() {
            @Override
            public void onGetImageListSuccessful(List<Images> images) {
                if (toolListView.isActive()) {
                    toolListView.onGetImageListSuccessful(images);
                }
            }

            @Override
            public void onGetImageListFailed() {
                if (toolListView.isActive()) {
                    toolListView.onGetImageListFailed();
                }
            }
        });
    }
}
