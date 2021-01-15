package org.asl19.paskoocheh.feedback;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackFragment extends Fragment implements FeedbackContract.FeedbackView{
    public static FeedbackFragment newInstance() {
        return new FeedbackFragment();
    }

    public static final String TAG = FeedbackFragment.class.getCanonicalName();

    @BindView(R.id.success)
    LinearLayout successLayout;

    @BindView(R.id.form_content)
    NestedScrollView nestedScrollView;

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.email_error)
    TextView emailError;

    @BindView(R.id.email_subject)
    EditText emailSubject;

    @BindView(R.id.email_subject_error)
    TextView emailSubjectError;

    @BindView(R.id.explanation)
    EditText explanation;

    @BindView(R.id.explanation_error)
    TextView explanationError;

    @BindView(R.id.submit_feedback)
    TextView submitFeedback;

    private FeedbackContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feedback, container, false);
        ButterKnife.bind(this, root);

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
    public void setPresenter(FeedbackContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @OnClick(R.id.submit_feedback)
    void submitFeedback() {
        emailError.setVisibility(View.GONE);
        emailSubjectError.setVisibility(View.GONE);
        explanationError.setVisibility(View.GONE);


        boolean formComplete = true;
        if (email.getText().length() == 0 || !Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            formComplete = false;
            emailError.setVisibility(View.VISIBLE);
        }

        if (emailSubject.getText().length() == 0) {
            formComplete = false;
            emailSubjectError.setVisibility(View.VISIBLE);
        }

        if (explanation.getText().length() == 0) {
            formComplete = false;
            explanationError.setVisibility(View.VISIBLE);
        }

        if (formComplete) {
            ActivityUtils.sendEmail(String.valueOf(getText(R.string.support_email)), String.valueOf(emailSubject.getText()), String.valueOf(explanation.getText()), getActivity());
            onSubmitFeedbackSuccessful();
        }
    }

    @Override
    public void onSubmitFeedbackSuccessful() {
        successLayout.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        nestedScrollView.setVisibility(View.GONE);
        submitFeedback.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        }, 2000);
    }

    @Override
    public void onSubmitFeedbackFailed() {
        Toast.makeText(getContext(), R.string.time_out_retry_check_connection, Toast.LENGTH_SHORT).show();
    }
}