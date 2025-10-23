package com.droidkfx.st.account

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: String,
    var name: String,
    val accountNumber: String,
    val accountNumberHash: String
)