package com.ludovic.vimont.deezeropenapi.helper

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView

object ViewHelper {
    private const val TEXT_COLOR_PROPERTY_NAME = "textColor"

    fun getColor(context: Context, resourceDrawable: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(resourceDrawable)
        }
        return context.resources.getColor(resourceDrawable)
    }

    fun textColorAnimation(textView: TextView, fromColor: Int, toColor: Int, duration: Long, lambda: (() -> Unit)? = null) {
        val colorAnim: ObjectAnimator = ObjectAnimator.ofInt(textView, TEXT_COLOR_PROPERTY_NAME, fromColor, toColor)
        colorAnim.setEvaluator(ArgbEvaluator())
        colorAnim.duration = duration
        colorAnim.addListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                lambda?.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        colorAnim.start()
    }

    fun highlightTextViewWithColor(textView: TextView, text: String, textToHighlight: String, highlightColor: Int) {
        val spannable: Spannable = SpannableString(text)
        val beginOfTextToHighlight: Int = text.indexOf(textToHighlight)
        val endOfTextToHighlight: Int = beginOfTextToHighlight + textToHighlight.length
        val foregroundColorSpan = ForegroundColorSpan(highlightColor)
        spannable.setSpan(foregroundColorSpan, beginOfTextToHighlight, endOfTextToHighlight, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(spannable, TextView.BufferType.SPANNABLE)
    }
}