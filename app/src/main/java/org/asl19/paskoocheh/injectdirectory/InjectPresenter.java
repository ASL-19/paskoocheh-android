package org.asl19.paskoocheh.injectdirectory;



import org.asl19.paskoocheh.injectdirectory.InjectContract;

public class InjectPresenter implements InjectContract.Presenter{
    private final InjectContract.InjectView injectView;
    public InjectPresenter(InjectContract.InjectView injectView) {
        this.injectView = (InjectContract.InjectView) injectView;
        this.injectView.setPresenter((InjectContract.Presenter) this);
    }

}
