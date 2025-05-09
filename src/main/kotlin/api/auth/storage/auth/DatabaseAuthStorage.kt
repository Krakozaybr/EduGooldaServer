package itmo.edugoolda.api.auth.storage.auth

import io.ktor.util.logging.*
import itmo.edugoolda.api.auth.domain.AuthCredentials
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.checkPassword
import itmo.edugoolda.utils.database.reduceByAnd
import itmo.edugoolda.utils.hashPassword
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class DatabaseAuthStorage(
    private val logger: Logger
) : AuthStorage {
    override suspend fun saveCredentials(
        id: EntityIdentifier,
        authCredentials: AuthCredentials
    ) = transaction {
        if (authCredentials !is AuthCredentials.EmailPassword) throw RuntimeException()

        val alreadyExists = AuthTable.select(AuthTable.userId)
            .where {
                listOf(
                    AuthTable.providerType eq authCredentials.providerType.toDTO(),
                    AuthTable.userId eq id.value
                ).reduceByAnd()
            }
            .empty()
            .not()

        val hashedPassword = hashPassword(authCredentials.password)

        if (alreadyExists) {
            AuthTable.update(
                where = {
                    listOf(
                        AuthTable.userId eq id.value,
                        AuthTable.providerEntityId eq authCredentials.email
                    ).reduceByAnd()
                },
                body = {
                    it[userId] = id.value
                    it[providerEntityId] = authCredentials.email
                    it[providerType] = authCredentials.providerType.toDTO()
                    it[passwordHash] = hashedPassword
                }
            )

            return@transaction
        }

        AuthTable.insert {
            it[userId] = id.value
            it[providerEntityId] = authCredentials.email
            it[providerType] = authCredentials.providerType.toDTO()
            it[passwordHash] = hashedPassword
        }
    }

    override suspend fun checkCredentials(authCredentials: AuthCredentials): EntityIdentifier? = transaction {
        if (authCredentials !is AuthCredentials.EmailPassword) throw RuntimeException()

        val row = AuthTable.select(AuthTable.userId, AuthTable.passwordHash)
            .where {
                listOf(
                    AuthTable.providerType eq authCredentials.providerType.toDTO(),
                    AuthTable.providerEntityId eq authCredentials.email
                ).reduceByAnd()
            }
            .singleOrNull() ?: return@transaction null

        row[AuthTable.userId].takeIf {
            checkPassword(
                password = authCredentials.password,
                hashed = row[AuthTable.passwordHash],
            )
        }?.value?.let(EntityIdentifier::parse)
    }

    override suspend fun getHashedPassword(userId: EntityIdentifier): String? = transaction {
        AuthTable.select(AuthTable.passwordHash)
            .where {
                AuthTable.userId eq userId.value
            }
            .singleOrNull()
            ?.get(AuthTable.passwordHash)
    }
}