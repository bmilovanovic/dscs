package com.example.aninterface;

import android.content.Context;

/**
 * Abstracts an entity that can be stored to shared database.
 */
public interface Storable {

    /**
     * Invites the object to store it's values to a permanent place.
     */
    void store();
}
