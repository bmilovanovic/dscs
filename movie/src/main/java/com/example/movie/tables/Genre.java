package com.example.movie.tables;

import android.text.TextUtils;

/**
 * Class encapsulating movie genre.
 */
public class Genre {

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("genreId")
    private int mGenreId;

    @com.google.gson.annotations.SerializedName("genreName")
    private String mGenreName;

    @Override
    public boolean equals(Object o) {
        return o instanceof Genre && TextUtils.equals(mId, ((Genre) o).mId);
    }

    @Override
    public String toString() {
        return mGenreId + "\t\t" + mGenreName;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public int getGenreId() {
        return mGenreId;
    }

    public void setGenreId(int genreId) {
        mGenreId = genreId;
    }

    public String getGenreName() {
        return mGenreName;
    }

    public void setGenreName(String genreName) {
        mGenreName = genreName;
    }
}
