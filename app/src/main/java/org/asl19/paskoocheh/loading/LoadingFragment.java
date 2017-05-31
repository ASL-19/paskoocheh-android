package org.asl19.paskoocheh.loading;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoadingFragment extends Fragment {

    public static final String TAG = LoadingFragment.class.getCanonicalName();

    public static LoadingFragment newInstance() {
        return new LoadingFragment();
    }

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.retry)
    TextView retry;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void paskoochehConfigTimeout(Event.Timeout timeout) {
        progressBar.setVisibility(View.GONE);
        retry.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.retry)
    public void retryConnection() {
        retry.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        getActivity().startService(new Intent(getActivity(), PaskoochehConfigService.class));
    }
}
