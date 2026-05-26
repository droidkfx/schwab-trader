package com.droidkfx.st.view.about

import com.droidkfx.st.BuildInfo

class AboutViewModel {
    val appVersion: String = BuildInfo.VERSION
    val gitHash: String = BuildInfo.GIT_HASH
    val gitBranch: String = BuildInfo.GIT_BRANCH
    val buildTime: String = BuildInfo.BUILD_TIME

    val jvmVersion: String = System.getProperty("java.version") ?: "unknown"
    val jvmVendor: String = System.getProperty("java.vendor") ?: "unknown"

    val osName: String = System.getProperty("os.name") ?: "unknown"
    val osVersion: String = System.getProperty("os.version") ?: "unknown"
    val osArch: String = System.getProperty("os.arch") ?: "unknown"
}
