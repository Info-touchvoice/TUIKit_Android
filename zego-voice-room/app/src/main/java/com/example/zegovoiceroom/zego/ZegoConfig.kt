package com.example.zegovoiceroom.zego

/**
 * Replace these values with credentials from the ZEGOCLOUD console.
 * The demo screens still compile and run with placeholders, while the
 * embedded ZEGO Voice Room Kit fragment starts only when both are set.
 */
object ZegoConfig {
    const val APP_ID: Long = 0L
    const val APP_SIGN: String = ""

    val hasCredentials: Boolean
        get() = APP_ID > 0 && APP_SIGN.isNotBlank()
}
