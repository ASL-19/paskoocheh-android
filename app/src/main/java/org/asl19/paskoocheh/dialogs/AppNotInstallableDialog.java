package org.asl19.paskoocheh.dialogs;

import static org.asl19.paskoocheh.Constants.TOOL_ID;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.toolinfo.ToolInfoFragment;
import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class AppNotInstallableDialog extends DialogFragment {

    public static final String TAG = AppNotInstallableDialog.class.getCanonicalName();

    public static final String EXTRA_APP =  "EXTRA_APP";
    private Unbinder unbinder;

    private Version version;

    public static AppNotInstallableDialog newInstance() {
        return new AppNotInstallableDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        version = Parcels.unwrap(getArguments().getParcelable(EXTRA_APP));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_layout, container, false);
        unbinder = ButterKnife.bind(this, root);
        ((TextView)root.findViewById(R.id.dialog_text)).setText(R.string.app_not_installable_try_google_play_store_text);
        ((Button)root.findViewById(R.id.negative_btn)).setText(R.string.ok);

        if (version.downloadVia.url == null || version.downloadVia.url.isEmpty()) {
            root.findViewById(R.id.positive_btn).setVisibility(View.GONE);
        } else {
            ((Button)root.findViewById(R.id.positive_btn)).setText(R.string.try_google_play_store_btn_text);
        }

        return root;
    }

    private void playStoreRedirect() {
        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(version.getDownloadVia().getUrl())
        );
        if (browserIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            getContext().startActivity(browserIntent);

            Bundle bundle = new Bundle();
            bundle.putString(Constants.SCREEN, ToolInfoFragment.TAG);
            bundle.putString(TOOL_ID, version.getAppName());
            FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.PLAY_STORE, bundle);
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_playstore_clients), Toast.LENGTH_SHORT).show();
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.positive_btn)
    void update() {
        playStoreRedirect();
        getDialog().dismiss();
    }

    @OnClick(R.id.negative_btn)
    void dismissTag() {
        getDialog().dismiss();
    }
}
