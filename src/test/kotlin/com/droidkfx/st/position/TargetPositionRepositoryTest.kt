package com.droidkfx.st.position

import com.droidkfx.st.config.defaultConfigEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class TargetPositionRepositoryTest {
    private lateinit var tempRoot: Path
    private lateinit var repository: TargetPositionRepository

    @BeforeEach
    fun setUp() {
        tempRoot = Files.createTempDirectory("target-position-repo-test-")
        repository = TargetPositionRepository(defaultConfigEntity(repositoryRoot = tempRoot.toString()))
    }

    @AfterEach
    fun tearDown() {
        tempRoot.toFile().deleteRecursively()
    }

    @Test
    fun `save then load roundtrip for target positions`() {
        val accountId = "acct-1"
        val targets = listOf(
            defaultPositionTarget(symbol = "AAPL"),
            defaultPositionTarget(symbol = "MSFT"),
        )

        repository.saveTargetPositions(accountId, targets)
        val loaded = repository.loadTargetPositions(accountId)

        assertEquals(targets, loaded)
    }

    @Test
    fun `loadTargetPositions returns empty when missing`() {
        val loaded = repository.loadTargetPositions("missing")
        assertTrue(loaded.isEmpty())
    }

    @Test
    fun `clear removes all stored target position files`() {
        repository.saveTargetPositions("a1", listOf(defaultPositionTarget()))
        repository.saveTargetPositions("a2", listOf(defaultPositionTarget()))
        assertTrue(repository.loadTargetPositions("a1").isNotEmpty())

        repository.clear()

        assertTrue(repository.loadTargetPositions("a1").isEmpty())
        assertTrue(repository.loadTargetPositions("a2").isEmpty())

        val dir = tempRoot.resolve("position").resolve("target").toFile()
        val jsonFiles = dir.listFiles { _, name -> name.endsWith(".json") }
        assertTrue(jsonFiles == null || jsonFiles.isEmpty())
    }
}
