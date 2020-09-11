package com.ludovic.vimont.deezeropenapi.api

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class TrustAllCertificateManager: X509TrustManager {
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf()
    }
}