package org.asl19.paskoocheh.terms;


import org.asl19.paskoocheh.data.source.TextDataSource;
import org.asl19.paskoocheh.pojo.Text;

import java.util.List;

public class TermsPresenter implements TermsContract.Presenter {

    private TextDataSource textRepository;
    private TermsContract.TermsView termsView;

    public TermsPresenter(TermsContract.TermsView termsView, TextDataSource textRepository) {
        this.termsView = termsView;
        this.textRepository = textRepository;

        this.termsView.setPresenter(this);
    }

    @Override
    public void getText() {
        textRepository.getTexts(new TextDataSource.GetTextsCallback() {
            @Override
            public void onGetTextsSuccessful(List<Text> texts) {
                if (termsView.isActive()) {
                    termsView.onGetTextSuccessful(texts.get(0));
                }
            }

            @Override
            public void onGetTextsFailed() {
                if (termsView.isActive()) {
                    termsView.onGetTextFailed();
                }
            }
        });
    }
}
