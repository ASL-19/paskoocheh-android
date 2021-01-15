package org.asl19.paskoocheh.toollist;


import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.categorylist.CategoryListActivity;
import org.asl19.paskoocheh.categorylist.CategoryListFragment;
import org.asl19.paskoocheh.pojo.Name;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.asl19.paskoocheh.categorylist.CategoryListActivity.CATEGORY;
import static org.asl19.paskoocheh.categorylist.CategoryListActivity.TYPE;

public class ToolListCategoryAdapter extends RecyclerView.Adapter<ToolListCategoryAdapter.ViewHolder> {

    private List<Name> category;
    private Integer cardId;
    private Context context;

    public ToolListCategoryAdapter(List<Name> categoryNames, Context context, int cardId) {
        this.category = categoryNames;
        this.cardId = cardId;
        this.context = context;
    }

    @Override
    public ToolListCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(cardId, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Name name = category.get(position);

        Picasso.with(context).load(name.getIcon()).placeholder(R.color.blue).into(holder.imageView);

        if (name.getFa().isEmpty()) {
            holder.name.setText(name.getEn());
        } else {
            holder.name.setText(name.getFa());
        }
    }

    @Override
    public int getItemCount() {
        return (null != category ? category.size() : 0);
    }

    /**
     * Custom RecyclerView.ViewHolder implementation.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.img)
        ImageView imageView;
        @BindView(R.id.title)
        TextView name;


        /**
         * ViewHolder for View with Android Tools
         *
         * @param view The view.
         */
        public ViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, CategoryListActivity.class);
            intent.putExtra(CategoryListFragment.CATEGORY, Parcels.wrap(category.get(getLayoutPosition())));
            intent.putExtra(TYPE, CATEGORY);
            context.startActivity(intent);
        }
    }
}
