package com.droidkfx.st.config

import com.droidkfx.st.util.databind.ValueDataBinding

fun defaultConfigEntity(
    schwabConfig: SchwabClientConfig = defaultSchwabClientConfig(),
    repositoryRoot: String = "build/tmp/test-repository"
) = ValueDataBinding(
    ConfigEntity(
        schwabConfig = schwabConfig,
        repositoryRoot = repositoryRoot,
    )
)
