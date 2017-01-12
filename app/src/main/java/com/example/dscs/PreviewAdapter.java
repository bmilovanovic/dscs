package com.example.dscs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Simple adapter for listing items from Azure tables (domain items).
 */
class PreviewAdapter<E> extends ArrayAdapter<E> {

    PreviewAdapter(Context context, ArrayList<E> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = (TextView) LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_preview_list_item, parent, false);
        } else {
            textView = (TextView) convertView;
        }
        //noinspection ConstantConditions
        textView.setText(getItem(position).toString());
        return textView;
    }

}
