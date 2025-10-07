package com.droidkfx.st.account

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: String,
    val name: String,
    val accountNumber: String,
    val accountNumberHash: String
)