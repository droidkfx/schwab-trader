package com.droidkfx.st.schwab.oauth

import com.droidkfx.st.util.databind.toDataBinding
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class OauthRepositoryTest {
    private lateinit var tempRoot: Path
    private lateinit var repository: OauthRepository

    @BeforeEach
    fun setUp() {
        tempRoot = Files.createTempDirectory("oauth-repo-test-")
        repository = OauthRepository(tempRoot.toString().toDataBinding())
    }

    @AfterEach
    fun tearDown() {
        tempRoot.toFile().deleteRecursively()
    }

    @Test
    fun `saveToken then loadExistingToken returns same`() {
        val token = defaultOauthTokenResponse()
        repository.saveToken(token)

        val loaded = repository.loadExistingToken()
        assertEquals(token, loaded)
    }

    @Test
    fun `loadExistingToken returns null when missing`() {
        val loaded = repository.loadExistingToken()
        assertNull(loaded)
    }

    @Test
    fun `deleteToken removes the token file`() {
        val token = defaultOauthTokenResponse()
        repository.saveToken(token)
        assertTrue(repository.loadExistingToken() != null)

        repository.deleteToken()

        val loaded = repository.loadExistingToken()
        assertNull(loaded)

        val dir = tempRoot.resolve("oauth").toFile()
        val jsonFiles = dir.listFiles { _, name -> name.endsWith(".json") }
        assertTrue(jsonFiles == null || jsonFiles.isEmpty())
    }
}
