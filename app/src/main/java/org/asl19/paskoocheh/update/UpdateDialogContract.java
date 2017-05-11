package org.asl19.paskoocheh.update;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import org.asl19.paskoocheh.baseactivities.BasePresenter;
import org.asl19.paskoocheh.baseactivities.BaseView;

public interface UpdateDialogContract {

    interface UpdateDialogView extends BaseView<Presenter> {

        void onRegisterDownloadSuccessful();

        void onRegisterDownloadFailed();
    }

    interface Presenter extends BasePresenter {

        void registerDownload(String uuid, String tool, DynamoDBMapper dynamoDBMapper);
    }
}