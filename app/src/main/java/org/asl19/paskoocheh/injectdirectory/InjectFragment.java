package org.asl19.paskoocheh.injectdirectory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.firebase.analytics.FirebaseAnalytics;
import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.OuinetService;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.p2pnetwork.P2POuinetStatusReceiverFragment;
import org.asl19.paskoocheh.toollist.ToolListActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.View.VISIBLE;
import static org.asl19.paskoocheh.Constants.OUINET_DIR;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.p2pnetwork.P2PBaseFragment.FOLDERPICKER_PERMISSIONS;


public class InjectFragment extends P2POuinetStatusReceiverFragment implements InjectContract.InjectView {
    private InjectContract.Presenter presenter;
    private boolean isGoBackToHomeAfterInject;

    public static InjectFragment newInstance(boolean isGoBackToHome) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(InjectActivity.EXTRA_IS_GO_BACK_TO_HOME_AFTER_INJECT, isGoBackToHome);
        InjectFragment injectFragment = new InjectFragment();
        injectFragment.setArguments(bundle);
        return injectFragment;
    }

    @BindView(R.id.scroll_view_inject)
    ScrollView scroll_view_inject;

    @BindView(R.id.inject_layout_container)
    LinearLayout inject_layout_container;

    @BindView(R.id.progressbar_layout)
    LinearLayout progressbar_layout;
    public static final String TAG = InjectFragment.class.getCanonicalName();

    @BindView(R.id.inject_button_layout)
    LinearLayout inject_button_layout;

    @BindView(R.id.inject_success_layout)
    LinearLayout inject_success_layout;

    @BindView(R.id.directory_change_view)
    LinearLayout directory_change_view;

    @BindView(R.id.frame_directory)
    ImageView frame_directoryimg;

    @BindView(R.id.frame_directorytext)
    TextView frame_directorytext;

    @BindView(R.id.directory_path)
    TextView directoryPath;

    @BindView(R.id.tv_p2p_directory_select_error)
    TextView p2pInjectErrorMessage;

    @BindView(R.id.button_inject)
    Button injectButton;

    @BindView(R.id.button_cancel)
    Button cancelButton;

    @BindView(R.id.button_change)
    Button changeButton;

    @BindView(R.id.button_browse)
    Button browseAppButton;

    boolean injectSuccess = false;
    boolean flagDirectoryInjectSuccess = false;
    private Unbinder unbinder;
    private String directoryForInject;
    private ProgressDialog progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_inject, container, false);
        unbinder = ButterKnife.bind(this, root);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.OPEN_PAGE, bundle);
        injectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar_layout.setVisibility(VISIBLE);
                scroll_view_inject.setFillViewport(true);
                PaskoochehApplication app = PaskoochehApplication.getInstance();
                inject_layout_container.setVisibility(View.GONE);
                boolean success = app.setOuinetCacheStaticContentPath(directoryForInject);
                if (success) {
                    // Wait until the Ouinet restart is complete. Inside onOuinetStatusUpdate in this fragment call injectSuccessful().
                } else {
                    injectUnsuccessful();
                }
            }
        });

        isGoBackToHomeAfterInject = getArguments().getBoolean(InjectActivity.EXTRA_IS_GO_BACK_TO_HOME_AFTER_INJECT, true);
        browseAppButton.setVisibility(isGoBackToHomeAfterInject ? VISIBLE : View.GONE);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressbar_layout.setVisibility(View.GONE);
        scroll_view_inject.setFillViewport(false);
        directoryForInject = this.getActivity().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE)
                .getString(OUINET_DIR, "Download");
        directoryPath.setText(directoryForInject);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(InjectContract.Presenter presenter) {
        this.presenter = presenter;
    }

    private void injectSuccessful() {
        progressbar_layout.setVisibility(View.GONE);
        scroll_view_inject.setFillViewport(false);
        inject_layout_container.setVisibility(VISIBLE);
        frame_directoryimg.setBackgroundResource(R.drawable.frame_directorysuccess);
        frame_directorytext.setText(R.string.injection_w);
        directory_change_view.setVisibility(View.GONE);
        inject_success_layout.setVisibility(VISIBLE);
        inject_button_layout.setVisibility(View.GONE);
        p2pInjectErrorMessage.setVisibility(View.INVISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, getActivity().getComponentName().getClassName());
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.P2P_INJECT_SUCCESS, bundle);
    }

    private void injectUnsuccessful() {
        progressbar_layout.setVisibility(View.GONE);
        scroll_view_inject.setFillViewport(false);
        inject_layout_container.setVisibility(VISIBLE);
        frame_directoryimg.setBackgroundResource(R.drawable.frame_directoryfailure);
        frame_directorytext.setTextColor(getResources().getColor(R.color.red));
        frame_directorytext.setText(R.string.the_directo);
        directory_change_view.setBackgroundResource(R.drawable.rectangle_error);
        inject_success_layout.setVisibility(View.GONE);
        inject_button_layout.setVisibility(VISIBLE);
        p2pInjectErrorMessage.setVisibility(VISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, getActivity().getComponentName().getClassName());
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.P2P_INJECT_FAIL, bundle);
    }

    @OnClick(R.id.button_change)
    public void changeDirectory(){
        //progressBar.setVisibility(View.VISIBLE);
        //startActivity(new Intent(this.getActivity(), ToolListActivity.class));
        ShowDirectoryPicker();

        //P2PFragment.showDirectoryPicker();
    }

    @OnClick(R.id.button_browse)
    public void browseApp(){
        startActivity(new Intent(this.getActivity(), ToolListActivity.class));
        getActivity().finish();
    }

    @OnClick(R.id.button_cancel)
    public void cancelDirectoryInjection(){
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void onOuinetStatusUpdate(boolean operationSuccessful) {
        injectSuccessful(); // irrespective of the value of operationSuccessful, call injectSuccessful() here.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean checkDirectory(String directoryForInject) {
        File file = new File(directoryForInject+ "/" + PaskoochehApplication.OUINET_DIR_NAME);
        boolean isDirectory = file.isDirectory();
        return isDirectory;
    }

    public void changeDirectoryText() {
        directoryForInject = this.getActivity().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE)
                .getString(OUINET_DIR, "Download");
        directoryPath.setText(directoryForInject);
        directory_change_view.setBackgroundResource(R.drawable.rectangle_2);
        p2pInjectErrorMessage.setVisibility(View.INVISIBLE);
        frame_directoryimg.setBackgroundResource(R.drawable.frame_directory);
        frame_directorytext.setTextColor(getResources().getColor(R.color.textColor));
        frame_directorytext.setText(R.string.following_d);
    }

    //Storagechooser............
    /**
     * Method that displays the directory chooser of the StorageChooser.
     */
    public void ShowDirectoryPicker(){
        // 1. Initialize dialog
        final StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(this.getActivity())
                .withFragmentManager(this.getActivity().getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();

        // 2. Retrieve the selected path by the user and show in a toast !
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                getOuinetRootDirectory(path);
//                Toast.makeText(getActivity(), "The selected path is : " + path, Toast.LENGTH_SHORT).show();
            }
        });

        // 3. Display File Picker !
        chooser.show();
    }

    /**
     * Helper method that verifies whether the permissions of a given array are granted or not.
     *
     * @param context
     * @param permissions
     * @return {Boolean}
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Callback that handles the status of the permissions request.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FOLDERPICKER_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ShowDirectoryPicker();
                } else {
                    Toast.makeText(
                            this.getActivity(),
                            "Permission denied to read your External storage :(",
                            Toast.LENGTH_SHORT
                    ).show();
                }

                return;
            }
        }
    }

    public void getOuinetRootDirectory(String folderLocation) {
        Log.i( "make dir Location", "in make dire of ouinet" );
        this.getActivity().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).edit().putString(OUINET_DIR,folderLocation).commit();
        changeDirectoryText();
    }

    public void stopOuinetFromActivity(){
        if (PaskoochehApplication.getInstance().isServiceRunning(OuinetService.class)) {
            Log.d("In inject Activity stop", " --------- Stopping ouinet service");
            PaskoochehApplication.getInstance().stopOuinetService();
        }
    }
}
