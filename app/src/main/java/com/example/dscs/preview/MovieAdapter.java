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
 * Adapter for showing all movie info.
 */
class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieInfoViewHolder> {

    private final Context mContext;

    private List<MovieItem> mItems = new ArrayList<>();

    MovieAdapter(Context context, List<MovieItem> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public MovieInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_info_item, null);
        return new MovieInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieInfoViewHolder holder, int position) {
        holder.categoryTextView.setText(mItems.get(position).category);
        holder.valueTextView.setText(mItems.get(position).value);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class MovieItem {
        String category;
        String value;

        MovieItem(String category, String value) {
            this.category = category;
            if (!TextUtils.isEmpty(category)) {
                this.category = category.substring(0, 1).toUpperCase() + category.substring(1);
            }
            this.value = value;
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
    }
}
