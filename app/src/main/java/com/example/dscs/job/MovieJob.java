package com.example.dscs.job;

import android.content.Context;
import android.util.Log;

import com.example.aninterface.Storable;
import com.example.dscs.Network;
import com.example.dscs.utility.PreferenceUtility;
import com.example.movie.MovieHelper;
import com.example.movie.MovieTask;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.Query;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

/**
 * Concrete implementation of an application job.
 */
public class MovieJob extends Job {

    private static final String TAG = MovieJob.class.getSimpleName();

    private static final String AZURE_APP_URL = "https://dscs.azurewebsites.net";
    private static final int NUMBER_OF_MOVIES = 926;

    @Override
    public String getAzureUrl() {
        return AZURE_APP_URL;
    }

    @Override
    public Storable parseTask(Context context, int key) {
        return new MovieTask(Network.getClient(context), key).parse();
    }

    @Override
    public List<Class> getAllDomainClasses() {
        return MovieHelper.getAllDomainClasses();
    }

    @Override
    public void init(Context context) throws ExecutionException, InterruptedException {
        if (PreferenceUtility.shouldInitTasks(context)) {
            initTasks(context);
        }
    }

    /**
     * Initializes Azure tasks table so work can start after.
     *
     * @param context Context for networking.
     * @throws ExecutionException   Error connecting to Azure.
     * @throws InterruptedException Somebody interrupted the network operation.
     */
    private void initTasks(Context context) throws ExecutionException, InterruptedException {
        int numberOfTasks = Math.min(PreferenceUtility.getNumberOfTasks(context), NUMBER_OF_MOVIES);
        MobileServiceTable<Task> taskTable = Network.getTable(context, Task.class);

        for (int i = 1; i < numberOfTasks; i++) {
            Query query = QueryOperations.field("key").eq(val(i));
            MobileServiceList<Task> list = taskTable.where(query).execute().get();
            if (list.size() == 0) {
                Task task = new Task(i, 1);
                taskTable.insert(task).get();
                Log.d(TAG, "Tasks initialization:\tInserted new task with key " + i);
            } else {
                Log.d(TAG, "Tasks initialization:\tTask with key " + i + " already exists. "
                        + "It's status is " + list.get(0).getStatus());
            }
        }
    }
}
