package itmo.edugoolda.plugins

import itmo.edugoolda.api.auth.storage.AuthTable
import itmo.edugoolda.api.user.storage.UserTable
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

fun Application.configureDatabase(config: ApplicationConfig) {
    val url = config.property("storage.jdbcURL").getString()
    val driver = config.property("storage.driverClassName").getString()

    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    val database = Database.connect(
        url = url,
        driver = driver
    )

    transaction(database) {
        SchemaUtils.create(
            UserTable,
            AuthTable
        )
    }
}
