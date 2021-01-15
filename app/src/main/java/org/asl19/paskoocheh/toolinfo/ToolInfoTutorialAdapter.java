package org.asl19.paskoocheh.toolinfo;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Tutorial;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

public class ToolInfoTutorialAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Tutorial> tutorials;

    public ToolInfoTutorialAdapter(Context context, ArrayList<Tutorial> tutorials) {
        this.context = context;
        this.tutorials = tutorials;
    }

    @Override
    public int getGroupCount() {
        return tutorials.size();
    }

    @Override
    public int getChildrenCount(int groupId) {
        return 1;
    }

    @Override
    public Object getGroup(int groupId) {
        return tutorials.get(groupId);
    }

    @Override
    public Object getChild(int groupId, int childId) {
        return tutorials.get(groupId);
    }

    @Override
    public long getGroupId(int groupId) {
        return groupId;
    }

    @Override
    public long getChildId(int groupId, int childId) {
        return childId;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupId, boolean isExpanded, View view, ViewGroup viewGroup) {
        Tutorial tutorial = (Tutorial) getGroup(groupId);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_group, null);
        }

        ImageView imageHeader = view.findViewById(R.id.arrow);
        if (isExpanded) {
            imageHeader.setImageResource(R.drawable.ic_keyboard_arrow_down);
        } else {
            imageHeader.setImageResource(R.drawable.ic_keyboard_arrow_left);
        }

        TextView lblListHeader = view
                .findViewById(R.id.list_header);

        String startText = "";
        if (tutorial.language.equals("fa")) {
            startText = "\u200f";
        }
        lblListHeader.setText(startText + Html.fromHtml(tutorial.getTitle()));

        return view;
    }

    @Override
    public View getChildView(int groupId, int childId, final boolean b, View view, ViewGroup viewGroup) {
        final Tutorial tutorial = (Tutorial) getChild(groupId, childId);

        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_item_tutorial, null);
        }

        final RelativeLayout videoLayout = view.findViewById(R.id.video_layout);
        final ProgressBar progressBar = view.findViewById(R.id.progress);
        final Button button = view.findViewById(R.id.play_video);

        final Uri video = Uri.parse(tutorial.getVideoLink());

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, video);
                context.startActivity(intent);
            }
        });

        try {

            final VideoView videoView;
            videoView = view.findViewById(R.id.list_item_video);
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(videoView);

            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.start();


            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(GONE);
                    videoView.start();
                }
            });

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    videoLayout.setVisibility(GONE);
                    button.setVisibility(VISIBLE);
                    return true;
                }
            });
        } catch (Exception exception) {
            Log.e(getClass().getSimpleName(), exception.getMessage());
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
