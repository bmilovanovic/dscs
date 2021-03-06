package com.example.dscs.job;

import android.text.TextUtils;

/**
 * Represents an item in a Task list.
 */
public class Task {

    public static final int INVALID = -1;
    public static final int INSERTED = 0;
    public static final int SUBMITTED = 1;
    public static final int IN_PROGRESS = 2;
    public static final int DONE = 3;

    @com.google.gson.annotations.SerializedName("status")
    private int mStatus;

    @com.google.gson.annotations.SerializedName("key")
    private int mKey;

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    public Task(int filmId, int status) {
        mKey = filmId;
        mStatus = status;
    }

    public String getId() {
        return mId;
    }

    public final void setId(String id) {
        mId = id;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getKey() {
        return mKey;
    }

    public void setKey(int key) {
        mKey = key;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Task && TextUtils.equals(mId, ((Task) o).mId);
    }

    @Override
    public String toString() {
        return mKey + "\t\t" + getStatusDescription(mStatus);
    }

    /**
     * For each state a task can be in, provides description.
     *
     * @param status State of a task.
     * @return Textual description.
     */
    public static String getStatusDescription(int status) {
        switch (status) {
            case INSERTED:
                return "Inserted";
            case SUBMITTED:
                return "Submitted";
            case IN_PROGRESS:
                return "In progress";
            case DONE:
                return "Done";
            case INVALID:
                return "Invalid";
        }
        return "Unknown";
    }
}