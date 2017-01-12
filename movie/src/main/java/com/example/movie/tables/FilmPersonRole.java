package com.example.movie.tables;

import android.text.TextUtils;

/**
 * The main table for assigning roles to persons for a movie.
 */
public class FilmPersonRole {

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("filmId")
    private int mFilmId;

    @com.google.gson.annotations.SerializedName("personId")
    private int mPersonId;

    @com.google.gson.annotations.SerializedName("roleId")
    private int mRoleId;

    @Override
    public boolean equals(Object o) {
        return o instanceof FilmPersonRole && TextUtils.equals(mId, ((FilmPersonRole) o).mId);
    }

    @Override
    public String toString() {
        return mFilmId + "\t\t" + mPersonId + "\t\t" + mRoleId;
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

    public int getPersonId() {
        return mPersonId;
    }

    public void setPersonId(int personId) {
        mPersonId = personId;
    }

    public int getRoleId() {
        return mRoleId;
    }

    public void setRoleId(int roleId) {
        mRoleId = roleId;
    }

}
