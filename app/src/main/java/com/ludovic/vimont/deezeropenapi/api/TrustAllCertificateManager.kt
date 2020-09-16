package com.ludovic.vimont.deezeropenapi.api

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

/**
 * While using OKHttpBuilder for Android 4.4. We accept all certificate without any verification,
 * but thanks to the class HostnameVerifier. We are able to filter it, only on deezer hostname.
 * @see OkHttpBuilder
 */
class TrustAllCertificateManager: X509TrustManager {
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf()
    }
}