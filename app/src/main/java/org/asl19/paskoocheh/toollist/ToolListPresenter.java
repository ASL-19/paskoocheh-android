package org.asl19.paskoocheh.toollist;


import org.asl19.paskoocheh.categorylist.CategoryListActivity;
import org.asl19.paskoocheh.data.source.DownloadAndRatingDataSource;
import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.NameDataSource;
import org.asl19.paskoocheh.data.source.ToolDataSource;
import org.asl19.paskoocheh.data.source.VersionDataSource;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.pojo.Tool;
import org.asl19.paskoocheh.pojo.Version;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class ToolListPresenter implements ToolListContract.Presenter {

    private final ToolListContract.ToolListView toolListView;
    private final VersionDataSource versionRepository;
    private final DownloadAndRatingDataSource downloadCountRepository;
    private final ImagesDataSource imagesRepository;
    private final LocalizedInfoDataSource localizedInfoRepository;
    private final NameDataSource nameRepository;
    private final ToolDataSource toolRepository;

    public ToolListPresenter(@NonNull ToolListContract.ToolListView toolListView, @NonNull VersionDataSource versionRepository, @NonNull DownloadAndRatingDataSource downloadCountRepository, @NonNull ImagesDataSource imagesRepository, @NonNull LocalizedInfoDataSource localizedInfoRepository, NameDataSource nameRepository, ToolDataSource toolRepository) {
        this.toolListView = toolListView;
        this.versionRepository = versionRepository;
        this.downloadCountRepository = downloadCountRepository;
        this.imagesRepository = imagesRepository;
        this.localizedInfoRepository = localizedInfoRepository;
        this.nameRepository = nameRepository;
        this.toolRepository = toolRepository;

        this.toolListView.setPresenter(this);
    }

    @Override
    public void getCategoryAndroidTools(final Name category) {
        versionRepository.getCategoryVersions(category.getCategoryId(), new VersionDataSource.GetVersionsCallback() {
            @Override
            public void onGetVersionsSuccessful(List<Version> versions) {
                if (toolListView.isActive()) {
                    toolListView.getCategoryVersionsSuccessful(versions, category);
                }
            }

            @Override
            public void onGetVersionsFailed() {
                if (toolListView.isActive()) {
                    toolListView.getCategoryVersionsFailed();
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

    @Override
    public void getFeatured() {
        versionRepository.getAndroidVersions(new VersionDataSource.GetVersionsCallback() {
            @Override
            public void onGetVersionsSuccessful(final List<Version> versions) {
                toolRepository.getTools(new ToolDataSource.GetToolListCallback() {
                    @Override
                    public void onGetToolListSuccessful(List<Tool> toolList) {
                        final List<Version> featuredVersions = new ArrayList<>();
                        for (final Version version : versions) {
                            for (Tool tool : toolList) {
                                if (version.getToolId() == tool.getId() && tool.getFeatured()) {
                                    featuredVersions.add(version);
                                }
                            }
                        }
                        if (toolListView.isActive()) {
                            toolListView.getSetVersionsSuccessful(featuredVersions, CategoryListActivity.FEATURED);
                        }
                    }
                    @Override
                    public void onGetToolListFailed() {
                    }
                });
            }

            @Override
            public void onGetVersionsFailed() {
            }
        });
    }

    @Override
    public void getTopDownloads() {
        downloadCountRepository.getDownloadAndRatingsDownloadCountDesc(new DownloadAndRatingDataSource.GetDownloadAndRatingListCallback() {
            @Override
            public void onGetDownloadAndRatingListSuccessful(final List<DownloadAndRating> downloadAndRatings) {
                versionRepository.getAndroidVersions(new VersionDataSource.GetVersionsCallback() {
                    @Override
                    public void onGetVersionsSuccessful(List<Version> versions) {
                        final List<Version> sortedVersions = new ArrayList<>();
                        for (DownloadAndRating downloadAndRating : downloadAndRatings) {
                            for (Version version: versions) {
                                if (downloadAndRating.getToolId() == version.getToolId()) {
                                    sortedVersions.add(version);
                                }
                            }
                        }
                        if (toolListView.isActive()) {
                            toolListView.getSetVersionsSuccessful(sortedVersions, CategoryListActivity.TOP_DOWNLOADS);
                        }
                    }

                    @Override
                    public void onGetVersionsFailed() {

                    }
                });
            }

            @Override
            public void onGetDownloadAndRatingListFailed() {
            }
        });
    }

    @Override
    public void getUpdated() {
        versionRepository.getUpdatedAndroidVersion(new VersionDataSource.GetVersionsCallback() {
            @Override
            public void onGetVersionsSuccessful(List<Version> versions) {
                if (toolListView.isActive()) {
                    toolListView.getSetVersionsSuccessful(versions, CategoryListActivity.UPDATED);
                }
            }

            @Override
            public void onGetVersionsFailed() {
                if (toolListView.isActive()) {
                    toolListView.getSetVersionsFailed();
                }
            }
        });
    }
}
