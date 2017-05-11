package org.asl19.paskoocheh.data.source;


import org.asl19.paskoocheh.pojo.AndroidTool;

import java.util.List;

public interface ToolDataSource {

    interface GetToolsCallback {

        void onGetToolsSuccessful(List<AndroidTool> tools);

        void onGetToolsFailed();
    }

    void getAndroidTools(GetToolsCallback callback);

    void getFeaturedTools(GetToolsCallback callback);

    void getInstalledTools(GetToolsCallback callback);

    interface GetToolCallback {

        void onGetToolSuccessful(AndroidTool tool);

        void onGetToolFailed();
    }

    void getTool(long toolId, GetToolCallback callback);
}