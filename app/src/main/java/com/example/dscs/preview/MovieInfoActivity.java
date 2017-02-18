package com.example.dscs.preview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dscs.R;
import com.example.movie.tables.Film;

import java.util.List;

/**
 * Activity for displaying all data about particular movie.
 */
public class MovieInfoActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_MOVIE_KEY = "movie_key";
    public static final String EXTRA_MOVIE_TITLE = "movie_name";

    private TextView mKeyTextView;
    private TextView mTitleTextView;
    private RecyclerView mRecycler;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_info);

        mKeyTextView = (TextView) findViewById(R.id.activity_movie_info_key_text_view);
        mTitleTextView = (TextView) findViewById(R.id.activity_movie_info_title_text_view);

        mRecycler = (RecyclerView) findViewById(R.id.activity_movie_info_recycler);
        mRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mProgressBar = (ProgressBar) findViewById(R.id.activity_movie_info_progress_bar);

        final int movieKey = getIntent().getIntExtra(EXTRA_MOVIE_KEY, 0);
        final String movieTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);
        final MovieQuery query = new MovieQuery(movieTitle, Film.class, movieKey, "title");
        showInfo(query);
    }

    /**
     * Shows movie info.
     *
     * @param query Query to gather information upon.
     */
    private void showInfo(final MovieQuery query) {
        mKeyTextView.setText(String.valueOf(query.id));
        mTitleTextView.setText(query.title);

        mRecycler.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MovieInfoAdapter.MovieItem> list;
                if (query.clazz.equals(Film.class) && TextUtils.equals(query.field, "title")) {
                    // Display one movie with all attributes
                    list = new MovieInfoDownloader(MovieInfoActivity.this).getAllMovieAttributes(query.id);
                } else {
                    // Display list of movies for certain attribute
                    list = new MovieInfoDownloader(MovieInfoActivity.this).getAllMovies(query);
                }
                final MovieInfoAdapter adapter = new MovieInfoAdapter(MovieInfoActivity.this, list);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);
                        mRecycler.setAdapter(adapter);
                        mRecycler.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    static class MovieQuery {
        String title;
        Class clazz;
        int id;
        String field;

        MovieQuery(String title, Class clazz, int id, String field) {
            this.title = title;
            this.clazz = clazz;
            this.id = id;
            this.field = field;
        }
    }

    @Override
    public void onClick(View v) {
        MovieQuery query = (MovieQuery) v.getTag();
        if (query != null) {
            showInfo(query);
        }
    }
}
