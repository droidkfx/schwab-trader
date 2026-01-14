package com.droidkfx.st.account

import com.droidkfx.st.config.CONFIG_ENTITY
import com.droidkfx.st.config.ConfigEntity
import com.droidkfx.st.util.databind.ValueDataBinding
import com.droidkfx.st.util.databind.readOnlyMapped
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val internalAccountModule = module {
    single {
        AccountRepository(get<ValueDataBinding<ConfigEntity>>(named(CONFIG_ENTITY)).readOnlyMapped { it.repositoryRoot })
    }
}

val accountModule = module {
    includes(internalAccountModule)
    single { AccountService(get(), get()) }
}