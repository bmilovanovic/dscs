package com.example.movie.tables;

import android.text.TextUtils;

/**
 * Class representing one person whatever role he/she would have.
 */
public class Person {

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("name")
    private String mName;

    @com.google.gson.annotations.SerializedName("personId")
    private int mPersonId;

    @Override
    public boolean equals(Object o) {
        return o instanceof Person && TextUtils.equals(mId, ((Person) o).mId);
    }

    @Override
    public String toString() {
        return mPersonId + "\t\t" + mName;
    }

    public String getId() {
        return mId;
    }

    public final void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getPersonId() {
        return mPersonId;
    }

    public void setPersonId(int personId) {
        mPersonId = personId;
    }

}
