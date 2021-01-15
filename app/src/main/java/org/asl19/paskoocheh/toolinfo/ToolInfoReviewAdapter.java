package org.asl19.paskoocheh.toolinfo;


import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.pojo.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

public class ToolInfoReviewAdapter extends RecyclerView.Adapter<ToolInfoReviewAdapter.ViewHolder> {

    private List<Review> reviewList;

    public ToolInfoReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @Override
    public ToolInfoReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.row_tool_info_review, parent, false);

        return new ToolInfoReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ToolInfoReviewAdapter.ViewHolder holder, int position) {
        Review reviewItem = reviewList.get(position);

        holder.reviewTitle.setText(reviewItem.getSubject());
        holder.rating.setText(String.valueOf(reviewItem.getRating()));
        holder.review.setText(reviewItem.getText());
//        holder.date.setText(reviewItem.getDateOther().getJshortdate());

        holder.review.post(new Runnable() {
            @Override
            public void run() {
                if (holder.review != null) {
                    if (holder.review.getLineCount() < 3) {
                        holder.readMore.setVisibility(GONE);
                    } else {
                        holder.review.setMaxLines(2);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != reviewList ? reviewList.size() : 0);
    }

    /**
     * Custom RecyclerView.ViewHolder implementation.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.review_title)
        TextView reviewTitle;
        @BindView(R.id.rating)
        TextView rating;
        @BindView(R.id.review)
        TextView review;
        @BindView(R.id.read_more)
        TextView readMore;
        @BindView(R.id.date)
        TextView date;

        /**
         * ViewHolder for View with Android Tools
         *
         * @param view The view.
         */
        public ViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.read_more)
        void readMore() {
            review.setMaxLines(review.getLineCount());
            readMore.setVisibility(GONE);
        }
    }
}
