package com.example.movie.tables;

import android.text.TextUtils;

/**
 * Class encapsulating basic movie attributes such as title, year, etc.
 */
public class Film {

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("filmId")
    private int mFilmId;

    @com.google.gson.annotations.SerializedName("title")
    private String mTitle;

    @com.google.gson.annotations.SerializedName("synopsis")
    private String mSynopsis;

    @com.google.gson.annotations.SerializedName("duration")
    private String mDuration;

    @com.google.gson.annotations.SerializedName("technique")
    private String mTechnique;

    @com.google.gson.annotations.SerializedName("premiere")
    private String mPremiere;

    @Override
    public boolean equals(Object o) {
        return o instanceof Film && TextUtils.equals(mId, ((Film) o).mId);
    }

    @Override
    public String toString() {
        return mFilmId + "\t\t" + mTitle + "\t\t(" + mPremiere + ")";
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

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public void setSynopsis(String synopsis) {
        mSynopsis = synopsis;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }

    public String getTechnique() {
        return mTechnique;
    }

    public void setTechnique(String technique) {
        mTechnique = technique;
    }

    public String getPremiere() {
        return mPremiere;
    }

    public void setPremiere(String premiere) {
        mPremiere = premiere;
    }
}
