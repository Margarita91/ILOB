package com.r3pi.task.utils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * Created by margarita on 11/24/18.
 */

public class Constants {
    public static final int RC_AUTHORIZE_CONTACTS = 1;
    public static final int RC_REAUTHORIZE = 2;
    public static final String APPLICATION_ID = "com.r3pi.task";
    public static final String BAR_CODE = "bar_code";
    public static long BOOK_AMOUNT_PER_CALL = 40;
    public static int ISBN_LENGTH2006 = 10; // International Standard Book Number 2006
    public static int ISBN_LENGTH2007 = 13; // International Standard Book Number 2007
    public static String SEARCH_FRAGMENT_TAG = "SEARCH_FRAGMENT_TAG";
    public static String METADATA = "metadata";
    public static String BOOK_URL = "infoUrl";
    public static final int RC_BARCODE_CAPTURE = 1000;
    public static final int PERM_REQUEST_CAMERA = 1001;
    public static final int PERM_READ_URI = 1002;

    public static final int PERM_REQUEST_READ_EXTERNAL_STORAGE = 1002;
    public static final String ISBN = "+isbn:";
    /**
     * Global instance of the HTTP transport.
     */
    public static HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    /**
     * Global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static final String GOOGLE_API_URL = "https://www.googleapis.com/auth/contacts.readonly";


}
