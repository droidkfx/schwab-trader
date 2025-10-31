package com.droidkfx.st.account

import io.ktor.util.sha1
import java.util.UUID

fun defaultAccount(
    id: String = UUID.randomUUID().toString(),
    name: String = "Account Fixture",
    accountNumber: String = "123456789",
    accountNumberHash: String = String(sha1("123456789".toByteArray()))
) = Account(
    id = id,
    name = name,
    accountNumber = accountNumber,
    accountNumberHash = accountNumberHash
)