package org.asl19.paskoocheh.toollist;


import android.content.Context;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;
import org.asl19.paskoocheh.pojo.AndroidTool;
import org.asl19.paskoocheh.pojo.DownloadCountList;
import org.asl19.paskoocheh.pojo.RatingList;

import java.util.List;

public interface ToolListContract {

    interface ToolListView extends BaseView<Presenter> {
        void getToolsSuccessful(List<AndroidTool> tools);

        void getToolsFailed();

        void getFeaturedSuccessful(List<AndroidTool> tools);

        void getFeaturedFailed();

        void getDownloadCountListSuccessful(DownloadCountList downloadCountList);

        void getDownloadCountListFailed();

        void getRatingListSuccessful(RatingList ratingList);

        void getRatingListFailed();

        void onRegisterDownloadSuccessful();

        void onRegisterDownloadFailed();
    }

    interface ToolListAdapter {
        Context getContext();

        void registerDownload(String tool);

        void onPermissionRequested(Integer code);
    }

    interface Presenter extends BasePresenter {
        void getAndroidTools();

        void getFeaturedTools();

        void getDownloadCountList();

        void getRatingList();

        void registerDownload(String uuid, String tool, DynamoDBMapper dynamoDBMapper);
    }
}
