package com.ludovic.vimont.deezeropenapi.screens

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import org.junit.Assert

class RecyclerViewItemCountAssertion(private val expectedCount: Int = 0): ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }

        val recyclerView: RecyclerView = view as RecyclerView
        recyclerView.adapter?.let { adapter ->
            Assert.assertEquals(expectedCount, adapter.itemCount)
        }
    }
}