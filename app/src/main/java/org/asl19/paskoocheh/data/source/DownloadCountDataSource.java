package org.asl19.paskoocheh.data.source;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import org.asl19.paskoocheh.pojo.DownloadCountList;

public interface DownloadCountDataSource {

    interface GetDownloadListCallback {

        void onGetDownloadsSuccessful(DownloadCountList downloadCountList);

        void onGetDownloadsFailed();
    }

    void getDownloadCountList(GetDownloadListCallback callback);

    interface GetDownloadCountCallback {

        void onGetDownlaodCountSuccessful(String count);

        void onGetDownloadCountFailed();
    }

    void getDownloadCount(String toolName, GetDownloadCountCallback callback);

    interface RegisterDownloadCallback {

        void onRegisterDownloadSuccessful();

        void onRegisterDownloadFailed();
    }

    void registerDownload(String uuid, String tool, DynamoDBMapper dynamoDBMapper, RegisterDownloadCallback callback);
}