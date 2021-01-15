package org.asl19.paskoocheh.about;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
import butterknife.Unbinder;

public class AboutFragment extends Fragment implements AboutContract.AboutView {

    public static final String TAG = AboutFragment.class.getCanonicalName();

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    private AboutContract.Presenter presenter;

    private Unbinder unbinder;

    @BindView(R.id.paskoocheh_about_body)
    TextView aboutTextview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        unbinder = ButterKnife.bind(this, view);

        aboutTextview.setMovementMethod(LinkMovementMethod.getInstance());

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.OPEN_PAGE, bundle);

        presenter.getText();

        return view;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(AboutContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onGetTextSuccessful(Text text) {
        aboutTextview.setText(Html.fromHtml(HtmlRenderer.builder().build().render(Parser.builder().build().parse(text.getAbout()))));
    }

    @Override
    public void onGetTextFailed() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
