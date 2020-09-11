package com.ludovic.vimont.deezeropenapi.api

import com.ludovic.vimont.deezeropenapi.helper.AndroidVersions
import okhttp3.OkHttpClient
import java.security.SecureRandom
import javax.net.ssl.*

object OkHttpBuilder {
    fun getClient(): OkHttpClient {
        return if (AndroidVersions.inferiorOrEqualAtKitKat) {
            getKitKatOkHttpClient()
        } else {
            OkHttpClient.Builder().build()
        }
    }

    /**
     * We create an other instance of OkHttpClient for Android 4.4. We can't easily check the
     * certificates of a server so we accept them all, but we only accept request made to the hostname
     * of Deezer & his CDN !
     * @see: https://developer.android.com/training/articles/security-ssl.html#WarningsSslSocket
     */
    private fun getKitKatOkHttpClient(): OkHttpClient {
        val trustAllCerts: Array<TrustManager> = arrayOf(TrustAllCertificateManager())

        // Install the all-trusting trust manager
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier { _, session ->
            val hostnameVerifier: HostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()
            val deezerHostname: String = DeezerAPI.Constants.HOSTNAME
            val cdnHostname: String = DeezerAPI.Constants.CDN_HOSTNAME
            hostnameVerifier.verify(deezerHostname, session) || hostnameVerifier.verify(cdnHostname, session)
        }

        return builder.build()
    }
}