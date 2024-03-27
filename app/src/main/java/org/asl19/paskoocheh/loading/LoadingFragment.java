package org.asl19.paskoocheh.loading;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.event.Event;
import org.asl19.paskoocheh.service.PaskoochehConfigService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.asl19.paskoocheh.Constants.APPS;
import static org.asl19.paskoocheh.service.PaskoochehConfigService.CONFIG;

public class LoadingFragment extends Fragment {

    public static final String TAG = LoadingFragment.class.getCanonicalName();

    public static LoadingFragment newInstance() {
        return new LoadingFragment();
    }

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.retry)
    TextView retry;

    @BindView(R.id.banner_message)
    TextView banner_message;

    @BindView((R.id.banner_sub_message))
    TextView banner_sub_message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //startActivity(new Intent(getActivity(), P2PActivity.class));
                if(getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.d("UI thread", "I am the UI thread");
                        banner_message.setVisibility(View.VISIBLE);
                        banner_sub_message.setVisibility(View.VISIBLE);
                    }
                });

            }
        }, 3000,3000);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void paskoochehConfigTimeout(Event.Timeout timeout) {
        banner_message.setVisibility(View.GONE);
        banner_sub_message.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        retry.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.retry)
    public void retryConnection() {
        retry.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Intent configIntent = new Intent(getContext(), PaskoochehConfigService.class);
        configIntent.putExtra(CONFIG, APPS);
        getActivity().startService(configIntent);
    }
}
