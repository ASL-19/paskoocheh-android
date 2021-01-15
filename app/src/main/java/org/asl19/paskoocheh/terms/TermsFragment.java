package org.asl19.paskoocheh.terms;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsFragment extends Fragment implements TermsContract.TermsView {
    @BindView(R.id.privacy)
    TextView privacy;

    @BindView(R.id.terms)
    TextView terms;

    private TermsContract.Presenter presenter;

    public static TermsFragment newInstance() {
        return new TermsFragment();
    }

    public static final String TAG = TermsFragment.class.getCanonicalName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_terms, container, false);
        ButterKnife.bind(this, root);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.OPEN_PAGE, bundle);

        presenter.getText();

        return root;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(TermsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onGetTextSuccessful(Text text) {

        String terms = HtmlRenderer.builder().build().render(Parser.builder().build().parse(text.getTermsAndPrivacy()));
        terms = terms.replace("<li>", "<p>\u2022 ");
        terms = terms.replace("</li>", "</p>");
        terms = terms.replace("<p>", "<p>\u200F");
        privacy.setText(Html.fromHtml(terms));
    }

    @Override
    public void onGetTextFailed() {

    }
}
