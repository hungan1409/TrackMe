package com.example.trackme.data

object Constants {
    const val DATABASE_NAME = "trackme.db"
}

object HttpClient {
    const val CONNECT_TIMEOUT = 10L
    const val READ_TIMEOUT = 10L
    const val WRITE_TIMEOUT = 10L
    const val CONNECTION_TIME_OUT_MLS = CONNECT_TIMEOUT * 1000L
}

object TrackDatabase {
    const val VERSION_DATABASE = 1
}

object Authentication {
    const val MAX_RETRY = 1
}
