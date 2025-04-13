package module

import kotlin.test.AfterTest

interface ModuleTest {
    @AfterTest
    fun deleteDatabase() {
        removeDatabase()
    }
}