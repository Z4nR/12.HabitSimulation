package com.dicoding.habitapp.ui.list

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dicoding.habitapp.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

//TODO 16 : Write UI test to validate when user tap Add Habit (+), the AddHabitActivity displayed

@RunWith(AndroidJUnit4::class)
class HabitActivityTest {

    @Before
    fun setUp(){
        ActivityScenario.launch(HabitListActivity::class.java)
    }

    @Test
    fun openAdd(){
        onView(withId(R.id.rv_habit))
            .check(matches(isDisplayed()))
        onView(withId(R.id.fab))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withId(R.id.add_ed_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.add_ed_minutes_focus))
            .check(matches(isDisplayed()))
        onView(withId(R.id.sp_priority_level))
            .check(matches(isDisplayed()))
        onView(withId(R.id.add_tv_start_time))
            .check(matches(isDisplayed()))
        onView(withId(R.id.action_save))
            .check(matches(isDisplayed()))
    }

}