package com.ludovic.vimont.deezeropenapi.screens.detail

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.ludovic.vimont.deezeropenapi.R
import com.ludovic.vimont.deezeropenapi.screens.DrawableMatcher
import com.ludovic.vimont.deezeropenapi.screens.home.HomeActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class DetailActivityTest {
    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule<HomeActivity>(HomeActivity::class.java, true, true)

    @Test
    fun testDetailActivityLoadingState() {
        // We wait for albums
        Thread.sleep(1_000)

        val recyclerViewAlbums = onView(
            allOf(
                withId(R.id.recycler_view_albums),
                childAtPosition(
                    withId(android.R.id.content),
                    0
                )
            )
        )
        recyclerViewAlbums.perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        // We wait for tracks
        Thread.sleep(1_000)

        val firstTrackItem: Matcher<View> = childAtPosition(withId(R.id.recycler_view_tracks), 0)
        Espresso.onView(childAtPosition(firstTrackItem, 0)).check(matches(DrawableMatcher(R.drawable.play_to_pause)))
    }

    private fun withTextColor(expectedId: Int): Matcher<View?>? {
        return object : BoundedMatcher<View?, TextView>(TextView::class.java) {
            override fun matchesSafely(textView: TextView): Boolean {
                val colorId: Int = ContextCompat.getColor(textView.context, expectedId)
                return textView.currentTextColor == colorId
            }

            override fun describeTo(description: Description) {
                description.appendText("with text color: ")
                description.appendValue(expectedId)
            }
        }
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
