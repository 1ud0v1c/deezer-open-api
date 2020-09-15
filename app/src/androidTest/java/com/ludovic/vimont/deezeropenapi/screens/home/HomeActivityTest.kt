package com.ludovic.vimont.deezeropenapi.screens.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.ludovic.vimont.deezeropenapi.R
import com.ludovic.vimont.deezeropenapi.screens.RecyclerViewItemCountAssertion
import com.ludovic.vimont.deezeropenapi.screens.detail.DetailActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class HomeActivityTest {
    @Rule
    @JvmField
    var homeActivityTestRule = ActivityTestRule(HomeActivity::class.java)

    @Test
    fun testDetailActivityLaunch() {
        Intents.init()

        // We wait a timeout of 1 second for the request to be made
        Thread.sleep(1_000)

        val recyclerView: ViewInteraction = onView(
            allOf(
                withId(R.id.recycler_view_albums),
                childAtPosition(
                    withId(android.R.id.content),
                    0
                )
            )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        // On click, we should be in DetailActivity
        intended(hasComponent(DetailActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun testLoadingMoreDataWhileScrolling() {
        Thread.sleep(1_000)
        onView(withId(R.id.recycler_view_albums)).check(RecyclerViewItemCountAssertion(25))
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_albums)).perform(ViewActions.swipeUp())

        Thread.sleep(1_000)
        onView(withId(R.id.recycler_view_albums)).check(RecyclerViewItemCountAssertion(50))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
