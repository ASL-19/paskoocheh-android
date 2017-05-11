package org.asl19.paskoocheh.update;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import org.asl19.paskoocheh.data.source.DownloadCountDataSource;

import lombok.NonNull;

public class UpdateDialogPresenter implements UpdateDialogContract.Presenter {

    private final UpdateDialogContract.UpdateDialogView dialogView;
    private final DownloadCountDataSource downloadCountRepository;

    public UpdateDialogPresenter(@NonNull UpdateDialogContract.UpdateDialogView dialogView, @NonNull DownloadCountDataSource downloadCountRepository) {
        this.dialogView = dialogView;
        this.downloadCountRepository = downloadCountRepository;

        this.dialogView.setPresenter(this);
    }

    @Override
    public void registerDownload(String tool, String uuid, DynamoDBMapper dynamoDBMapper) {
        downloadCountRepository.registerDownload(tool, uuid, dynamoDBMapper, new DownloadCountDataSource.RegisterDownloadCallback() {
            @Override
            public void onRegisterDownloadSuccessful() {
                if (dialogView.isActive()) {
                    dialogView.onRegisterDownloadSuccessful();
                }
            }

            @Override
            public void onRegisterDownloadFailed() {
                if (dialogView.isActive()) {
                    dialogView.onRegisterDownloadFailed();
                }
            }
        });
    }
}