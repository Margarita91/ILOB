package com.r3pi.task.api;

import android.accounts.Account;
import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import com.r3pi.task.activities.MainActivity;
import com.r3pi.task.utils.Constants;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by margarita on 11/29/18.
 */

public class GetContactsTask extends AsyncTask<Void, Void, List<Person>> {
    private Account mAccount;
    private MainActivity mMainActivity;
    public GetContactsTask(Account account, MainActivity mainActivity) {
        mAccount = account;
        mMainActivity = mainActivity;
    }

    @Override
    protected List<Person> doInBackground(Void... params) {
        List<Person> result = null;
        try {
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            mMainActivity,
                            Collections.singleton(Constants.GOOGLE_API_URL)
                    );
            credential.setSelectedAccount(mAccount);
            People service = new People.Builder(Constants.HTTP_TRANSPORT, Constants.JSON_FACTORY, credential)
                    .setApplicationName("REST API sample")
                    .build();
            ListConnectionsResponse connectionsResponse = service
                    .people()
                    .connections()
                    .list("people/me")
                    .execute();
            result = connectionsResponse.getConnections();
        } catch (UserRecoverableAuthIOException userRecoverableException) {
            // Explain to the user again why you need these OAuth permissions
            // And prompt the resolution to the user again:
            mMainActivity.startActivityForResult(userRecoverableException.getIntent(), Constants.RC_REAUTHORIZE);
        } catch (IOException e) {
            // Other non-recoverable exceptions.
        }
        return result;
    }

    @Override
    protected void onCancelled() {

    }

    @Override
    protected void onPostExecute(List<Person> connections) {

    }
}