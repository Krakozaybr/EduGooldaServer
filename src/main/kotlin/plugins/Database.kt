package itmo.edugoolda.plugins

import io.ktor.server.application.*
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.Koin
import java.sql.Connection

fun Application.configureDatabase(
    config: ApplicationConfig,
    koin: Koin
) {
    val url = config.property("storage.jdbcURL").getString()
    val driver = config.property("storage.driverClassName").getString()

    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    val database = Database.connect(
        url = url,
        driver = driver
    )

    transaction(database) {
        SchemaUtils.create(*koin.getAll<Table>().toTypedArray())
    }
}
