package org.asl19.paskoocheh.toolinfo;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ToolInfoReviewAdapter extends ArrayAdapter<Review> {

    @BindView(R.id.review_title)
    TextView reviewTitle;
    @BindView(R.id.star)
    ImageView star;
    @BindView(R.id.rating)
    TextView rating;
    @BindView(R.id.review)
    TextView review;
    @BindView(R.id.date)
    TextView date;

    private List<Review> reviewList;

    public ToolInfoReviewAdapter(Context context, List<Review> reviewList) {
        super(context, 0, reviewList);
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_tool_info_review, parent, false);
        }
        ButterKnife.bind(this, convertView);

        Review reviewItem = reviewList.get(position);

        reviewTitle.setText(reviewItem.getTitle());
        rating.setText(reviewItem.getRating().toString());
        review.setText(reviewItem.getText());
        date.setText(reviewItem.getDateOther().getJshortdate());

        convertView.measure(View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        return convertView;
    }
}
