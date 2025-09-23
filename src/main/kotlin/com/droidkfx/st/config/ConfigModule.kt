package com.droidkfx.st.config

class ConfigModule(location: String = "application.config.json") {
    val configService = ConfigService(location)
}