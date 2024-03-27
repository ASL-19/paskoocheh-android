package org.asl19.paskoocheh.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.asl19.paskoocheh.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PaskoochehDialog extends DialogFragment {
    private boolean invisibleButton;
    private String buttonCaption;
    private String description;
    private String title;
    private int titleColor;
    private int imageResourceID;
    private boolean isProgressBarAlert;

    public PaskoochehDialog() {}

    public PaskoochehDialog(String title, int titleColor, String description, String buttonCaption) {
        this(title, titleColor, description, false, buttonCaption, -1, false);
        this.setRetainInstance(true);
    }

    public PaskoochehDialog(String title, int titleColor, String description, String buttonCaption, int imageResourceID) {
        this(title, titleColor, description, false, buttonCaption, imageResourceID, false);
        this.setRetainInstance(true);
    }

    public PaskoochehDialog(String title, int titleColor, String description, boolean isProgressBarAlert) {
        this(title, titleColor, description, true, null, -1, isProgressBarAlert);
        this.setRetainInstance(true);
    }

    private PaskoochehDialog(String title, int titleColor, String description, boolean invisibleButton, String buttonCaption, int imageResourceID, boolean isProgressBarAlert) {
        this.title = title;
        this.description = description;
        this.buttonCaption = buttonCaption;
        this.titleColor = titleColor;
        this.invisibleButton = invisibleButton;
        this.imageResourceID = imageResourceID;
        this.isProgressBarAlert = isProgressBarAlert;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            title = savedInstanceState.getString("title");
            description = savedInstanceState.getString("description");
            buttonCaption = savedInstanceState.getString("button_caption");
            titleColor = savedInstanceState.getInt("title_color");
            invisibleButton = savedInstanceState.getBoolean("is_invisible_button");
            imageResourceID = savedInstanceState.getInt("image_resource_id");
            isProgressBarAlert = savedInstanceState.getBoolean("is_progress_bar_alert");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("title", title);
        savedInstanceState.putString("description", description);
        savedInstanceState.putString("button_caption", buttonCaption);
        savedInstanceState.putInt("title_color", titleColor);
        savedInstanceState.putBoolean("is_invisible_button", invisibleButton);
        savedInstanceState.putInt("image_resource_id", imageResourceID);
        savedInstanceState.putBoolean("is_progress_bar_alert", isProgressBarAlert);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.paskoocheh_one_button_progress_spinner_alert_layout, container, false);

        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(title);
        titleView.setTextColor(titleColor); // default is black.

        if (description != null) {
            ((TextView)view.findViewById(R.id.description)).setText(description);
        } else {
            view.findViewById(R.id.description).setVisibility(View.GONE);
        }

        Button okButton = view.findViewById(R.id.ok_button);
        if (invisibleButton) {
            okButton.setVisibility(View.INVISIBLE);
        } else {
            okButton.setVisibility(View.VISIBLE);
            okButton.setOnClickListener(v -> getDialog().dismiss());
            okButton.setText(buttonCaption);
        }

        ImageView imageView = view.findViewById(R.id.image);
        if (isProgressBarAlert) {
            imageView.setVisibility(View.INVISIBLE);
        } else {
            ProgressBar progressBar = view.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.INVISIBLE);
            if (imageResourceID > -1) {
                imageView.setBackground(getContext().getDrawable(imageResourceID));
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }
        }

        if (imageView.getVisibility() == View.GONE && !isProgressBarAlert) {
            titleView.setPadding(16, 120, 16, 16);
        }

        this.setCancelable(false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
        getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}