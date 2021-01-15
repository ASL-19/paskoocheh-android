package org.asl19.paskoocheh.toolinfo;


import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Faq;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.ArrayList;

public class ToolInfoFaqAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Faq> faqs;

    public ToolInfoFaqAdapter(Context context, ArrayList<Faq> faqs) {
        this.context = context;
        this.faqs = faqs;
    }

    @Override
    public int getGroupCount() {
        return faqs.size();
    }

    @Override
    public int getChildrenCount(int groupId) {
        return 1;
    }

    @Override
    public Object getGroup(int groupId) {
        return faqs.get(groupId);
    }

    @Override
    public Object getChild(int groupId, int childId) {
        return faqs.get(groupId);
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
        Faq faq = (Faq) getGroup(groupId);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_group, null);
        }

        ImageView imageHeader = (ImageView) view.findViewById(R.id.arrow);
        if (isExpanded) {
            imageHeader.setImageResource(R.drawable.ic_keyboard_arrow_down);
        } else {
            imageHeader.setImageResource(R.drawable.ic_keyboard_arrow_left);
        }

        TextView lblListHeader = (TextView) view
                .findViewById(R.id.list_header);

        String startText = "";
        if (faq.language.equals("fa")) {
            startText = "\u200f";
        }
        lblListHeader.setText(startText + faq.getQuestion());

        return view;
    }

    @Override
    public View getChildView(int groupId, int childId, boolean b, View view, ViewGroup viewGroup) {
        final Faq faq = (Faq) getChild(groupId, childId);

        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) view.findViewById(R.id.list_item);

        String startText = "";
        if (faq.language.equals("fa")) {
            startText = "\u200f";
        }
        txtListChild.setText(startText + Html.fromHtml(HtmlRenderer.builder().build().render(Parser.builder().build().parse(faq.getAnswer()))));
        txtListChild.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
