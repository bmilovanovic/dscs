package com.example.movie;

import com.example.movie.tables.Country;
import com.example.movie.tables.Film;
import com.example.movie.tables.FilmPersonRole;
import com.example.movie.tables.Genre;

import java.util.LinkedList;
import java.util.List;

/**
 * POJO Class encapsulating all movie attributes.
 */
class MovieBean {
    private Film mFilm = new Film();
    private List<Genre> genres = new LinkedList<>();
    private List<Country> countries = new LinkedList<>();

    private List<FilmPersonRole> people = new LinkedList<>();

    String getTitle() {
        return mFilm.getTitle();
    }

    void setTitle(String title) {
        mFilm.setTitle(title);
    }

    List<Genre> getGenres() {
        return genres;
    }

    void addGenre(Genre genre) {
        genres.add(genre);
    }

    String getSynopsis() {
        return mFilm.getSynopsis();
    }

    void setSynopsis(String synopsis) {
        mFilm.setSynopsis(synopsis);
    }

    List<Country> getCountries() {
        return countries;
    }

    void addCountry(Country country) {
        countries.add(country);
    }

    String getDuration() {
        return mFilm.getDuration();
    }

    void setDuration(String duration) {
        mFilm.setDuration(duration);
    }

    String getTechnique() {
        return mFilm.getTechnique();
    }

    void setTechnique(String technique) {
        mFilm.setTechnique(technique);
    }

    String getPremiere() {
        return mFilm.getPremiere();
    }

    void setPremiere(String premiere) {
        mFilm.setPremiere(premiere);
    }

    void addPerson(FilmPersonRole person) {
        people.add(person);
    }

    List<FilmPersonRole> getPeople() {
        return people;
    }
}

