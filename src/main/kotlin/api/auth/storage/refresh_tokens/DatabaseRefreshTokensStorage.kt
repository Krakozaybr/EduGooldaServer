package itmo.edugoolda.api.auth.storage.refresh_tokens

import itmo.edugoolda.utils.EntityId
import itmo.edugoolda.utils.toCurrentLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseRefreshTokensStorage : RefreshTokensStorage {
    override suspend fun removeToken(refreshToken: String) = transaction {
        RefreshTokensTable.deleteWhere {
            token eq refreshToken
        }
    }

    override suspend fun putToken(
        refreshToken: String,
        userId: EntityId,
        expiresAt: LocalDateTime
    ): Unit = transaction {
        RefreshTokensTable.insert {
            it[token] = refreshToken
            it[RefreshTokensTable.userId] = userId.value
            it[RefreshTokensTable.expiresAt] = expiresAt
        }
    }

    override suspend fun getEntityIdByRefreshTokenIfNotExpired(refreshToken: String): EntityId? = transaction {
        val row = RefreshTokensTable.select(RefreshTokensTable.userId, RefreshTokensTable.expiresAt)
            .where {
                RefreshTokensTable.token eq refreshToken
            }
            .singleOrNull()

        row?.let {
            row[RefreshTokensTable.userId].value
                .let(EntityId::parse)
                .takeIf {
                    row[RefreshTokensTable.expiresAt] > Clock.System.now().toCurrentLocalDateTime()
                }
        }
    }
}