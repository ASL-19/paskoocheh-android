package org.asl19.paskoocheh.guide;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.data.source.GuideDataSource;
import org.asl19.paskoocheh.pojo.Guide;

public class GuidePresenter implements GuideContract.Presenter {

    private final GuideContract.GuideView guideView;
    private GuideDataSource guideRepository;

    public GuidePresenter(@NonNull GuideContract.GuideView guideView, @NonNull GuideDataSource guideDataSource) {
        this.guideView = guideView;
        this.guideRepository = guideDataSource;

        this.guideView.setPresenter(this);
    }

    @Override
    public void getGuide(int id) {
        guideRepository.getGuide(id, new GuideDataSource.GetGuideCallback() {
            @Override
            public void onGetGuideSuccessful(Guide guide) {
                if (guideView.isActive()) {
                    guideView.onGetGuideSuccessful(guide);
                }
            }

            @Override
            public void onGetGuideFailed() {
                if (guideView.isActive()) {
                    guideView.onGetGuideFailed();
                }
            }
        });
    }
}
