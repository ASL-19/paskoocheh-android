package org.asl19.paskoocheh.update;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.service.ToolDownloadService;
import org.asl19.paskoocheh.utils.ApkManager;
import org.parceler.Parcels;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;


public class UpdateDialogFragment extends DialogFragment implements UpdateDialogContract.UpdateDialogView {

    public static final String TAG = UpdateDialogFragment.class.getCanonicalName();

    private Unbinder unbinder;

    private Version paskoocheh;

    private UpdateDialogContract.Presenter presenter;

    private ApkManager apkManager;

    public static UpdateDialogFragment newInstance() {
        return new UpdateDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apkManager = new ApkManager(getContext().getApplicationContext());

        paskoocheh = Parcels.unwrap(getArguments().getParcelable("PASKOOCHEH"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_update, container, false);
        unbinder = ButterKnife.bind(this, root);
        return root;
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

    @OnClick(R.id.ok)
    void update() {
        File toolFile = new File(getContext().getApplicationContext().getFilesDir() + "/" + String.format("%s_%s.apk", paskoocheh.getAppName(), paskoocheh.getVersionNumber()));

        if (toolFile.exists()) {
            apkManager.installPackage(paskoocheh, toolFile);
            getDialog().dismiss();
        } else {
            ConnectivityManager connManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
            if (!getActivity().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(DOWNLOAD_WIFI, true) || (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
                Intent intent = new Intent(getActivity(), ToolDownloadService.class);
                intent.putExtra("VERSION", Parcels.wrap(paskoocheh));
                getActivity().startService(intent);
                Toast.makeText(getContext(), getString(R.string.queued), Toast.LENGTH_SHORT).show();
            } else if ((activeNetwork != null && activeNetwork.getType() != ConnectivityManager.TYPE_WIFI)) {
                Toast.makeText(getContext(), getString(R.string.connect_wifi), Toast.LENGTH_SHORT).show();
            }
            getDialog().dismiss();
        }
    }

    @OnClick(R.id.cancel)
    void dismissTag() {
        getDialog().dismiss();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(UpdateDialogContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
