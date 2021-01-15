package org.asl19.paskoocheh.data.source;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.pojo.LastModified;

public interface LastModifiedDataSource {

    interface GetLastModifiedCallback {

        void onGetLastModifiedSuccessful(LastModified lastModified);

        void onGetLastModifiedFailed();
    }

    void getLastModified(String configFile, LastModifiedDataSource.GetLastModifiedCallback callback);

    void saveLastModified(@NonNull final LastModified... lastModifieds);

    void clearTable();
}
