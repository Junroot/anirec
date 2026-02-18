package com.anirec.global.exception

class SyncAlreadyRunningException(
    message: String = "Sync is already running",
) : RuntimeException(message)
