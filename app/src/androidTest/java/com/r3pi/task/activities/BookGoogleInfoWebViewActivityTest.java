package com.r3pi.task.activities;

import android.content.Intent;

import com.r3pi.task.utils.Constants;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.*;

/**
 * Created by margarita on 11/29/18.
 */
public class BookGoogleInfoWebViewActivityTest {
    @Rule
    public ActivityTestRule<BookGoogleInfoWebViewActivity> bookGoogleInfoWebViewActivityTestRule = new ActivityTestRule<BookGoogleInfoWebViewActivity>(BookGoogleInfoWebViewActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(ApplicationProvider.getApplicationContext(),BookGoogleInfoWebViewActivity.class);
            intent.putExtra(Constants.METADATA,"Value");
            return intent;
        }
    };

}