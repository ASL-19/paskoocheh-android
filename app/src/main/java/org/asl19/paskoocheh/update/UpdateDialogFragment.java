package org.asl19.paskoocheh.update;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.AndroidTool;
import org.asl19.paskoocheh.service.ToolDownloadService;
import org.asl19.paskoocheh.utils.ApkManager;
import org.parceler.Parcels;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_UUID;


public class UpdateDialogFragment extends DialogFragment implements UpdateDialogContract.UpdateDialogView {

    public static final String TAG = UpdateDialogFragment.class.getCanonicalName();

    private Unbinder unbinder;

    private AndroidTool paskoocheh;

    private UpdateDialogContract.Presenter presenter;

    public static UpdateDialogFragment newInstance() {
        return new UpdateDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        File toolFile = new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + paskoocheh.getName() + ".apk");
        String uuid = getContext().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getString(PASKOOCHEH_UUID, "");

        if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, paskoocheh.getToolId().intValue());
        } else if (toolFile.exists()) {
            presenter.registerInstall(uuid, paskoocheh.getEnglishName());
            ApkManager.installPackage(getContext(), paskoocheh.getChecksum(), toolFile);
            getDialog().dismiss();
        } else {
            ConnectivityManager connManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
            if (!getActivity().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(DOWNLOAD_WIFI, true) || (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
                presenter.registerInstall(uuid, paskoocheh.getEnglishName());
                Intent intent = new Intent(getActivity(), ToolDownloadService.class);
                intent.putExtra("TOOL", Parcels.wrap(paskoocheh));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean requestGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (requestGranted) {
            Toast.makeText(getContext(), getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
            update();
        } else {
            Toast.makeText(getContext(), getString(R.string.required_write), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(UpdateDialogContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onRegisterInstallSuccessful() {

    }

    @Override
    public void onRegisterInstallFailed() {

    }
}
