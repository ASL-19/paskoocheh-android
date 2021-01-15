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

        Intent configIntent = new Intent(getContext(), PaskoochehConfigService.class);
        configIntent.putExtra(CONFIG, APPS);
        getActivity().startService(configIntent);
    }
}
