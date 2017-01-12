package com.example.movie.tables;

import android.text.TextUtils;

/**
 * Class encapsulating one role in a movie.
 */
public class Role {

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("roleName")
    private String mRoleName;

    @com.google.gson.annotations.SerializedName("roleId")
    private int mRoleId;

    @Override
    public boolean equals(Object o) {
        return o instanceof Role && TextUtils.equals(mId, ((Role) o).mId);
    }

    @Override
    public String toString() {
        return mRoleId + "\t\t" + mRoleName;
    }

    public String getId() {
        return mId;
    }

    public final void setId(String id) {
        mId = id;
    }

    public String getRoleName() {
        return mRoleName;
    }

    public void setRoleName(String roleName) {
        mRoleName = roleName;
    }

    public int getRoleId() {
        return mRoleId;
    }

    public void setRoleId(int roleId) {
        mRoleId = roleId;
    }

}
