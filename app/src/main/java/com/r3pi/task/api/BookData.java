package com.r3pi.task.api;

import java.io.Serializable;

/**
 * Created by margarita on 11/24/18.
 */

public class BookData implements Serializable {
    private String mId;
    private String mTitle;
    private String mSubtitle;
    private String mDescription;
    private String mPublisher;
    private String [] mAuthors;
    private String mInfoUrl;
    private String mCover;
    private String mPublishedDate;


    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public void setSubtitle(String mSubtitle) {
        this.mSubtitle = mSubtitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public void setPublisher(String mPublisher) {
        this.mPublisher = mPublisher;
    }

    public String[] getAuthors() {
        return mAuthors;
    }

    public void setAuthors(String[] mAuthors) {
        this.mAuthors = mAuthors;
    }

    public String getCover() {
        return mCover;
    }

    public void setCover(String mCover) {
        this.mCover = mCover;
    }

    public String getInfoUrl() {
        return mInfoUrl;
    }

    public void setInfoUrl(String mInfoUrl) {
        this.mInfoUrl = mInfoUrl;
    }

    public String getPublishedDate() {
        return mPublishedDate;
    }

    public void setPublishedDate(String mPublishedDate) {
        this.mPublishedDate = mPublishedDate;
    }
}
