package org.asl19.paskoocheh.data.source;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.pojo.LocalizedInfo;

import java.util.List;

public interface LocalizedInfoDataSource {

    interface GetLocalizedInfoCallback {

        void onGetLocalizedInfoSuccessful(LocalizedInfo localizedInfo);

        void onGetLocalizedInfoFailed();
    }

    interface GetLocalizedInfoListCallback {

        void onGetLocalizedInfoListSuccessful(List<LocalizedInfo> localizedInfo);

        void onGetLocalizedInfoListFailed();
    }

    void getLocalizedInfo(long localizedInfoId, GetLocalizedInfoCallback callback);

    void getLocalizedInfoList(GetLocalizedInfoListCallback callback);

    void saveLocalizedInfo(@NonNull LocalizedInfo localizedInfo);

    void clearTable();
}