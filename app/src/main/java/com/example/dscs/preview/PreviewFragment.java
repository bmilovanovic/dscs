package com.example.dscs.preview;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.dscs.Network;
import com.example.dscs.R;
import com.example.dscs.job.Task;
import com.example.dscs.utility.PreferenceUtility;
import com.example.dscs.utility.UiUtils;
import com.example.movie.tables.Film;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A fragment that fetches domain data from the Azure and presents it in a list.
 */
public class PreviewFragment extends Fragment {

    private static final String TAG = PreviewFragment.class.getSimpleName();

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Class> mDomainClasses = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDomainClasses.add(Task.class);
        mDomainClasses.addAll(PreferenceUtility.getCurrentJob().getAllDomainClasses());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_preview_layout, container, false);

        setupRefreshingLayout(contentView);

        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        refresh();
    }

    /**
     * Sets swipe up to refresh layout.
     *
     * @param rootView The highest view in the hierarchy.
     */
    private void setupRefreshingLayout(View rootView) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mListView = (ListView) rootView.findViewById(R.id.list);
        mListView.setAdapter(null);
    }

    /**
     * Refresh the preview list.
     */
    private void refresh() {
        if (mSwipeRefreshLayout != null && !mDomainClasses.isEmpty()) {
            mSwipeRefreshLayout.setRefreshing(true);

            final Handler handler = new Handler();
            final Context context = getActivity();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<PreviewAdapter.TaskPreview> list = getTaskItemsList(context);
                    ListAdapter adapter = new PreviewAdapter(context, list);

                    setPreviewAdapter(adapter);
                }

                private void setPreviewAdapter(final ListAdapter adapter) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (adapter.getCount() > 0) {
                                mListView.setAdapter(adapter);
                            } else {
                                UiUtils.showEmptyTableToast(context, Task.class);
                                mListView.setAdapter(null);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }).start();
        }
    }

    /**
     * Forms a list of a task and film tables.
     *
     * @param context Context for networking.
     * @return List of the items to display.
     */
    private ArrayList<PreviewAdapter.TaskPreview> getTaskItemsList(Context context) {
        ArrayList<PreviewAdapter.TaskPreview> list = new ArrayList<>();
        try {
            MobileServiceList<Task> tasks =
                    Network.getTable(context, Task.class).where()
                            .orderBy("key", QueryOrder.Ascending).execute().get();
            MobileServiceList<Film> films =
                    Network.getTable(context, Film.class).where()
                            .orderBy("filmId", QueryOrder.Ascending).execute().get();
            int filmIndex = 0;
            for (Task task : tasks) {
                int taskId = task.getKey();
                int filmId = 0;
                // Run to the next filmId which is not lower than key in Task table
                while (filmId < taskId && filmIndex < films.size()) {
                    filmId = films.get(filmIndex).getFilmId();
                    if (filmId < taskId) {
                        filmIndex++;
                    }
                }

                PreviewAdapter.TaskPreview item = new PreviewAdapter.TaskPreview();
                item.key = taskId;
                item.status = task.getStatus();
                if (taskId == filmId) {
                    item.title = films.get(filmIndex).getTitle();
                }

                list.add(item);
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error connecting to the Azure!");
            e.printStackTrace();
        }

        return list;
    }
}
