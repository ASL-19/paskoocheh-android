package org.asl19.paskoocheh.categorylist;


import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.Local.DownloadAndRatingLocalDataSource;
import org.asl19.paskoocheh.data.source.Local.VersionLocalDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.ToolDataSource;
import org.asl19.paskoocheh.data.source.VersionDataSource;
import org.asl19.paskoocheh.pojo.Tool;
import org.asl19.paskoocheh.pojo.Version;

import java.util.ArrayList;
import java.util.List;

class FeaturedListPresenter extends CategoryListPresenter {
    private CategoryListContract.CategoryListView categoryListView;
    private VersionDataSource versionRepository;
    private ToolDataSource toolRepository;

    public FeaturedListPresenter(CategoryListFragment categoryListView, VersionLocalDataSource versionLocalDataSource, DownloadAndRatingLocalDataSource downloadAndRatingLocalDataSource, ImagesDataSource imagesDataSource, LocalizedInfoDataSource localizedInfoDataSource, ToolDataSource toolLocalDataSource) {
        super(categoryListView, versionLocalDataSource, downloadAndRatingLocalDataSource, imagesDataSource, localizedInfoDataSource);
        this.categoryListView = categoryListView;
        this.versionRepository = versionLocalDataSource;
        this.toolRepository = toolLocalDataSource;
    }

    @Override
    public void getCategoryAndroidTools(Integer categoryId) {
        versionRepository.getAndroidVersions(new VersionDataSource.GetVersionsCallback() {
            @Override
            public void onGetVersionsSuccessful(final List<Version> versions) {
                if (categoryListView.isActive()) {
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
                            categoryListView.getVersionsSuccessful(featuredVersions);
                        }

                        @Override
                        public void onGetToolListFailed() {
                        }
                    });
                }
            }

            @Override
            public void onGetVersionsFailed() {
            }
        });
    }
}
