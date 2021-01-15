package org.asl19.paskoocheh.data.source;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.pojo.Tutorial;

import java.util.List;

public interface TutorialDataSource {

    interface GetTutorialListCallback {

        void onGetTutorialsSuccessful(List<Tutorial> tutorials);

        void onGetTutorialsFailed();
    }

    void getToolTutorial(int toolId, GetTutorialListCallback callback);

    void saveTutorial(@NonNull final Tutorial... tutorials);

    void clearTable();
}