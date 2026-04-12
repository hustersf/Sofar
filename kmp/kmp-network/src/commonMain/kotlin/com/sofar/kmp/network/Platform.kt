package com.sofar.kmp.network

import io.ktor.client.HttpClientConfig

expect fun platform(): String

expect fun HttpClientConfig<*>.configureTrustAll()

expect fun HttpClientConfig<*>.configureCustomCertificate(pemContent: String)