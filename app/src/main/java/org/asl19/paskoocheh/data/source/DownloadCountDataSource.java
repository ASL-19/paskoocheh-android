package org.asl19.paskoocheh.data.source;


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

    interface RegisterInstallCallback {

        void onRegisterInstallSuccessful();

        void onRegisterInstallFailed();
    }

    void registerInstall(String uuid, String tool, RegisterInstallCallback callback);
}