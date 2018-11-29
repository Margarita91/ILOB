package com.r3pi.task.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.api.services.books.model.Volume;
import com.r3pi.task.api.SearchTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margarita on 11/24/18.
 */
public class SearchFragment extends Fragment implements SearchTask.SearchListener {
    private SearchTask searchTask;
    private SearchTask.SearchListener searchListener;

    private String latestQuery;
    private List<Volume> volumeList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        searchListener = (SearchTask.SearchListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void searchBooks(String query, int paging) {
        if (query.equalsIgnoreCase(latestQuery) && paging == 1) {
            return;
        }
        if (searchTask != null) {
            searchTask.cancel(true);
        }
        latestQuery = query;
        searchTask = new SearchTask();
        searchTask.setSearchListener(this);
        searchTask.execute(query);
    }

    @Override
    public void onSearching() {
        volumeList.clear();
        searchListener.onSearching();
    }

    @Override
    public void onResult(List<Volume> volumes) {
        volumeList = volumes;
        searchListener.onResult(volumeList);
    }

    public String getLatestQuery() {
        return latestQuery;
    }
    public List<Volume> getVolumeList() {
        return volumeList;
    }
}
