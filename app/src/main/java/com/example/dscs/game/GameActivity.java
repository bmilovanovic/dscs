package com.example.dscs.game;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dscs.R;

import java.util.Collections;
import java.util.List;

/**
 * Activity for displaying the connection game and handling user interaction.
 */
public class GameActivity extends AppCompatActivity implements View.OnClickListener, GameViewInterface {

    private static final int COLUMN_LEFT = 1;
    private static final int COLUMN_RIGHT = 2;

    private static final long SCORE_CHANGE_DURATION = 1500;

    public static final int COLOR_HIT = Color.GREEN;
    public static final int COLOR_MISS = Color.parseColor("#ffff4444");
    public static final int COLOR_COMPLETED = Color.parseColor("#ff669900");

    private LinearLayout mLeftContainer;
    private LinearLayout mRightContainer;
    private TileTextView mSelectedView = null;

    private TextView mScoreTextView;
    private TextView mScoreChangeTextView;
    private TextView mTimerTextView;
    private TextView mDialogTitleTextView;

    private Handler mScoreChangeHandler = new Handler(Looper.getMainLooper());
    private AlertDialog mDialog;

    private GameEngineInterface mEngine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        mLeftContainer = (LinearLayout) findViewById(R.id.activity_game_container_left);
        mRightContainer = (LinearLayout) findViewById(R.id.activity_game_container_right);

        mScoreTextView = (TextView) findViewById(R.id.activity_game_score);
        mScoreChangeTextView = (TextView) findViewById(R.id.activity_game_score_change);
        mTimerTextView = (TextView) findViewById(R.id.activity_game_timer);

        initDialog();

        mEngine = new GameEngine(this);
    }

    /**
     * Initializes the dialog that is displayed between the levels.
     */
    private void initDialog() {
        mDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setPadding(20, 20, 20, 20);
        mDialog.setView(progressBar);

        mDialogTitleTextView = new TextView(this);
        mDialogTitleTextView.setPadding(20, 20, 20, 20);
        mDialogTitleTextView.setGravity(Gravity.CENTER);
        mDialogTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        mDialogTitleTextView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        mDialog.setCustomTitle(mDialogTitleTextView);
    }

    @Override
    public void showLoadingNextLevel(int mCurrentLevel) {
        mDialogTitleTextView.setText(getString(R.string.activity_game_starting_level, mCurrentLevel));
        mDialog.show();
    }

    @Override
    public void displayGame(List<Pair> pairs) {
        mDialog.dismiss();
        int height = mLeftContainer.getHeight() / pairs.size();

        mLeftContainer.removeAllViewsInLayout();
        for (Pair pair : pairs) {
            mLeftContainer.addView(getTile(COLUMN_LEFT, pair.first, pair.second, height));
        }

        Collections.shuffle(pairs);
        mRightContainer.removeAllViewsInLayout();
        for (Pair pair : pairs) {
            mRightContainer.addView(getTile(COLUMN_RIGHT, pair.second, pair.first, height));
        }
    }

    /**
     * Forms a new tile from a pair.
     *
     * @param column    Column, either {@link #COLUMN_LEFT} or {@link #COLUMN_RIGHT}.
     * @param title     Title to display on a tile.
     * @param pairTitle Title of the other tile that is to be paired with this tile.
     * @param height    Height of a tile.
     * @return A new {@link TileTextView} instance.
     */
    private View getTile(int column, String title, String pairTitle, int height) {
        final TileTextView view = new TileTextView(this, column, height, title, pairTitle);
        view.setOnClickListener(this);

        return view;
    }

    @Override
    public void showScore(int score) {
        final String text = getString(R.string.activity_game_score, score);
        mScoreTextView.setText(text);
    }

    @Override
    public void showScoreChange(int points) {
        int color;
        String text;
        if (points > 0) {
            color = COLOR_HIT;
            text = "+" + points;
        } else {
            color = COLOR_MISS;
            text = "" + points;
        }
        mScoreChangeTextView.setTextColor(color);
        mScoreChangeTextView.setText(text);

        mScoreChangeHandler.removeCallbacksAndMessages(null);
        mScoreChangeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScoreChangeTextView.setText("");
            }
        }, SCORE_CHANGE_DURATION);
    }

    @Override
    public void showTimeLeft(int timeLeft) {
        final String text = String.valueOf(timeLeft / 1000) + "." +
                String.valueOf(timeLeft % 1000).substring(0, 1);
        mTimerTextView.setText(text);
    }

    @Override
    public void gameOver(int score) {
        mDialog.dismiss();
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.activity_game_game_over))
                .setMessage(getString(R.string.activity_game_do_you_want_to_start_again, score))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEngine.restart();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TileTextView) {
            TileTextView view = (TileTextView) v;
            if (mSelectedView == null) {
                // There's none selected. Just select this.
                view.select();
                mSelectedView = view;
            } else if (mSelectedView.equals(view)) {
                // It's click on the same tile. Deselect it.
                view.deselect();
                mSelectedView = null;
            } else if (mSelectedView.isPair(view)) {
                // It's click on a correct pair. Complete them.
                view.complete();
                mSelectedView.complete();
                mSelectedView = null;
                mEngine.hit();
            } else if (mSelectedView.isInTheSameColumn(view)) {
                // Another tile from the same column is clicked. Change the selection.
                mSelectedView.deselect();
                view.select();
                mSelectedView = view;
            } else {
                // These are not pair, show wrong selection.
                mSelectedView.warnAndDeselect();
                view.warnAndDeselect();
                mSelectedView = null;
                mEngine.miss();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEngine.restart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEngine.stop();
    }
}
