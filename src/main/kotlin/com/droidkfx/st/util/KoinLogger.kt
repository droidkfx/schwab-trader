package com.droidkfx.st.util

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

class KoinLogger : Logger(Level.DEBUG) {
    private val logger = logger {}

    override fun display(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> logger.debug { msg }
            Level.INFO -> logger.info { msg }
            Level.ERROR -> logger.error { msg }
            Level.WARNING -> logger.warn { msg }
            Level.NONE -> {}
        }
    }
}