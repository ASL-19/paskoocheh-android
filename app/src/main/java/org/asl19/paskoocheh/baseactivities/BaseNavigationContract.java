package org.asl19.paskoocheh.baseactivities;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import org.asl19.paskoocheh.pojo.AndroidTool;

import java.util.List;

public interface BaseNavigationContract {

    interface NavigationView extends BaseView<Presenter> {
        void getInstalledToolsSuccessful(List<AndroidTool> tools);

        void getInstalledToolsFailed();

        void getToolsSuccessful(List<AndroidTool> tools);

        void getToolsFailed();

        void onRegisterDownloadSuccessful();

        void onRegisterDownloadFailed();
    }

    interface Presenter extends BasePresenter {
        void getInstalledTools();

        void getAndroidTools();

        void registerDownload(String uuid, String tool, DynamoDBMapper dynamoDBMapper);
    }
}