package org.asl19.paskoocheh.p2ploading;

import static org.asl19.paskoocheh.Constants.APPS;
import static org.asl19.paskoocheh.service.PaskoochehConfigService.CONFIG;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;

import org.asl19.paskoocheh.OuinetService;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.event.Event;
import org.asl19.paskoocheh.p2pnetwork.P2PBaseFragment;
import org.asl19.paskoocheh.service.PaskoochehConfigService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoadingP2PFragment extends P2PBaseFragment {

    public static final String TAG = LoadingP2PFragment.class.getCanonicalName();

    public static LoadingP2PFragment newInstance() {
        return new LoadingP2PFragment();
    }

    public LoadingP2PFragment() {}
    @BindView(R.id.group_progress_spinner)
    Group progressSpinner;

    @BindView(R.id.p2p_status_image_loading_screen)
    ImageView p2pStatusImage;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.description)
    TextView description;

    @BindView(R.id.group_inject_folder)
    Group groupInjectFolder;

    @BindView(R.id.group_p2p)
    Group groupP2P;

    @BindView(R.id.tv_how_to_inject)
    TextView tvHowToInject;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isGoBackToHomeAfterInject = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_loadingp2p, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }

        tvHowToInject.setClickable(true);
        tvHowToInject.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    @Override
    protected void updateP2PUI(boolean isP2POn) {
        if (isP2POn) {
            super.updateUIAfterOuinetStart();

            p2pStatusImage.setImageResource(R.drawable.p2p_on_loading_screen_image);
            title.setText(R.string.p2p_on_loading_screen_title);
            description.setText(R.string.p2p_on_loading_screen_description);

            groupInjectFolder.setVisibility(View.VISIBLE);
        } else {
            super.updateUIAfterOuinetStop();
            p2pStatusImage.setImageResource(R.drawable.p2p_off_loading_screen_image);
            title.setText(R.string.p2p_off_loading_screen_title);
            description.setText(R.string.p2p_off_loading_screen_description);

            groupInjectFolder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this.getActivity());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void paskoochehConfigTimeout(Event.Timeout timeout) {
//        Intent intent = new Intent(getContext(), LoadingP2PActivity.class);
//        getActivity().startActivity(intent);
        progressSpinner.setVisibility(View.GONE);
        groupP2P.setVisibility(View.VISIBLE);

        updateP2PUI(PaskoochehApplication.getInstance().isServiceRunning(OuinetService.class));
    }

    @OnClick({R.id.refresh_button,R.id.refresh_text})
    void refreshP2PLoad() {
        progressSpinner.setVisibility(View.VISIBLE);
        groupP2P.setVisibility(View.GONE);
        groupInjectFolder.setVisibility(View.GONE);

        Intent configIntent = new Intent(getContext(), PaskoochehConfigService.class);
        configIntent.putExtra(CONFIG, APPS);
        getActivity().startService(configIntent);
    }
}
