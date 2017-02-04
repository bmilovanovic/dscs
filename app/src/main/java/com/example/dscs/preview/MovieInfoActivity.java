package com.example.dscs.preview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dscs.Network;
import com.example.dscs.R;
import com.example.movie.MovieHelper;
import com.example.movie.tables.Country;
import com.example.movie.tables.Film;
import com.example.movie.tables.FilmCountry;
import com.example.movie.tables.FilmGenre;
import com.example.movie.tables.FilmPersonRole;
import com.example.movie.tables.Genre;
import com.example.movie.tables.Person;
import com.example.movie.tables.Role;
import com.microsoft.windowsazure.mobileservices.table.query.Query;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

/**
 * Activity for displaying all data about particular movie.
 */
public class MovieInfoActivity extends Activity {

    public static final String EXTRA_MOVIE_KEY = "movie_key";
    public static final String EXTRA_MOVIE_TITLE = "movie_name";

    private static final String TAG = MovieInfoActivity.class.getSimpleName();
    private Query mFilmQuery;

    private int mMovieKey;

    private RecyclerView mRecycler;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_info);

        mMovieKey = getIntent().getIntExtra(EXTRA_MOVIE_KEY, 0);
        TextView keyTextView = (TextView) findViewById(R.id.activity_movie_info_key_text_view);
        keyTextView.setText(String.valueOf(mMovieKey));

        String title = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);
        TextView titleTextView = (TextView) findViewById(R.id.activity_movie_info_title_text_view);
        titleTextView.setText(title);
        mRecycler = (RecyclerView) findViewById(R.id.activity_movie_info_recycler);
        mRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mProgressBar = (ProgressBar) findViewById(R.id.activity_movie_info_progress_bar);

        showInfo();
    }

    private void showInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final MovieAdapter adapter = new MovieAdapter(MovieInfoActivity.this, getAllItems());
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

    private List<MovieAdapter.MovieItem> getAllItems() {
        List<MovieAdapter.MovieItem> list = new ArrayList<>();

        try {
            mFilmQuery = QueryOperations.field("filmId").eq(val(mMovieKey));

            addCountries(list);
            addGenres(list);
            addMovieCategories(list);
            addPersons(list);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error in connection with the Azure", e);
            // TODO: Display error state
        }

        return list;
    }

    private void addGenres(List<MovieAdapter.MovieItem> list) throws ExecutionException, InterruptedException {
        for (FilmGenre filmGenre : Network.getTable(this, FilmGenre.class)
                .where(mFilmQuery).execute().get()) {
            Genre genre = MovieHelper.getGenre(Network.getClient(this), filmGenre.getGenreId());
            if (genre == null) {
                continue;
            }
            list.add(new MovieAdapter.MovieItem("Žanr", genre.getGenreName()));
        }
    }

    private void addCountries(List<MovieAdapter.MovieItem> list) throws ExecutionException, InterruptedException {
        for (FilmCountry filmCountry : Network.getTable(this, FilmCountry.class)
                .where(mFilmQuery).execute().get()) {
            Country country = MovieHelper.getCountry(Network.getClient(this), filmCountry.getCountryId());
            if (country == null) {
                continue;
            }
            list.add(new MovieAdapter.MovieItem("Država", country.getCountryName()));
        }
    }

    private void addMovieCategories(List<MovieAdapter.MovieItem> list) throws ExecutionException, InterruptedException {
        Film film = Network.getTable(this, Film.class).where(mFilmQuery).execute().get().get(0);

        addIfNotEmpty(list, Film.CATEGORY_YEAR, film.getPremiere());
        addIfNotEmpty(list, Film.CATEGORY_SYNOPSIS, film.getSynopsis());
        addIfNotEmpty(list, Film.CATEGORY_DURATION, film.getDuration());
        addIfNotEmpty(list, Film.CATEGORY_TECHNIQUE, film.getTechnique());
    }

    private void addIfNotEmpty(List<MovieAdapter.MovieItem> list, String name, String value) {
        if (!TextUtils.isEmpty(value)) {
            list.add(new MovieAdapter.MovieItem(name, value));
        }
    }

    private void addPersons(List<MovieAdapter.MovieItem> list) throws ExecutionException, InterruptedException {
        String lastRoleName = "";
        for (FilmPersonRole personRole : Network.getTable(this, FilmPersonRole.class)
                .where(mFilmQuery).orderBy("roleId", QueryOrder.Ascending).execute().get()) {
            // Find a role name via its ID
            Role role = MovieHelper.getRole(Network.getClient(this), personRole.getRoleId());
            if (role == null) {
                continue;
            }
            String roleName = role.getRoleName();
            if (TextUtils.equals(lastRoleName, role.getRoleName())) {
                roleName = "";
            }
            if (!TextUtils.isEmpty(roleName)) {
                lastRoleName = roleName;
            }

            // Find a person name via its ID
            Query personQuery = QueryOperations.field("personId").eq(val(personRole.getPersonId()));
            Person person = Network.getTable(this, Person.class)
                    .where(personQuery).execute().get().get(0);
            if (person == null) {
                continue;
            }
            String personName = person.getName();

            list.add(new MovieAdapter.MovieItem(roleName, personName));
        }
    }
}
