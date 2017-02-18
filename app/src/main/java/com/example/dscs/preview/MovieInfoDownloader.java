package com.example.dscs.preview;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.dscs.Network;
import com.example.dscs.utility.UiUtils;
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
 * Helper class for getting all data from azure to display in MovieInfoActivity.
 */
class MovieInfoDownloader {

    private final MobileServiceClient mClient;
    private Query mFilmQuery;

    MovieInfoDownloader(Context context) {
        mClient = Network.getClient(context);
    }

    /**
     * Downloads all movie information from Azure.
     *
     * @param movieKey Unique identifier of the movie.
     * @return List of all the movie attributes.
     */
    List<MovieInfoAdapter.MovieItem> getAllMovieAttributes(int movieKey) {
        mFilmQuery = QueryOperations.field("filmId").eq(val(movieKey));
        List<MovieInfoAdapter.MovieItem> list = new ArrayList<>();

        try {
            addCountries(list);
            addGenres(list);
            addMovieCategories(list);
            addPersons(list);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error in connection with the Azure", e);
            UiUtils.showNoConnectionToast(mClient.getContext());
        }

        return list;
    }

    /**
     * Downloads movie countries.
     *
     * @param list List to add items to.
     * @throws ExecutionException   Error connecting to Azure.
     * @throws InterruptedException Somebody interrupted the network operation.
     */
    private void addCountries(List<MovieInfoAdapter.MovieItem> list) throws ExecutionException, InterruptedException {
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
            list.add(new MovieInfoAdapter.MovieItem(attributeName, country.getCountryName(), query));
        }
    }

    /**
     * Downloads movie genres.
     *
     * @param list List to add items to.
     * @throws ExecutionException   Error connecting to Azure.
     * @throws InterruptedException Somebody interrupted the network operation.
     */
    private void addGenres(List<MovieInfoAdapter.MovieItem> list) throws ExecutionException, InterruptedException {
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
            list.add(new MovieInfoAdapter.MovieItem(attributeName, genre.getGenreName(), query));
        }
    }

    /**
     * Downloads basic movie info from a Film table.
     *
     * @param list List to add items to.
     * @throws ExecutionException   Error connecting to Azure.
     * @throws InterruptedException Somebody interrupted the network operation.
     */
    private void addMovieCategories(List<MovieInfoAdapter.MovieItem> list) throws ExecutionException, InterruptedException {
        Film film = mClient.getTable(Film.class).where(mFilmQuery).execute().get().get(0);

        MovieInfoActivity.MovieQuery query = new MovieInfoActivity.MovieQuery(film.getPremiere(),
                Film.class, Integer.parseInt(film.getPremiere()), "premiere");
        addIfNotEmpty(list, Film.CATEGORY_YEAR, film.getPremiere(), query);
        addIfNotEmpty(list, Film.CATEGORY_SYNOPSIS, film.getSynopsis(), null);
        addIfNotEmpty(list, Film.CATEGORY_DURATION, film.getDuration(), null);
        addIfNotEmpty(list, Film.CATEGORY_TECHNIQUE, film.getTechnique(), null);
    }

    /**
     * Checks the value and if it's not empty, add it to the list.
     *
     * @param list  List to add item to.
     * @param name  Name of the item.
     * @param value Value of the item.
     * @param query MovieQuery.
     */
    private void addIfNotEmpty(List<MovieInfoAdapter.MovieItem> list, String name, String value,
                               MovieInfoActivity.MovieQuery query) {
        if (!TextUtils.isEmpty(value)) {
            list.add(new MovieInfoAdapter.MovieItem(name, value, query));
        }
    }

    /**
     * Downloads movie crew.
     *
     * @param list List to add items to.
     * @throws ExecutionException   Error connecting to Azure.
     * @throws InterruptedException Somebody interrupted the network operation.
     */
    private void addPersons(List<MovieInfoAdapter.MovieItem> list) throws ExecutionException, InterruptedException {
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
            list.add(new MovieInfoAdapter.MovieItem(roleName, personName, query));
        }
    }

    /**
     * Gets all movies for a certain attribute.
     *
     * @param query Source attribute is in here.
     * @return List of movies.
     */
    List<MovieInfoAdapter.MovieItem> getAllMovies(MovieInfoActivity.MovieQuery query) {
        List<MovieInfoAdapter.MovieItem> items = new ArrayList<>();

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
                items.add(new MovieInfoAdapter.MovieItem(film.getPremiere(), film.getTitle(), movieQuery));
            }
        } catch (InterruptedException | ExecutionException | NoSuchFieldException
                | IllegalAccessException e) {
            Log.e(TAG, "Error in connection with the Azure", e);
            UiUtils.showNoConnectionToast(mClient.getContext());
        }

        return items;
    }

    /**
     * Gets film with a given id.
     *
     * @param movieId Unique identifier of the movie.
     * @return Film.
     * @throws ExecutionException   Error connecting to Azure.
     * @throws InterruptedException Somebody interrupted the network operation.
     */
    private Film getFilmById(int movieId) throws ExecutionException, InterruptedException {
        Query query = QueryOperations.field("filmId").eq(val(movieId));
        return mClient.getTable(Film.class).where(query).execute().get().get(0);
    }
}
