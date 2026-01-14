package com.droidkfx.st.position

import com.droidkfx.st.config.CONFIG_ENTITY
import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val internalPositionModule = module {
    single { TargetPositionRepository(get<ValueDataBinding<ConfigEntity>>(named(CONFIG_ENTITY)).readOnlyMapped { it.repositoryRoot }) }
    singleOf(::PositionTargetService)
    single { PositionRepository(get<ValueDataBinding<ConfigEntity>>(named(CONFIG_ENTITY)).readOnlyMapped { it.repositoryRoot }) }
    singleOf(::PositionService)
}

val positionModule = module {
    includes(internalPositionModule)
    singleOf(::AccountPositionService)
}