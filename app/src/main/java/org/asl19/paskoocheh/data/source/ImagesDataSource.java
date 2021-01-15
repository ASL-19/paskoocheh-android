package org.asl19.paskoocheh.data.source;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.pojo.Images;

import java.util.List;

public interface ImagesDataSource {

    interface GetImageListCallback {

        void onGetImageListSuccessful(List<Images> images);

        void onGetImageListFailed();
    }

    interface GetImagesCallback {

        void onGetImagesSuccessful(Images images);

        void onGetImagesFailed();
    }

    void getVersionImages(long versionId, GetImagesCallback callback);

    void getToolImages(long toolId, GetImagesCallback callback);

    void getImages(GetImageListCallback callback);

    void saveImages(@NonNull Images... images);

    void clearTable();
}