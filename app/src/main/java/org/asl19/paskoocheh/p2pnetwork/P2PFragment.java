package org.asl19.paskoocheh.p2pnetwork;

import static android.view.View.VISIBLE;

import org.asl19.paskoocheh.Constants;
import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;

import static org.asl19.paskoocheh.p2pnetwork.P2PContract.Presenter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.toolinfo.ToolInfoActivity;
import org.asl19.paskoocheh.toollist.ToolListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class P2PFragment extends P2PBaseFragment implements P2PContract.ListView {
    private static final String LOGTAG = "P2PFragment";

    public static String PACKAGE_TOOSHEH="Toosheh";
    public static final String TOOL = "TOOL";
    public static final String TOOL_NAME = "TOOL_NAME";

    @BindView(R.id.successToosheh)
    LinearLayout successTooshehLayout;
    @BindView(R.id.injectToosheh)
    LinearLayout injectTooshehLayout;
    @BindView(R.id.banner_toosheh)
    ConstraintLayout bannerToosheh;
    @BindView(R.id.tv_how_to_inject)
    TextView tvHowToInject;

    private List<Version> versions = new ArrayList<>();
    private Version versionToosheh;
    private Presenter presenter;

    public static P2PFragment newInstance() {
        return new P2PFragment();
    }

    public static final String TAG = P2PFragment.class.getCanonicalName();
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isGoBackToHomeAfterInject = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_p2p, container, false);
        unbinder = ButterKnife.bind(this, root);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.OPEN_PAGE, bundle);

        tvHowToInject.setClickable(true);
        tvHowToInject.setMovementMethod(LinkMovementMethod.getInstance());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getAllTools();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onGetVersionsListSuccessful(List<Version> versionList) {

        versions.clear();
        for (Version version : versionList) {
            try {
                if (version.appName.trim().equals(PACKAGE_TOOSHEH)) {
                    versionToosheh = version;
                    try {
                        // Check to see if it is installed
                        int installedVersionCode = getContext().getApplicationContext().getPackageManager().getPackageInfo(versionToosheh.packageName, 0).versionCode;
                        versionToosheh.installed = true;
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                    Log.d("Toosheh", versionToosheh.toString());
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        successTooshehLayout.setVisibility(VISIBLE);

    }

    @Override
    public void onGetVersionsListFailed() {
        // TODO-jay - How to handle the failure case?
    }

    @Override
    protected void updateP2PUI(boolean isP2POn) {
        if (isP2POn) {
            super.updateUIAfterOuinetStart();
            injectTooshehLayout.setVisibility(VISIBLE);
            bannerToosheh.setVisibility(VISIBLE);
        } else {
            super.updateUIAfterOuinetStop();
            injectTooshehLayout.setVisibility(View.GONE);
            bannerToosheh.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.install_toosheh_banner)
    public void install(){
        ConnectivityManager connManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        if (!getActivity().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(DOWNLOAD_WIFI, true) || (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
            Intent intent = new Intent(this.getActivity(), ToolInfoActivity.class);
            intent.putExtra(TOOL, versionToosheh.toolId);
            intent.putExtra(TOOL_NAME, versionToosheh.appName);
            startActivity(intent);
        } else {
            // TODO-jay : in the else part, user should be asked to turn on the wife to complete the action.
        }
    }

    @OnClick(R.id.backToHome)
    public void backToHome(){
        startActivity(new Intent(this.getActivity(), ToolListActivity.class));
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}