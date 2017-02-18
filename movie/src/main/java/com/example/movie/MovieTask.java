package com.example.movie;

import android.util.Log;

import com.example.aninterface.Parsable;
import com.example.aninterface.Storable;
import com.example.movie.tables.Country;
import com.example.movie.tables.Film;
import com.example.movie.tables.FilmCountry;
import com.example.movie.tables.FilmGenre;
import com.example.movie.tables.FilmPersonRole;
import com.example.movie.tables.Genre;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Encapsulation of movie parsing and storing.
 */
public class MovieTask extends MovieBean implements Parsable, Storable {

    private static final String TAG = MovieTask.class.getSimpleName();

    private final MobileServiceClient mClient;
    private int mFilmId;

    public MovieTask(MobileServiceClient client, int filmId) {
        mClient = client;
        mFilmId = filmId;
    }

    @Override
    public Storable parse() {
        Log.d(TAG, "Parsing movie with ID: " + mFilmId);
        Document doc;
        try {
            doc = getDocumentFromKey();
            parseLinks(doc);
        } catch (IOException | InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error! A movie with ID: " + mFilmId + " couldn't be parsed.");
            return null;
        }

        setTitle(doc.title());
        setSynopsis(parseCategory(doc.outerHtml(), "Kratak sadr"));
        setDuration(parseCategory(doc.outerHtml(), "Trajanje"));
        setTechnique(parseCategory(doc.outerHtml(), "Tehnika"));
        Log.d(TAG, "Movie " + getTitle() + " successfully parsed.");
        return this;
    }

    /**
     * Connects to the web and downloads the page based on movie id.
     *
     * @return HTML in a Document form
     * @throws IOException Error in connection.
     */
    private Document getDocumentFromKey() throws IOException {
        String url = "http://www.filmovi.com/yu/film/" + mFilmId + ".shtml";
        return Jsoup.connect(url).get();
    }

    /**
     * Goes through all the links found in the document and fills movie attributes
     * depending on the category.
     */
    private void parseLinks(Document doc) throws ExecutionException, InterruptedException {
        for (Element link : doc.select("a[href]")) {

            String linkCaption = link.text();
            if (linkCaption.isEmpty())
                continue;

            String linkCategory = extractCategoryFromLink(link.attr("href"));

            Log.d(TAG, "Parsing, linkCategory: " + linkCategory + " linkCaption: " + linkCaption);
            switch (linkCategory) {
                case "zanrovi":
                    addGenre(MovieHelper.getGenre(mClient, linkCaption));
                    break;
                case "zemlje":
                    addCountry(MovieHelper.getCountry(mClient, linkCaption));
                    break;
                case "godine":
                    setPremiere(linkCaption);
                    break;
                default:
                    // It is one of the roles
                    FilmPersonRole personRole = new FilmPersonRole();
                    personRole.setRoleId(MovieHelper.getRole(mClient, linkCategory).getRoleId());
                    personRole.setPersonId(MovieHelper.getPerson(mClient, linkCaption).getPersonId());
                    personRole.setFilmId(mFilmId);

                    addPerson(personRole);
            }
        }
    }

    /**
     * Extracts the part of the link which determines movie category.
     *
     * @param link Text to extract category from
     * @return Category
     */
    private String extractCategoryFromLink(String link) {
        int end = link.lastIndexOf('/');
        int start = link.substring(0, end - 1).lastIndexOf('/');
        return link.substring(start + 1, end);
    }

    /**
     * Extracts category value from raw html.
     *
     * @param categoryName Name of the category.
     * @return Category value
     */
    private String parseCategory(String html, String categoryName) {
        int start = html.indexOf(categoryName);
        if (start == -1)
            //Or NULL?
            return "";

        //Increment the start to skip the '>'
        start = html.indexOf('>', ++start);
        int end = html.indexOf('<', ++start);

        return html.substring(start, end).trim();
    }

    @Override
    public void store() {
        Log.d(TAG, "Storing movie\t" + getTitle() + "\tjust started.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                storeFilm();
                Log.d(TAG, "Storing movie\t" + getTitle() + "\tfinished.");
            }
        }).start();
    }

    /**
     * For each movie attribute, do proper storing to Azure.
     */
    private void storeFilm() {
        MobileServiceTable<Film> filmTable = mClient.getTable(Film.class);
        MobileServiceTable<FilmGenre> filmGenreTable = mClient.getTable(FilmGenre.class);
        MobileServiceTable<FilmCountry> filmCountryTable = mClient.getTable(FilmCountry.class);
        MobileServiceTable<FilmPersonRole> filmPersonRoleTable = mClient.getTable(FilmPersonRole.class);

        // Basic film attributes storing
        Film film = new Film();
        film.setFilmId(mFilmId);
        film.setTitle(getTitle());
        film.setSynopsis(getSynopsis());
        film.setDuration(getDuration());
        film.setTechnique(getTechnique());
        film.setPremiere(getPremiere());

        filmTable.insert(film);

        // Genre storing
        for (Genre genre : getGenres()) {
            FilmGenre filmGenre = new FilmGenre();
            filmGenre.setGenreId(genre.getGenreId());
            filmGenre.setFilmId(mFilmId);

            filmGenreTable.insert(filmGenre);
        }

        // Country storing
        for (Country country : getCountries()) {
            FilmCountry filmCountry = new FilmCountry();
            filmCountry.setCountryId(country.getCountryId());
            filmCountry.setFilmId(mFilmId);

            filmCountryTable.insert(filmCountry);
        }

        // People storing
        for (FilmPersonRole person : getPeople()) {
            filmPersonRoleTable.insert(person);
        }
    }

}
