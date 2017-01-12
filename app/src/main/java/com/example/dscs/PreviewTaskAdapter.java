package com.example.dscs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

        return convertView;
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
