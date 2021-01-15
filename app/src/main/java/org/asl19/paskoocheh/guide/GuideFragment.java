package org.asl19.paskoocheh.guide;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Guide;
import org.asl19.paskoocheh.utils.URLImageParser;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuideFragment extends Fragment implements GuideContract.GuideView {
    public static GuideFragment newInstance() {
        return new GuideFragment();
    }

    public static final String TAG = GuideFragment.class.getCanonicalName();
    public static final String GUIDE = "GUIDE";

    @BindView(R.id.headline)
    TextView headline;

    @BindView(R.id.content)
    TextView body;

    private GuideContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_guide, container, false);
        ButterKnife.bind(this, root);

        int guideId = -1;
        if (getArguments() != null) {
            guideId = getArguments().getInt(GUIDE);
        }

        presenter.getGuide(guideId);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.OPEN_PAGE, bundle);

        return root;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(GuideContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onGetGuideSuccessful(Guide guide) {
        headline.setText(guide.getHeadline());

        URLImageParser urlImageParser = new URLImageParser(body, getContext());
        Spanned htmlSpan = Html.fromHtml(HtmlRenderer.builder().build().render(Parser.builder().build().parse(guide.getBody())), urlImageParser, null);
        body.setText(htmlSpan);
        body.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onGetGuideFailed() {

    }
}