package org.asl19.paskoocheh.data.source;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.pojo.Tool;

import java.util.List;

public interface ToolDataSource {

    interface GetToolCallback {

        void onGetToolSuccessful(Tool tool);

        void onGetToolFailed();
    }

    interface  GetToolListCallback {

        void onGetToolListSuccessful(List<Tool> toolList);

        void onGetToolListFailed();
    }

    void getTools(GetToolListCallback callback);

    void getTool(long ToolId, GetToolCallback callback);

    void saveTool(@NonNull Tool... tool);

    void clearTable();
}