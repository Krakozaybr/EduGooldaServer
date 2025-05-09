package module

import org.koin.core.context.stopKoin
import kotlin.test.AfterTest

interface ModuleTest {
    @AfterTest
    fun deleteDatabase() {
        removeDatabase()
    }

    @AfterTest
    fun clearKoin() {
        stopKoin()
    }
}