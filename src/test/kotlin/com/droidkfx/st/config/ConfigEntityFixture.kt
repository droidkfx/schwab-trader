package com.droidkfx.st.config

import com.droidkfx.st.util.databind.toDataBinding

fun defaultConfigEntity(
    schwabConfig: SchwabClientConfig = defaultSchwabClientConfig(),
    repositoryRoot: String = "build/tmp/test-repository"
) = ConfigEntity(
    schwabConfig = schwabConfig,
    repositoryRoot = repositoryRoot,
).toDataBinding()

