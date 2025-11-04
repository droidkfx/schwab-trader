package com.droidkfx.st.config

fun getUsersAppDirPath(): String = when (System.getProperty("os.name").lowercase().contains("win")) {
    true -> System.getenv("APPDATA") + "/../Local" + "/schwab-trader"
    false -> System.getProperty("user.home") + "/.schwab-trader"
}