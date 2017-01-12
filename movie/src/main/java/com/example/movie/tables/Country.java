package com.example.movie.tables;

import android.text.TextUtils;

/**
 * Class storing one origin country of a movie.
 */
public class Country {

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("countryName")
    private String mCountryName;

    @com.google.gson.annotations.SerializedName("countryId")
    private int mCountryId;

    @Override
    public boolean equals(Object o) {
        return o instanceof Country && TextUtils.equals(mId, ((Country) o).mId);
    }

    @Override
    public String toString() {
        return mCountryId + "\t\t" + mCountryName;
    }

    public String getId() {
        return mId;
    }

    public final void setId(String id) {
        mId = id;
    }

    public String getCountryName() {
        return mCountryName;
    }

    public void setCountryName(String countryName) {
        mCountryName = countryName;
    }

    public int getCountryId() {
        return mCountryId;
    }

    public void setCountryId(int countryId) {
        mCountryId = countryId;
    }
}
