package org.asl19.paskoocheh.rating;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.data.SendReviewRequest;
import org.asl19.paskoocheh.pojo.AndroidTool;
import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_UUID;
import static org.asl19.paskoocheh.Constants.TOOL_ID;

public class RatingDialogFragment extends DialogFragment implements RatingDialogContract.RatingDialogView {

    public static final String TAG = RatingDialogFragment.class.getCanonicalName();


    private static final String INACTIVE_STAR = "#CCCCCC";
    private static final String STAR_COLOUR = "#FFA131";
    private static final String REVIEW_TEXT_COLOUR = "#0B233C";

    @Inject
    CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider;

    @BindView(R.id.review_title)
    TextInputEditText reviewTitle;

    @BindView(R.id.review_body)
    TextInputEditText reviewBody;

    @BindView(R.id.submit_review)
    Button submitReview;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.rating_bar_dialog)
    RatingBar ratingBarDialog;

    private Unbinder unbinder;
    private RatingDialogContract.Presenter presenter;
    private AndroidTool tool;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tool_info_review, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getActivity()).logEvent(Constants.OPEN_PAGE, bundle);

        ((PaskoochehApplication) getActivity().getApplicationContext()).getAmazonComponenet().inject(this);

        if (getArguments() != null) {
            ratingBarDialog.setRating(getArguments().getFloat("RATING"));
            tool = Parcels.unwrap(getArguments().getParcelable("TOOL"));
        }

        LayerDrawable stars = (LayerDrawable) ratingBarDialog.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor(STAR_COLOUR), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.parseColor(STAR_COLOUR), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.parseColor(INACTIVE_STAR), PorterDuff.Mode.SRC_ATOP);

        reviewTitle.getBackground().setColorFilter(
                Color.parseColor(REVIEW_TEXT_COLOUR), PorterDuff.Mode.SRC_IN
        );
        reviewBody.getBackground().setColorFilter(
                Color.parseColor(REVIEW_TEXT_COLOUR), PorterDuff.Mode.SRC_IN
        );

        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    submitReview.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.getIndeterminateDrawable().setColorFilter(
                            ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark),
                            android.graphics.PorterDuff.Mode.SRC_IN);

                    String uuid = getActivity().getSharedPreferences(
                            PASKOOCHEH_PREFS,
                            Context.MODE_PRIVATE).getString(PASKOOCHEH_UUID, ""
                    );

                    SendReviewRequest sendReviewRequest = new SendReviewRequest();
                    sendReviewRequest.setId(uuid);
                    sendReviewRequest.setAppName(tool.getEnglishName());
                    sendReviewRequest.setVersion(tool.getBuildVersion());
                    sendReviewRequest.setTitle(reviewTitle.getText().toString());
                    sendReviewRequest.setText(reviewBody.getText().toString());
                    sendReviewRequest.setRating(ratingBarDialog.getRating());
                    sendReviewRequest.setCognito(cognitoCachingCredentialsProvider.getIdentityId());
                    presenter.submitReview(sendReviewRequest);

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.SCREEN, TAG);
                    bundle.putString(TOOL_ID, tool.getEnglishName());
                    FirebaseAnalytics.getInstance(view.getContext()).logEvent(Constants.REVIEW, bundle);
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onSubmitReviewSuccessful() {
        Toast.makeText(getActivity(), R.string.review_submitted, Toast.LENGTH_LONG).show();
        getDialog().dismiss();
    }

    @Override
    public void onSubmitReviewFailed() {
        Toast.makeText(getActivity(), R.string.unable_to_submit_review, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
        submitReview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(RatingDialogContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
