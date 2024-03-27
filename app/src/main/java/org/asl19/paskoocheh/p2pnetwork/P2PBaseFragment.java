package org.asl19.paskoocheh.p2pnetwork;

import static org.asl19.paskoocheh.Constants.OUINET_PREF;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.OUINET_DIR;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.OuinetService;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.injectdirectory.InjectActivity;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class P2PBaseFragment extends P2POuinetStatusReceiverFragment {
    private static final String LOGTAG = "P2PBaseFragment";

    public static final int FOLDERPICKER_PERMISSIONS = 1;
    public static final String USER_ACCEPTED_OUINET_ALERT_PREF = "USER_ACCEPTED_OUINET_ALERT";

    private static final String EXTRA_IS_COMMUNICATION_WITH_OUINET_INITIATED = "is_communication_with_ouinet_initiated";
    private static final String EXTRA_IS_OUINET_STOP_REQUEST_INITIATED = "is_ouinet_stop_request_initiated";
    private static final String EXTRA_IS_SAVED_INSTANCE_STATE_CALLED = "is_saved_instance_state_called";
    private static final String EXTRA_PENDING_OUINET_STATUS_OPERATION_SUCCESSFUL = "pending_ouinet_status_operation_successful";

    @BindView(R.id.p2pEnableButton)
    SwitchCompat ouinetSwitch;

    protected boolean alertAccepted = false;
    protected boolean isGoBackToHomeAfterInject = true;
    protected P2PAlerts mP2PAlerts;

    private boolean isCommunicationWithOuinetInitiated;
    private boolean isOuinetStopRequestInitiated;

    private Boolean pendingOuinetStatusOperationSuccessful = null;
    private boolean isSavedInstanceStateCalled = false;

    protected abstract void updateP2PUI(boolean isP2POn);

    public P2PBaseFragment() {
    }

    @Override
    protected void onOuinetStatusUpdate(boolean operationSuccessful) {

        if(isSavedInstanceStateCalled) {
            // The App is in background while this method was called by the OuinetStatusReceiver in the parent P2POuinetStatusReceiverFragment.
            Log.v(LOGTAG, "onOuinetStatusUpdate isSavedInstanceStateCalled = true; operationSuccessful = " + operationSuccessful);
            pendingOuinetStatusOperationSuccessful = operationSuccessful;
            return;
        }

        pendingOuinetStatusOperationSuccessful = null;

        Log.v(LOGTAG, "onOuinetStatusUpdate operationSuccessful = " + operationSuccessful);
        if (isCommunicationWithOuinetInitiated) {
            alertsOnP2POperationCompletion(getActivity(), getParentFragmentManager(), ouinetSwitch.isChecked(), mP2PAlerts, operationSuccessful);
            isCommunicationWithOuinetInitiated = false;
            isOuinetStopRequestInitiated = false;
        }

        updateP2PUI(PaskoochehApplication.getInstance().isServiceRunning(OuinetService.class));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        isSavedInstanceStateCalled = true;
        outState.putBoolean(EXTRA_IS_COMMUNICATION_WITH_OUINET_INITIATED, isCommunicationWithOuinetInitiated);
        outState.putBoolean(EXTRA_IS_OUINET_STOP_REQUEST_INITIATED, isOuinetStopRequestInitiated);
        outState.putBoolean(EXTRA_IS_SAVED_INSTANCE_STATE_CALLED, true);
        if (pendingOuinetStatusOperationSuccessful != null) {
            outState.putBoolean(EXTRA_PENDING_OUINET_STATUS_OPERATION_SUCCESSFUL, pendingOuinetStatusOperationSuccessful);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mP2PAlerts = PaskoochehApplication.getInstance().getP2PAlerts();

        if (savedInstanceState != null) {
            isCommunicationWithOuinetInitiated = savedInstanceState.getBoolean(EXTRA_IS_COMMUNICATION_WITH_OUINET_INITIATED, false);
            isOuinetStopRequestInitiated = savedInstanceState.getBoolean(EXTRA_IS_OUINET_STOP_REQUEST_INITIATED, false);
            isSavedInstanceStateCalled = savedInstanceState.getBoolean(EXTRA_IS_SAVED_INSTANCE_STATE_CALLED, false);
            if (savedInstanceState.containsKey(EXTRA_PENDING_OUINET_STATUS_OPERATION_SUCCESSFUL)) {
                pendingOuinetStatusOperationSuccessful = savedInstanceState.getBoolean(EXTRA_PENDING_OUINET_STATUS_OPERATION_SUCCESSFUL);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        isSavedInstanceStateCalled = false;

        if (pendingOuinetStatusOperationSuccessful != null) {
            Log.v(LOGTAG, "onResume: calling onOuinetStatusUpdate since pendingOuinetStatusOperationSuccessful is not null = " + pendingOuinetStatusOperationSuccessful);
            onOuinetStatusUpdate(pendingOuinetStatusOperationSuccessful);
        }

        if (!isOuinetStopRequestInitiated) {
            // We do not call ouinetSwitch.setChecked if the user initiated Ouinet stop request since the OuinetService
            // might still be running and so in case of orientation change if we execute  the following statement it will set the switch to a wrong value (i.e. true / ON).
            ouinetSwitch.setChecked(PaskoochehApplication.getInstance().isServiceRunning(OuinetService.class));
        }
        updateP2PUI(ouinetSwitch.isChecked());
    }

    public static void alertsOnP2POperationCompletion(Activity activity, FragmentManager fragmentManager, boolean isP2PSwitchOn, P2PAlerts p2PAlerts, boolean operationSuccessful) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, activity.getComponentName().getClassName());

        if (isP2PSwitchOn) {
            p2PAlerts.hideP2PConnectingAlert();
            if (operationSuccessful) {
                p2PAlerts.showP2PConnectSuccessAlert(fragmentManager);
                FirebaseAnalytics.getInstance(activity).logEvent(Constants.P2P_CONNECT_SUCCESS, bundle);
            } else {
                FirebaseAnalytics.getInstance(activity).logEvent(Constants.P2P_CONNECT_FAIL, bundle);
                p2PAlerts.showP2PConnectFailAlert(fragmentManager);
            }
        } else {
            p2PAlerts.hideP2PDisconnectingAlert();
            FirebaseAnalytics.getInstance(activity).logEvent(Constants.P2P_DISCONNECT, bundle);
        }
    }

    protected void updateUIAfterOuinetStart() {
        setOuinetPref(true);
        ouinetSwitch.setChecked(true);
        ouinetSwitch.setText(ouinetSwitch.getTextOn());
    }

    protected void updateUIAfterOuinetStop() {
        setOuinetPref(false);
        ouinetSwitch.setChecked(false);
        ouinetSwitch.setText(ouinetSwitch.getTextOff());
    }

    private void setOuinetPref(boolean isP2PEnabled) {
        this.getActivity().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).edit().putBoolean(OUINET_PREF, isP2PEnabled).commit();
    }

    //Adding the alert to verify the understand of P2P network seeding.
    private void simpleAlert(FragmentActivity view) {
        final Dialog dialog = new Dialog(view);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);

        Button positive_btn = dialog.findViewById(R.id.positive_btn);
        Button negative_btn = dialog.findViewById(R.id.negative_btn);

        positive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOuinetServiceUpdateUI();
                P2PBaseFragment.this.getActivity().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).edit()
                        .putBoolean(USER_ACCEPTED_OUINET_ALERT_PREF, true).commit();
                alertAccepted = true;
                dialog.dismiss();
            }
        });
        negative_btn.setVisibility(View.GONE);
        dialog.setOnDismissListener(dialog1 -> {
            if (!alertAccepted) {
                ouinetSwitch.setChecked(false);
            }
        });

        dialog.show();
    }

    private void startOuinetServiceUpdateUI() {
        updateP2PUI(true);
        isCommunicationWithOuinetInitiated = true;
        mP2PAlerts.showP2PConnectingAlert(getParentFragmentManager());
        PaskoochehApplication.getInstance().startOuinetService();
    }

    @OnClick(R.id.p2pEnableButton)
    void enableP2PButton(){
        Log.d("Ouinet Checkbox","Checked box");

        if (ouinetSwitch.isChecked()) {
            if (PaskoochehApplication.getInstance().isServiceRunning(OuinetService.class)) {
                return;
            }
            alertAccepted = getActivity().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE)
                    .getBoolean(USER_ACCEPTED_OUINET_ALERT_PREF, false);
            if (!alertAccepted) {
                ouinetSwitch.setText(ouinetSwitch.getTextOn());
                simpleAlert(getActivity());
            } else {
                startOuinetServiceUpdateUI();
            }
        } else {
            if (!PaskoochehApplication.getInstance().isServiceRunning(OuinetService.class)) {
                return;
            }
            isCommunicationWithOuinetInitiated = true;
            isOuinetStopRequestInitiated = true;
            mP2PAlerts.showP2PDisconnectingAlert(getParentFragmentManager());
            PaskoochehApplication.getInstance().stopOuinetService();
            updateP2PUI(false);
        }
    }

    @OnClick(R.id.browse_directory)
    void browseDirectory() {
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if(PaskoochehApplication.hasPermissions(this.getActivity(), PERMISSIONS)) {
            ShowDirectoryPicker();
        }else{
            requestPermissions(PERMISSIONS, FOLDERPICKER_PERMISSIONS);
        }
    }

    //Storagechooser............
    /**
     * Method that displays the directory chooser of the StorageChooser.
     */
    private void ShowDirectoryPicker(){
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
     * Callback that handles the status of the permissions request.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("Permission", "In Fragment");
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

    private void getOuinetRootDirectory(String folderLocation) {
        Log.i( "make dir Location", "in make dire of ouinet" );
        this.getActivity().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).edit().putString(OUINET_DIR,folderLocation).commit();
        Intent intent = new Intent(this.getActivity(), InjectActivity.class);
        intent.putExtra(InjectActivity.EXTRA_IS_GO_BACK_TO_HOME_AFTER_INJECT, isGoBackToHomeAfterInject);
        startActivity(intent);
    }
}