package com.droidkfx.st.position

import com.droidkfx.st.config.defaultConfigEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

class PositionRepositoryTest {
    private lateinit var tempRoot: Path
    private lateinit var repository: PositionRepository

    @BeforeEach
    fun setUp() {
        tempRoot = Files.createTempDirectory("position-repo-test-")
        repository = PositionRepository(defaultConfigEntity(repositoryRoot = tempRoot.toString()))
    }

    @AfterEach
    fun tearDown() {
        tempRoot.toFile().deleteRecursively()
    }

    @Test
    fun `save then load roundtrip for positions`() {
        val accountId = "acct-1"
        val positions = defaultCurrentPositions(
            accountCash = BigDecimal("1234.56"),
            positions = listOf(
                defaultPosition(symbol = "AAPL", quantity = BigDecimal("10"), lastKnownPrice = BigDecimal("150.00")),
                defaultPosition(symbol = "MSFT", quantity = BigDecimal("5"), lastKnownPrice = BigDecimal("300.00")),
            )
        )

        repository.savePositions(accountId, positions)
        val loaded = repository.loadPositions(accountId)

        assertEquals(positions, loaded)
    }

    @Test
    fun `loadPositions returns default when missing`() {
        val accountId = "missing"
        val loaded = repository.loadPositions(accountId)
        assertEquals(CurrentPositions(BigDecimal.ZERO, emptyList()), loaded)
    }

    @Test
    fun `clear removes all stored position files`() {
        repository.savePositions("a1", defaultCurrentPositions())
        repository.savePositions("a2", defaultCurrentPositions())
        assertTrue(repository.loadPositions("a1").positions.isNotEmpty())

        repository.clear()

        // After clear, both account files should be gone
        assertEquals(CurrentPositions(BigDecimal.ZERO, emptyList()), repository.loadPositions("a1"))
        assertEquals(CurrentPositions(BigDecimal.ZERO, emptyList()), repository.loadPositions("a2"))

        val dir = tempRoot.resolve("position").resolve("current").toFile()
        val jsonFiles = dir.listFiles { _, name -> name.endsWith(".json") }
        assertTrue(jsonFiles == null || jsonFiles.isEmpty())
    }
}
