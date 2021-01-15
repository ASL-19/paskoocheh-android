package org.asl19.paskoocheh.data.source;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.pojo.Name;

import java.util.List;

public interface NameDataSource {

    interface GetNameListCallback {

        void onGetNamesSuccessful(List<Name> names);

        void onGetNamesFailed();
    }

    void getNames(GetNameListCallback callback);

    void saveNames(@NonNull final Name... names);

    void clearTable();
}