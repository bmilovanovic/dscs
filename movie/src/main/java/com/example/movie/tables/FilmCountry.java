package com.example.movie.tables;

import android.text.TextUtils;

/**
 * Class for assigning countries to a movie.
 */
public class FilmCountry {

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("filmId")
    private int mFilmId;

    @com.google.gson.annotations.SerializedName("countryId")
    private int mCountryId;

    @Override
    public boolean equals(Object o) {
        return o instanceof FilmCountry && TextUtils.equals(mId, ((FilmCountry) o).mId);
    }

    @Override
    public String toString() {
        return mFilmId + "\t\t" + mCountryId;
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

    public int getCountryId() {
        return mCountryId;
    }

    public void setCountryId(int countryId) {
        mCountryId = countryId;
    }

}
