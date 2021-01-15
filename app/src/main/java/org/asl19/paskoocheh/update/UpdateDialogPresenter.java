package org.asl19.paskoocheh.update;


import lombok.NonNull;

public class UpdateDialogPresenter implements UpdateDialogContract.Presenter {

    private final UpdateDialogContract.UpdateDialogView dialogView;

    public UpdateDialogPresenter(@NonNull UpdateDialogContract.UpdateDialogView dialogView) {
        this.dialogView = dialogView;

        this.dialogView.setPresenter(this);
    }
}