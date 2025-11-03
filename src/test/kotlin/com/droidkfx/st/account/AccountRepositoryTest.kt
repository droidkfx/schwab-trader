package com.droidkfx.st.account

import com.droidkfx.st.config.defaultConfigEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class AccountRepositoryTest {
    private lateinit var tempRoot: Path
    private lateinit var repository: AccountRepository

    @BeforeEach
    fun setUp() {
        tempRoot = Files.createTempDirectory("acct-repo-test-")
        repository = AccountRepository(defaultConfigEntity(repositoryRoot = tempRoot.toString()))
    }

    @AfterEach
    fun tearDown() {
        // Best-effort cleanup
        tempRoot.toFile().deleteRecursively()
    }

    @Test
    fun `saveAccount then getAccount returns the same entity`() {
        val account = defaultAccount()

        repository.saveAccount(account)
        val loaded = repository.getAccount(account.id)

        assertEquals(account, loaded)
    }

    @Test
    fun `loadAccounts returns all saved accounts`() {
        val a1 = defaultAccount()
        val a2 = defaultAccount()

        repository.saveAccount(a1)
        repository.saveAccount(a2)

        val all = repository.loadAccounts()
        assertEquals(2, all.size)
        // ensure both ids are present
        val ids = all.map { it.id }.toSet()
        assertTrue(ids.contains(a1.id) && ids.contains(a2.id))
    }

    @Test
    fun `getAccount throws when account not found`() {
        assertThrows(IllegalArgumentException::class.java) {
            repository.getAccount("missing-id")
        }
    }

    @Test
    fun `clear removes all stored accounts`() {
        repository.saveAccount(defaultAccount())
        repository.saveAccount(defaultAccount())

        assertTrue(repository.loadAccounts().isNotEmpty())

        repository.clear()

        val remaining = repository.loadAccounts()
        assertNotNull(remaining)
        assertTrue(remaining.isEmpty())

        // Also, ensure a directory exists but is empty of JSON files
        val acctDir = tempRoot.resolve("account").toFile()
        val jsonFiles = acctDir.listFiles { _, name -> name.endsWith(".json") }
        assertTrue(jsonFiles == null || jsonFiles.isEmpty())
    }
}
