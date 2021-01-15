package org.asl19.paskoocheh.toolinfo;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Guide;

import java.util.ArrayList;

import lombok.NonNull;

public class ToolInfoGuideAdapter extends ArrayAdapter<Guide> {

    private Context context;
    private ArrayList<Guide> guides;

    public ToolInfoGuideAdapter(Context context, ArrayList<Guide> guides) {
        super(context, 0, guides);
        this.context = context;
        this.guides = guides;
    }


    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        String headerTitle = getItem(position).getHeadline();
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = view
                .findViewById(R.id.list_header);
        lblListHeader.setText(Html.fromHtml(headerTitle));

        return view;
    }

    @Override
    public Guide getItem(int position) {
        return guides.get(position);
    }

    @Override
    public int getCount() {
        return (null != guides ? guides.size() : 0);
    }
}
