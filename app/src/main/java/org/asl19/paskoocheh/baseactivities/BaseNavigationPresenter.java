package org.asl19.paskoocheh.baseactivities;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import org.asl19.paskoocheh.data.source.DownloadCountDataSource;
import org.asl19.paskoocheh.data.source.ToolDataSource;
import org.asl19.paskoocheh.pojo.AndroidTool;

import java.util.List;

import lombok.NonNull;

public class BaseNavigationPresenter implements BaseNavigationContract.Presenter {

    private final BaseNavigationContract.NavigationView navigationView;
    private final ToolDataSource toolRepository;
    private final DownloadCountDataSource downloadCountDataRepository;

    public BaseNavigationPresenter(@NonNull BaseNavigationContract.NavigationView navigationView, @NonNull ToolDataSource toolRepository, @NonNull DownloadCountDataSource downloadCountDataRepository) {
        this.navigationView = navigationView;
        this.toolRepository = toolRepository;
        this.downloadCountDataRepository = downloadCountDataRepository;

        this.navigationView.setPresenter(this);
    }

    @Override
    public void getInstalledTools() {
        toolRepository.getInstalledTools(new ToolDataSource.GetToolsCallback() {
            @Override
            public void onGetToolsSuccessful(List<AndroidTool> tools) {
                if (navigationView.isActive()) {
                    navigationView.getInstalledToolsSuccessful(tools);
                }
            }

            @Override
            public void onGetToolsFailed() {
                if (navigationView.isActive()) {
                    navigationView.getInstalledToolsFailed();
                }
            }
        });
    }

    @Override
    public void getAndroidTools() {
        toolRepository.getAndroidTools(new ToolDataSource.GetToolsCallback() {
            @Override
            public void onGetToolsSuccessful(List<AndroidTool> tools) {
                if (navigationView.isActive()) {
                    navigationView.getToolsSuccessful(tools);
                }
            }

            @Override
            public void onGetToolsFailed() {
                if (navigationView.isActive()) {
                    navigationView.getToolsFailed();
                }
            }
        });
    }

    @Override
    public void registerDownload(final String tool, String uuid, DynamoDBMapper dynamoDBMapper) {
        downloadCountDataRepository.registerDownload(tool, uuid, dynamoDBMapper, new DownloadCountDataSource.RegisterDownloadCallback() {
            @Override
            public void onRegisterDownloadSuccessful() {
                if (navigationView.isActive()) {
                    navigationView.onRegisterDownloadSuccessful();
                }
            }

            @Override
            public void onRegisterDownloadFailed() {
                if (navigationView.isActive()) {
                    navigationView.onRegisterDownloadFailed();
                }
            }
        });
    }
}
