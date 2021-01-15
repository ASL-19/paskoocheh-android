package org.asl19.paskoocheh.data.source;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.pojo.Version;

import java.util.HashSet;
import java.util.List;

public interface VersionDataSource {


    interface GetVersionsCallback {

        void onGetVersionsSuccessful(List<Version> versions);

        void onGetVersionsFailed();
    }

    void getUpdatedAndroidVersion(GetVersionsCallback getVersionsCallback);

    void getCategoryVersions(Integer categoryId, GetVersionsCallback callback);

    void getAndroidVersions(GetVersionsCallback callback);

    void getSearchAndroidVersions(HashSet<Integer> searchTerm, HashSet<String> category, String query, GetVersionsCallback callback);

    void getInstalledVersions(GetVersionsCallback callback);

    interface GetVersionCallback {

        void onGetVersionSuccessful(Version version);

        void onGetVersionFailed();
    }

    void getVersion(long versionId, GetVersionCallback callback);

    void saveVersion(@NonNull Version... version);

    void clearTable();
}