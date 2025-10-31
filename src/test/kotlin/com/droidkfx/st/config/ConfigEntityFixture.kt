package com.droidkfx.st.config

fun defaultConfigEntity(
    schwabConfig: SchwabClientConfig = defaultSchwabClientConfig(),
    repositoryRoot: String = "build/tmp/test-repository"
) = ConfigEntity(
    schwabConfig = schwabConfig,
    repositoryRoot = repositoryRoot,
)
