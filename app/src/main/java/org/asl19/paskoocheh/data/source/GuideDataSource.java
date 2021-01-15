package org.asl19.paskoocheh.data.source;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.pojo.Guide;

import java.util.List;

public interface GuideDataSource {

    void getGuide(int guideId, GetGuideCallback callback);

    interface GetGuideListCallback {

        void onGetGuidesSuccessful(List<Guide> guideList);

        void onGetGuidesFailed();
    }

    interface GetGuideCallback {

        void onGetGuideSuccessful(Guide guide);

        void onGetGuideFailed();
    }

    void getToolGuides(int toolId, GetGuideListCallback callback);

    void saveGuides(@NonNull final Guide... guides);

    void clearTable();
}