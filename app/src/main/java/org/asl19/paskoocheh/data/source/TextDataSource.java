package org.asl19.paskoocheh.data.source;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.pojo.Text;

import java.util.List;

public interface TextDataSource {

    interface GetTextsCallback {

        void onGetTextsSuccessful(List<Text> texts);

        void onGetTextsFailed();
    }

    void getTexts(GetTextsCallback callback);

    void saveTexts(@NonNull Text... texts);

    void clearTable();
}