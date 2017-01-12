package com.example.dscs;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.movie.tables.Film;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A fragment that fetches data from the azure and presents it in a list.
 */
public class PreviewFragment extends Fragment {

    private static final String TAG = PreviewFragment.class.getSimpleName();

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Spinner mSpinnerView;
    private List<Class> mDomainClasses = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDomainClasses.add(Task.class);
        mDomainClasses.addAll(PreferenceUtility.getCurrentJob(getActivity()).getAllDomainClasses());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_preview_layout, container, false);

        setupSpinner(contentView);
        setupRefreshingLayout(contentView);

        return contentView;
    }

    private void setupSpinner(View rootView) {
        mSpinnerView = (Spinner) rootView.findViewById(R.id.tableSpinner);
        mSpinnerView.setAdapter(getSpinnerAdapter());
        mSpinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private SpinnerAdapter getSpinnerAdapter() {
        List<String> classNamesList = new ArrayList<>();
        for (Class clazz : mDomainClasses) {
            classNamesList.add(clazz.getSimpleName());
        }

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, classNamesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

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

    private void refresh() {
        if (mSpinnerView != null && mSwipeRefreshLayout != null && !mDomainClasses.isEmpty()) {
            mSwipeRefreshLayout.setRefreshing(true);
            final Class clazz = mDomainClasses.get(mSpinnerView.getSelectedItemPosition());

            final boolean isTaskTable = Task.class.equals(clazz);

            final Handler handler = new Handler();
            final Context context = getActivity();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ListAdapter adapter;
                    if (isTaskTable) {
                        ArrayList<PreviewTaskAdapter.TaskPreview> list = getTaskItemsList(context);
                        adapter = new PreviewTaskAdapter(context, list);
                    } else {
                        @SuppressWarnings("unchecked")
                        ArrayList<Object> list = getItems(clazz);
                        adapter = new PreviewAdapter<>(context, list);
                    }

                    setPreviewAdapter(adapter);
                }

                private void setPreviewAdapter(final ListAdapter adapter) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (adapter.getCount() > 0) {
                                mListView.setAdapter(adapter);
                            } else {
                                UiUtils.showEmptyTableToast(context, clazz);
                                mListView.setAdapter(null);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }).start();
        }
    }

    private <E> ArrayList<E> getItems(final Class<E> clazz) {

        ArrayList<E> list = new ArrayList<>();
        try {
            list = Network.getTable(getActivity(), clazz).where().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return list;
    }

    private ArrayList<PreviewTaskAdapter.TaskPreview> getTaskItemsList(Context context) {
        ArrayList<PreviewTaskAdapter.TaskPreview> list = new ArrayList<>();
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

                PreviewTaskAdapter.TaskPreview item = new PreviewTaskAdapter.TaskPreview();
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
