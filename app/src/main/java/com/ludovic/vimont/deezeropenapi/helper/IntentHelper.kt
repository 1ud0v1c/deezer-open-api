package com.ludovic.vimont.deezeropenapi.helper

import android.content.Context
import android.content.Intent
import android.net.Uri

object IntentHelper {
    private const val SHARE_TYPE = "text/plain"

    fun shareLink(context: Context, url: String, subject: String, title: String = "Share Music") {
        val share = Intent(Intent.ACTION_SEND)
        share.type = SHARE_TYPE
        share.putExtra(Intent.EXTRA_SUBJECT, subject)
        share.putExtra(Intent.EXTRA_TEXT, url)
        context.startActivity(Intent.createChooser(share, title))
    }

    fun openWebPage(context: Context, url: String) {
        val webPage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webPage)
        context.startActivity(intent)
    }
}