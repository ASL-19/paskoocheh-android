package org.asl19.paskoocheh.categorylist;


import org.asl19.paskoocheh.data.source.DownloadAndRatingDataSource;
import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.Local.DownloadAndRatingLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.VersionLocalDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.VersionDataSource;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Version;

import java.util.ArrayList;
import java.util.List;

class DownloadedListPresenter extends CategoryListPresenter {

    private CategoryListContract.CategoryListView categoryListView;
    private VersionDataSource versionRepository;
    private DownloadAndRatingDataSource downloadAndRatingRepository;

    public DownloadedListPresenter(CategoryListFragment categoryListView, VersionLocalDataSource versionLocalDataSource, DownloadAndRatingLocalDataSource downloadAndRatingLocalDataSource, ImagesDataSource imagesDataSource, LocalizedInfoDataSource localizedInfoDataSource) {
        super(categoryListView, versionLocalDataSource, downloadAndRatingLocalDataSource, imagesDataSource, localizedInfoDataSource);
        this.categoryListView = categoryListView;
        this.versionRepository = versionLocalDataSource;
        this.downloadAndRatingRepository = downloadAndRatingLocalDataSource;
    }

    @Override
    public void getCategoryAndroidTools(Integer categoryId) {
        downloadAndRatingRepository.getDownloadAndRatingsDownloadCountDesc(new DownloadAndRatingDataSource.GetDownloadAndRatingListCallback() {
            @Override
            public void onGetDownloadAndRatingListSuccessful(final List<DownloadAndRating> downloadAndRatings) {
                if (categoryListView.isActive()) {
                    versionRepository.getAndroidVersions(new VersionDataSource.GetVersionsCallback() {
                        @Override
                        public void onGetVersionsSuccessful(List<Version> versions) {
                            final List<Version> sortedVersions = new ArrayList<>();
                            for (DownloadAndRating downloadAndRating : downloadAndRatings) {
                                for (Version version : versions) {
                                    if (downloadAndRating.getToolId() == version.getToolId()) {
                                        sortedVersions.add(version);
                                    }
                                }
                            }
                            categoryListView.getVersionsSuccessful(sortedVersions);
                        }

                        @Override
                        public void onGetVersionsFailed() {

                        }
                    });
                }
            }

            @Override
            public void onGetDownloadAndRatingListFailed() {
            }
        });
    }
}
