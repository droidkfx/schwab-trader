package com.droidkfx.st.account

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AccountTest {
    @Test
    fun `serialize round trip`() {
        //assemble
        val account = defaultAccount()


        var deserializedAccount: Account? = null
        assertDoesNotThrow {
            //act
            val serializationResult = Json.encodeToString(account)
            deserializedAccount = Json.decodeFromString<Account>(serializationResult)
        }

        assertEquals(account, deserializedAccount)
    }
}