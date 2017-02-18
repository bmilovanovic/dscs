package com.example.dscs.preview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dscs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for showing all movie information.
 */
class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoAdapter.MovieInfoViewHolder>
        implements View.OnClickListener {

    private final Context mContext;

    private List<MovieItem> mItems = new ArrayList<>();

    MovieInfoAdapter(Context context, List<MovieItem> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public MovieInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_info_item, null);
        view.setOnClickListener(this);
        return new MovieInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieInfoViewHolder holder, int position) {
        MovieItem item = mItems.get(position);

        holder.categoryTextView.setText(item.category);
        holder.valueTextView.setText(item.value);
        holder.setTag(item.query);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onClick(View v) {
        ((View.OnClickListener) mContext).onClick(v);
    }

    static class MovieItem {
        String category;
        String value;
        MovieInfoActivity.MovieQuery query;

        MovieItem(String category, String value, MovieInfoActivity.MovieQuery query) {
            this.category = category;
            if (!TextUtils.isEmpty(category)) {
                this.category = category.substring(0, 1).toUpperCase() + category.substring(1);
            }
            this.value = value;
            this.query = query;
        }
    }

    class MovieInfoViewHolder extends RecyclerView.ViewHolder {

        TextView categoryTextView;
        TextView valueTextView;

        MovieInfoViewHolder(View itemView) {
            super(itemView);
            categoryTextView = (TextView) itemView.findViewById(R.id.text_view_movie_info_category);
            valueTextView = (TextView) itemView.findViewById(R.id.text_view_movie_info_value);
        }

        void setTag(MovieInfoActivity.MovieQuery query) {
            ((View) categoryTextView.getParent()).setTag(query);
        }
    }
}
