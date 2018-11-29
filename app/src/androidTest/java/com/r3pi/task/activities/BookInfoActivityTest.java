package com.r3pi.task.activities;

import android.app.Activity;
import android.content.Intent;

import com.r3pi.task.utils.Constants;

import org.junit.Rule;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.*;

/**
 * Created by margarita on 11/29/18.
 */
public class BookInfoActivityTest extends Activity {

    @Rule
    public ActivityTestRule<BookInfoActivityTest> bookInfoActivityTestActivityTestRule = new ActivityTestRule<BookInfoActivityTest>(BookInfoActivityTest.class){
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(ApplicationProvider.getApplicationContext(),BookInfoActivityTest.class);
            intent.putExtra(Constants.METADATA,"Value");
            return intent;
        }
    };
}