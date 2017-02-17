package com.example.dscs;

import android.content.Context;

import com.example.aninterface.Storable;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Single encapsulation for an app-wide job that is being processed.
 */
abstract public class Job {

    /**
     * Every job has the results that need to be stored somewhere. Find it via url.
     *
     * @return url to the azure database point.
     */
    abstract String getAzureUrl();

    /**
     * Perform actually fetching and parsing the page into an structured entity.
     *
     * @param context Context for networking
     * @param key     Key of the task for getting the url of the page
     * @return An object that can be stored
     */
    abstract Storable parseTask(Context context, int key);

    /**
     * Gets all the classes / tables from the Azure for domain content.
     *
     * @return List of domain classes
     */
    abstract public List<Class> getAllDomainClasses();

    /**
     * If there's something that needs to be done upon starting a job, it's place is here.
     *
     * @param context Context for networking
     */
    abstract void init(Context context) throws ExecutionException, InterruptedException;
}
