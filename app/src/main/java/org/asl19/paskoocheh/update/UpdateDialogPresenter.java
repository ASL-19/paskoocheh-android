package org.asl19.paskoocheh.update;


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
    public void registerInstall(String tool, String uuid) {
        downloadCountRepository.registerInstall(tool, uuid, new DownloadCountDataSource.RegisterInstallCallback() {
            @Override
            public void onRegisterInstallSuccessful() {
                if (dialogView.isActive()) {
                    dialogView.onRegisterInstallSuccessful();
                }
            }

            @Override
            public void onRegisterInstallFailed() {
                if (dialogView.isActive()) {
                    dialogView.onRegisterInstallFailed();
                }
            }
        });
    }
}