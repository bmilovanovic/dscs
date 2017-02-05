package com.example.dscs.preview;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.dscs.Network;
import com.example.movie.MovieHelper;
import com.example.movie.tables.Country;
import com.example.movie.tables.Film;
import com.example.movie.tables.FilmCountry;
import com.example.movie.tables.FilmGenre;
import com.example.movie.tables.FilmPersonRole;
import com.example.movie.tables.Genre;
import com.example.movie.tables.Person;
import com.example.movie.tables.Role;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.query.Query;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

/**
 * Helper class for getting all data from azure to display in MovieInfo
 */
class MovieInfoGatherer {

    private final MobileServiceClient mClient;
    private Query mFilmQuery;

    MovieInfoGatherer(Context context) {
        mClient = Network.getClient(context);
    }

    List<MovieAdapter.MovieItem> getAllMovieAttributes(int movieKey) {
        mFilmQuery = QueryOperations.field("filmId").eq(val(movieKey));
        List<MovieAdapter.MovieItem> list = new ArrayList<>();

        try {
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

    private void addCountries(List<MovieAdapter.MovieItem> list) throws ExecutionException, InterruptedException {
        boolean isFirst = true;
        for (FilmCountry filmCountry : mClient.getTable(FilmCountry.class)
                .where(mFilmQuery).execute().get()) {
            Country country = MovieHelper.getCountry(mClient, filmCountry.getCountryId());
            if (country == null) {
                continue;
            }
            String attributeName = "";
            if (isFirst) {
                attributeName = "Država";
            }
            isFirst = false;

            MovieInfoActivity.MovieQuery query = new MovieInfoActivity.MovieQuery(
                    country.getCountryName(), FilmCountry.class, country.getCountryId(), "countryId");
            list.add(new MovieAdapter.MovieItem(attributeName, country.getCountryName(), query));
        }
    }

    private void addGenres(List<MovieAdapter.MovieItem> list) throws ExecutionException, InterruptedException {
        boolean isFirst = true;
        for (FilmGenre filmGenre : mClient.getTable(FilmGenre.class)
                .where(mFilmQuery).execute().get()) {
            Genre genre = MovieHelper.getGenre(mClient, filmGenre.getGenreId());
            if (genre == null) {
                continue;
            }
            String attributeName = "";
            if (isFirst) {
                attributeName = "Žanr";
            }
            isFirst = false;

            MovieInfoActivity.MovieQuery query = new MovieInfoActivity.MovieQuery(
                    genre.getGenreName(), FilmGenre.class, genre.getGenreId(), "genreId");
            list.add(new MovieAdapter.MovieItem(attributeName, genre.getGenreName(), query));
        }
    }

    private void addMovieCategories(List<MovieAdapter.MovieItem> list) throws ExecutionException, InterruptedException {
        Film film = mClient.getTable(Film.class).where(mFilmQuery).execute().get().get(0);

        MovieInfoActivity.MovieQuery query = new MovieInfoActivity.MovieQuery(film.getPremiere(),
                Film.class, Integer.parseInt(film.getPremiere()), "premiere");
        addIfNotEmpty(list, Film.CATEGORY_YEAR, film.getPremiere(), query);
        addIfNotEmpty(list, Film.CATEGORY_SYNOPSIS, film.getSynopsis(), null);
        addIfNotEmpty(list, Film.CATEGORY_DURATION, film.getDuration(), null);
        addIfNotEmpty(list, Film.CATEGORY_TECHNIQUE, film.getTechnique(), null);
    }

    private void addIfNotEmpty(List<MovieAdapter.MovieItem> list, String name, String value,
                               MovieInfoActivity.MovieQuery query) {
        if (!TextUtils.isEmpty(value)) {
            list.add(new MovieAdapter.MovieItem(name, value, query));
        }
    }

    private void addPersons(List<MovieAdapter.MovieItem> list) throws ExecutionException, InterruptedException {
        String lastRoleName = "";
        for (FilmPersonRole personRole : mClient.getTable(FilmPersonRole.class)
                .where(mFilmQuery).orderBy("roleId", QueryOrder.Ascending).execute().get()) {
            // Find a role name via its ID
            Role role = MovieHelper.getRole(mClient, personRole.getRoleId());
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
            Person person = mClient.getTable(Person.class)
                    .where(personQuery).execute().get().get(0);
            if (person == null) {
                continue;
            }
            String personName = person.getName();

            MovieInfoActivity.MovieQuery query = new MovieInfoActivity.MovieQuery(personName,
                    FilmPersonRole.class, person.getPersonId(), "personId");
            list.add(new MovieAdapter.MovieItem(roleName, personName, query));
        }
    }

    List<MovieAdapter.MovieItem> getAllMovies(MovieInfoActivity.MovieQuery query) {
        List<MovieAdapter.MovieItem> items = new ArrayList<>();

        Query tableQuery;
        if (TextUtils.equals(query.field, "premiere")) {
            tableQuery = QueryOperations.field(query.field).eq(val(String.valueOf(query.id)));
        } else {
            tableQuery = QueryOperations.field(query.field).eq(val(query.id));
        }
        try {
            MobileServiceList list = (MobileServiceList)
                    mClient.getTable(query.clazz).where(tableQuery).execute().get();
            for (Object o : list) {
                Field field = o.getClass().getDeclaredField("mFilmId");
                field.setAccessible(true);
                Film film = getFilmById(field.getInt(o));
                MovieInfoActivity.MovieQuery movieQuery = new MovieInfoActivity
                        .MovieQuery(film.getTitle(), Film.class, film.getFilmId(), "title");
                items.add(new MovieAdapter.MovieItem(film.getPremiere(), film.getTitle(), movieQuery));
            }
        } catch (InterruptedException | ExecutionException | NoSuchFieldException
                | IllegalAccessException e) {
            e.printStackTrace();
        }

        return items;
    }

    private Film getFilmById(int movieId) throws ExecutionException, InterruptedException {
        Query query = QueryOperations.field("filmId").eq(val(movieId));
        return mClient.getTable(Film.class).where(query).execute().get().get(0);
    }
}
