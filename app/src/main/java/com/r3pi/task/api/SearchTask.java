package com.r3pi.task.api;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;
import com.google.common.primitives.Ints;
import com.r3pi.task.utils.Constants;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by margarita on 11/24/18.
 */
public class SearchTask extends AsyncTask<String, Void, List<Volume>> {

    private SearchListener mSearchListener;

    public void setSearchListener(SearchListener searchListener) {
        this.mSearchListener = searchListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSearchListener.onSearching();
    }

    @Override
    protected List<Volume> doInBackground(String... params) {
        String query = params[0];
        if (Ints.tryParse(query) != null && (query.length() == Constants.ISBN_LENGTH2007 ||
                                             query.length() == Constants.ISBN_LENGTH2006)) {
            query = query.concat(Constants.ISBN + query);
        }
        Books books = new Books.Builder(AndroidHttp.newCompatibleTransport(),
                                        AndroidJsonFactory.getDefaultInstance(), null)
                .setApplicationName(Constants.APPLICATION_ID)
                .build();
        try {
            Books.Volumes.List list = books.volumes().list(query).setProjection("LITE")
                                           .setMaxResults(Constants.BOOK_AMOUNT_PER_CALL);
            return list.execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    protected void onPostExecute(List<Volume> volumes) {
        super.onPostExecute(volumes);
        mSearchListener.onResult(volumes == null ? Collections.<Volume>emptyList() : volumes);
    }

    public interface SearchListener {
        void onSearching();
        void onResult(List<Volume> volumes);
    }
}
