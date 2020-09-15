package com.ludovic.vimont.deezeropenapi.screens

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher


class DrawableMatcher(private val expectedId: Int): TypeSafeMatcher<View>() {
    private var resourceName: String = ""

    override fun matchesSafely(target: View?): Boolean {
        if (target !is ImageView) {
            return false
        }
        val imageView: ImageView = target
        if (expectedId < 0) {
            return imageView.drawable == null
        }
        val resources: Resources = target.getContext().resources
        val expectedDrawable: Drawable = resources.getDrawable (expectedId)
        resourceName = resources.getResourceEntryName(expectedId)

        val vectorDrawable: AnimatedVectorDrawable = imageView.drawable as AnimatedVectorDrawable
        val otherVectorDrawable: AnimatedVectorDrawable = expectedDrawable as AnimatedVectorDrawable
        return getBitmap(vectorDrawable)?.sameAs(getBitmap(otherVectorDrawable)) ?: false
    }

    private fun getBitmap(drawable: Drawable): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun describeTo(description: Description?) {
        if (description != null) {
            description.appendText("with drawable from resource id: ")
            description.appendValue(expectedId)
            description.appendText("[")
            description.appendText(resourceName)
            description.appendText("]")
        }
    }
}