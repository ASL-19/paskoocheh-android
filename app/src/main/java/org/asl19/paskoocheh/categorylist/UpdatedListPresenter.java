package org.asl19.paskoocheh.categorylist;


import org.asl19.paskoocheh.data.source.DownloadAndRatingDataSource;
import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.VersionDataSource;
import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

import lombok.NonNull;

class UpdatedListPresenter extends CategoryListPresenter {

    private CategoryListContract.CategoryListView categoryListView;
    private VersionDataSource versionRepository;

    public UpdatedListPresenter(@NonNull CategoryListContract.CategoryListView categoryListView, @NonNull VersionDataSource versionRepository, @NonNull DownloadAndRatingDataSource downloadCountRepository, @NonNull ImagesDataSource imagesRepository, @NonNull LocalizedInfoDataSource localizedInfoRepository) {
        super(categoryListView, versionRepository, downloadCountRepository, imagesRepository, localizedInfoRepository);
        this.categoryListView = categoryListView;
        this.versionRepository = versionRepository;
    }

    @Override
    public void getCategoryAndroidTools(Integer categoryId) {
        versionRepository.getUpdatedAndroidVersion(new VersionDataSource.GetVersionsCallback() {
            @Override
            public void onGetVersionsSuccessful(List<Version> versions) {
                if (categoryListView.isActive()) {
                    categoryListView.getVersionsSuccessful(versions);
                }
            }

            @Override
            public void onGetVersionsFailed() {
                if (categoryListView.isActive()) {
                    categoryListView.getVersionsFailed();
                }
            }
        });
    }
}
