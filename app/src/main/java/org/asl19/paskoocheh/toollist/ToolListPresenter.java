package org.asl19.paskoocheh.toollist;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import org.asl19.paskoocheh.data.source.DownloadCountDataSource;
import org.asl19.paskoocheh.data.source.RatingDataSource;
import org.asl19.paskoocheh.data.source.ToolDataSource;
import org.asl19.paskoocheh.pojo.AndroidTool;
import org.asl19.paskoocheh.pojo.DownloadCountList;
import org.asl19.paskoocheh.pojo.RatingList;

import java.util.List;

import lombok.NonNull;

public class ToolListPresenter implements ToolListContract.Presenter {

    private final ToolListContract.ToolListView toolListView;
    private final ToolDataSource toolRepository;
    private final DownloadCountDataSource downloadCountRepository;
    private final RatingDataSource ratingRepository;

    public ToolListPresenter(@NonNull ToolListContract.ToolListView toolListView, @NonNull ToolDataSource toolRepository, @NonNull DownloadCountDataSource downloadCountRepository, @NonNull RatingDataSource ratingRepoository) {
        this.toolListView = toolListView;
        this.toolRepository = toolRepository;
        this.downloadCountRepository = downloadCountRepository;
        this.ratingRepository = ratingRepoository;

        this.toolListView.setPresenter(this);
    }

    @Override
    public void getAndroidTools() {
        toolRepository.getAndroidTools(new ToolDataSource.GetToolsCallback() {
            @Override
            public void onGetToolsSuccessful(List<AndroidTool> tools) {
                if (toolListView.isActive()) {
                    toolListView.getToolsSuccessful(tools);
                }
            }

            @Override
            public void onGetToolsFailed() {
                if (toolListView.isActive()) {
                    toolListView.getToolsFailed();
                }
            }
        });
    }

    @Override
    public void getFeaturedTools() {
        toolRepository.getFeaturedTools(new ToolDataSource.GetToolsCallback() {
            @Override
            public void onGetToolsSuccessful(List<AndroidTool> tools) {
                if (toolListView.isActive()) {
                    toolListView.getFeaturedSuccessful(tools);
                }
            }

            @Override
            public void onGetToolsFailed() {
                if (toolListView.isActive()) {
                    toolListView.getFeaturedFailed();
                }
            }
        });
    }

    @Override
    public void getDownloadCountList() {
        downloadCountRepository.getDownloadCountList(new DownloadCountDataSource.GetDownloadListCallback() {
            @Override
            public void onGetDownloadsSuccessful(DownloadCountList downloadCountList) {
                if (toolListView.isActive()) {
                    toolListView.getDownloadCountListSuccessful(downloadCountList);
                }
            }

            @Override
            public void onGetDownloadsFailed() {
                if (toolListView.isActive()) {
                    toolListView.getDownloadCountListFailed();
                }
            }
        });
    }

    @Override
    public void getRatingList() {
        ratingRepository.getRatingList(new RatingDataSource.GetRatingListCallback() {
            @Override
            public void onGetRatingSuccessful(RatingList ratingList) {
                if (toolListView.isActive()) {
                    toolListView.getRatingListSuccessful(ratingList);
                }
            }

            @Override
            public void onGetRatingsFailed() {
                if (toolListView.isActive()) {
                    toolListView.getRatingListFailed();
                }
            }
        });
    }

    @Override
    public void registerDownload(String tool, String uuid, DynamoDBMapper dynamoDBMapper) {
        downloadCountRepository.registerDownload(tool, uuid, dynamoDBMapper, new DownloadCountDataSource.RegisterDownloadCallback() {
            @Override
            public void onRegisterDownloadSuccessful() {
                if (toolListView.isActive()) {
                    toolListView.onRegisterDownloadSuccessful();
                }
            }

            @Override
            public void onRegisterDownloadFailed() {
                if (toolListView.isActive()) {
                    toolListView.onRegisterDownloadFailed();
                }
            }
        });
    }
}
