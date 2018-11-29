package com.r3pi.task.utils;

import com.r3pi.task.R;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Created by margarita on 11/29/18.
 */
public class SearchFragmentTest {
    @Test
    public void testTyping() throws Exception {
        onView(withId(R.id.search_view)).perform(clearText(),typeText("Arm"));
    }
}