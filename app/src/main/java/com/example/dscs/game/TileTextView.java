package com.example.dscs.game;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Extended text view to hold other part of the title (first one is in the text).
 */
public class TileTextView extends TextView {

    private static final long MISSED_STATE_DURATION = 400;

    private String mPairTitle;
    private int mColumn;

    public TileTextView(Context context) {
        super(context);
    }

    public TileTextView(Context context, int column, int height, String title, String pairTitle) {
        super(context);
        mColumn = column;

        setHeight(height);
        setGravity(Gravity.CENTER);

        setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setTextSize(height / 4);
        setText(title);

        mPairTitle = pairTitle;
    }

    public void complete() {
        setTextColor(Color.DKGRAY);
        setBackgroundColor(GameActivity.COLOR_COMPLETED);
        setEnabled(false);
    }

    /**
     * Marks the tile as selected.
     */
    public void select() {
        setBackgroundColor(Color.GREEN);
        setTextColor(Color.WHITE);
    }

    /**
     * Returns the tile to a starting state.
     */
    public void deselect() {
        setBackgroundColor(Color.TRANSPARENT);
        setTextColor(Color.GRAY);
    }

    /**
     * Holds a tile in a missed state for a {@link #MISSED_STATE_DURATION} and then deselects it.
     */
    public void warnAndDeselect() {
        setBackgroundColor(GameActivity.COLOR_MISS);
        setTextColor(Color.WHITE);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                deselect();
            }
        }, MISSED_STATE_DURATION);
    }

    /**
     * Checks with other tile if they are pairs.
     *
     * @param view Instance of the other {@link TileTextView}.
     * @return Whether they can be paired.
     */
    public boolean isPair(TileTextView view) {
        return TextUtils.equals(mPairTitle, view.getText()) ||
                TextUtils.equals(getText(), view.mPairTitle);
    }

    /**
     * Checks with other tile if they are in the same column.
     *
     * @param view Instance of the other {@link TileTextView}.
     * @return Whether they belong to the same column.
     */
    public boolean isInTheSameColumn(TileTextView view) {
        return mColumn == view.mColumn;
    }
}
