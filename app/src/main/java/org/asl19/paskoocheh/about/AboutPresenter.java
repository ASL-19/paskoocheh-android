package org.asl19.paskoocheh.about;


import org.asl19.paskoocheh.data.source.TextDataSource;
import org.asl19.paskoocheh.pojo.Text;

import java.util.List;

public class AboutPresenter implements AboutContract.Presenter {

    private AboutContract.AboutView aboutView;
    private TextDataSource textRepository;

    public AboutPresenter(AboutContract.AboutView aboutView, TextDataSource textRepository) {
        this.aboutView = aboutView;
        this.textRepository = textRepository;

        this.aboutView.setPresenter(this);
    }
    @Override
    public void getText() {
        textRepository.getTexts(new TextDataSource.GetTextsCallback() {
            @Override
            public void onGetTextsSuccessful(List<Text> texts) {
                if (aboutView.isActive()) {
                    aboutView.onGetTextSuccessful(texts.get(0));
                }
            }

            @Override
            public void onGetTextsFailed() {
                if (aboutView.isActive()) {
                    aboutView.onGetTextFailed();
                }
            }
        });
    }
}
