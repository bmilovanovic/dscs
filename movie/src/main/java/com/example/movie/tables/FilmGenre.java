package com.example.movie.tables;

import android.text.TextUtils;

/**
 * Class for assigning genres to a movie.
 */
public class FilmGenre {

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("filmId")
    private int mFilmId;

    @com.google.gson.annotations.SerializedName("genreId")
    private int mGenreId;

    @Override
    public boolean equals(Object o) {
        return o instanceof FilmGenre && TextUtils.equals(mId, ((FilmGenre) o).mId);
    }

    @Override
    public String toString() {
        return mFilmId + "\t\t" + mGenreId;
    }

    public String getId() {
        return mId;
    }

    public final void setId(String id) {
        mId = id;
    }

    public int getFilmId() {
        return mFilmId;
    }

    public void setFilmId(int filmId) {
        mFilmId = filmId;
    }

    public int getGenreId() {
        return mGenreId;
    }

    public void setGenreId(int genreId) {
        mGenreId = genreId;
    }

}
