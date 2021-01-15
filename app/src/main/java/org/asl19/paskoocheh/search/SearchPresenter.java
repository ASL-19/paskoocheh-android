package org.asl19.paskoocheh.search;


import org.asl19.paskoocheh.data.source.DownloadAndRatingDataSource;
import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.NameDataSource;
import org.asl19.paskoocheh.data.source.VersionDataSource;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.pojo.Version;

import java.util.HashSet;
import java.util.List;

import lombok.NonNull;

public class SearchPresenter implements SearchContract.Presenter {

    private final SearchContract.SearchView toolListView;
    private final VersionDataSource versionRepository;
    private final DownloadAndRatingDataSource downloadCountRepository;
    private final ImagesDataSource imagesRepository;
    private final LocalizedInfoDataSource localizedInfoRepository;
    private final NameDataSource nameRepository;

    public SearchPresenter(@NonNull SearchContract.SearchView searchView, @NonNull VersionDataSource versionRepository, @NonNull DownloadAndRatingDataSource downloadCountRepository, @NonNull ImagesDataSource imagesRepository, @NonNull LocalizedInfoDataSource localizedInfoRepository, NameDataSource nameRepository) {
        this.toolListView = searchView;
        this.versionRepository = versionRepository;
        this.downloadCountRepository = downloadCountRepository;
        this.imagesRepository = imagesRepository;
        this.localizedInfoRepository = localizedInfoRepository;
        this.nameRepository = nameRepository;

        this.toolListView.setPresenter(this);
    }

    @Override
    public void getAndroidTools() {
        versionRepository.getAndroidVersions(new VersionDataSource.GetVersionsCallback() {
            @Override
            public void onGetVersionsSuccessful(List<Version> versions) {
                if (toolListView.isActive()) {
                    toolListView.getVersionsSuccessful(versions);
                }
            }

            @Override
            public void onGetVersionsFailed() {
                if (toolListView.isActive()) {
                    toolListView.getVersionsFailed();
                }
            }
        });
    }

    @Override
    public void getSearchTools(HashSet<Integer> setOfTools, HashSet<String> category, String query) {
        versionRepository.getSearchAndroidVersions(setOfTools, category, query, new VersionDataSource.GetVersionsCallback() {
            @Override
            public void onGetVersionsSuccessful(List<Version> versions) {
                if (toolListView.isActive()) {
                    toolListView.getVersionsSuccessful(versions);
                }
            }

            @Override
            public void onGetVersionsFailed() {
                if (toolListView.isActive()) {
                    toolListView.getVersionsFailed();
                }
            }
        });
    }

    @Override
    public void getDownloadAndRatingList() {
        downloadCountRepository.getDownloadAndRatings(new DownloadAndRatingDataSource.GetDownloadAndRatingListCallback() {
            @Override
            public void onGetDownloadAndRatingListSuccessful(List<DownloadAndRating> downloadCountList) {
                if (toolListView.isActive()) {
                    toolListView.getDownloadAndRatingListSuccessful(downloadCountList);
                }
            }

            @Override
            public void onGetDownloadAndRatingListFailed() {
                if (toolListView.isActive()) {
                    toolListView.getDownloadAndRatingListFailed();
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
                    toolListView.getImagesSuccessful(images);
                }
            }

            @Override
            public void onGetImageListFailed() {
                if (toolListView.isActive()) {
                    toolListView.getImagesFailed();
                }
            }
        });
    }

    @Override
    public void getLocalizedInfo() {
        localizedInfoRepository.getLocalizedInfoList(new LocalizedInfoDataSource.GetLocalizedInfoListCallback() {
            @Override
            public void onGetLocalizedInfoListSuccessful(List<LocalizedInfo> localizedInfo) {
                if (toolListView.isActive()) {
                    toolListView.getLocalizedInfoSuccessful(localizedInfo);
                }
            }

            @Override
            public void onGetLocalizedInfoListFailed() {
                if (toolListView.isActive()) {
                    toolListView.getLocalizedInfoFailed();
                }
            }
        });
    }

    @Override
    public void getCategoryNames() {
        nameRepository.getNames(new NameDataSource.GetNameListCallback() {
            @Override
            public void onGetNamesSuccessful(List<Name> names) {
                if (toolListView.isActive()) {
                    toolListView.onGetCategoryNamesSuccessful(names);
                }
            }

            @Override
            public void onGetNamesFailed() {
                if (toolListView.isActive()) {
                    toolListView.onGetCategoryNamesFailed();
                }
            }
        });
    }
}
