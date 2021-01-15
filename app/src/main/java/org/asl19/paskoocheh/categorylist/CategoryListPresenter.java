package org.asl19.paskoocheh.categorylist;


import org.asl19.paskoocheh.data.source.DownloadAndRatingDataSource;
import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.VersionDataSource;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

import lombok.NonNull;

public class CategoryListPresenter implements CategoryListContract.Presenter {

    private final CategoryListContract.CategoryListView categoryListView;
    private final VersionDataSource versionRepository;
    private final DownloadAndRatingDataSource downloadCountRepository;
    private final ImagesDataSource imagesRepository;
    private final LocalizedInfoDataSource localizedInfoRepository;

    public CategoryListPresenter(@NonNull CategoryListContract.CategoryListView categoryListView, @NonNull VersionDataSource versionRepository, @NonNull DownloadAndRatingDataSource downloadCountRepository, @NonNull ImagesDataSource imagesRepository, @NonNull LocalizedInfoDataSource localizedInfoRepository) {
        this.categoryListView = categoryListView;
        this.versionRepository = versionRepository;
        this.downloadCountRepository = downloadCountRepository;
        this.imagesRepository = imagesRepository;
        this.localizedInfoRepository = localizedInfoRepository;

        this.categoryListView.setPresenter(this);
    }

    @Override
    public void getCategoryAndroidTools(Integer categoryId) {
        versionRepository.getCategoryVersions(categoryId, new VersionDataSource.GetVersionsCallback() {
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

    @Override
    public void getDownloadAndRatingList() {
        downloadCountRepository.getDownloadAndRatings(new DownloadAndRatingDataSource.GetDownloadAndRatingListCallback() {
            @Override
            public void onGetDownloadAndRatingListSuccessful(List<DownloadAndRating> downloadCountList) {
                if (categoryListView.isActive()) {
                    categoryListView.getDownloadAndRatingListSuccessful(downloadCountList);
                }
            }

            @Override
            public void onGetDownloadAndRatingListFailed() {
                if (categoryListView.isActive()) {
                    categoryListView.getDownloadAndRatingListFailed();
                }
            }
        });
    }

    @Override
    public void getImages() {
        imagesRepository.getImages(new ImagesDataSource.GetImageListCallback() {
            @Override
            public void onGetImageListSuccessful(List<Images> images) {
                if (categoryListView.isActive()) {
                    categoryListView.getImagesSuccessful(images);
                }
            }

            @Override
            public void onGetImageListFailed() {
                if (categoryListView.isActive()) {
                    categoryListView.getImagesFailed();
                }
            }
        });
    }

    @Override
    public void getLocalizedInfo() {
        localizedInfoRepository.getLocalizedInfoList(new LocalizedInfoDataSource.GetLocalizedInfoListCallback() {
            @Override
            public void onGetLocalizedInfoListSuccessful(List<LocalizedInfo> localizedInfo) {
                if (categoryListView.isActive()) {
                    categoryListView.getLocalizedInfoSuccessful(localizedInfo);
                }
            }

            @Override
            public void onGetLocalizedInfoListFailed() {
                if (categoryListView.isActive()) {
                    categoryListView.getLocalizedInfoFailed();
                }
            }
        });
    }
}
