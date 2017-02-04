package com.example.dscs.preview;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dscs.utility.IconFont;
import com.example.dscs.R;
import com.example.dscs.Task;

import java.util.ArrayList;

/**
 * Adapter for displaying task status and film names.
 */
class PreviewTaskAdapter extends ArrayAdapter<PreviewTaskAdapter.TaskPreview> {

    PreviewTaskAdapter(Context context, ArrayList<TaskPreview> items) {
        super(context, 0, items);
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder hld;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_preview_list_item_task, parent, false);
            hld = new ViewHolder(convertView);
            convertView.setTag(hld);
        } else {
            hld = (ViewHolder) convertView.getTag();
        }

        hld.mFilmIdView.setText(String.valueOf(getItem(position).key));
        hld.mTitleView.setText(getItem(position).title);
        hld.mIfStatus.setText(hld.mFilmIdView.getContext().getText(R.string.if_task_status_done));
        setupStatusIcon(hld.mIfStatus, getItem(position).status);
        setupClickListener(convertView);

        return convertView;
    }

    private void setupClickListener(View itemView) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ViewHolder hld = (ViewHolder) v.getTag();
                final String movieName = String.valueOf(hld.mTitleView.getText());

                if (!TextUtils.isEmpty(movieName)) {
                    Intent movieInfoIntent = new Intent(getContext(), MovieInfoActivity.class);
                    movieInfoIntent.putExtra(MovieInfoActivity.EXTRA_MOVIE_KEY,
                            Integer.parseInt(String.valueOf(hld.mFilmIdView.getText())));
                    movieInfoIntent.putExtra(MovieInfoActivity.EXTRA_MOVIE_TITLE, movieName);
                    getContext().startActivity(movieInfoIntent);
                }
            }
        });
    }

    private void setupStatusIcon(IconFont mIfStatus, int status) {
        int stringId = 0;
        int colorId = 0;
        switch (status) {
            case Task.INVALID:
                stringId = R.string.if_task_status_invalid;
                colorId = android.R.color.holo_red_light;
                break;
            case Task.SUBMITTED:
            case Task.INSERTED:
                stringId = R.string.if_task_status_inserted;
                colorId = android.R.color.darker_gray;
                break;
            case Task.IN_PROGRESS:
                stringId = R.string.if_task_status_in_progress;
                colorId = android.R.color.holo_blue_light;
                RotateAnimation animation = new RotateAnimation(0, 360);
                animation.setDuration(0);
                mIfStatus.setAnimation(animation);
                break;
            case Task.DONE:
                stringId = R.string.if_task_status_done;
                colorId = android.R.color.holo_green_dark;
                break;
        }

        mIfStatus.setText(mIfStatus.getContext().getString(stringId));
        mIfStatus.setTextColor(ContextCompat.getColor(getContext(), colorId));
    }

    private static class ViewHolder {
        final TextView mFilmIdView;
        final TextView mTitleView;
        final IconFont mIfStatus;

        ViewHolder(View view) {
            mFilmIdView = (TextView) view.findViewById(R.id.preview_item_key);
            mTitleView = (TextView) view.findViewById(R.id.preview_item_title);
            mIfStatus = (IconFont) view.findViewById(R.id.preview_item_status);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }

    static class TaskPreview {
        int key;
        String title;
        int status;
    }
}
