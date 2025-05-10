package itmo.edugoolda.api.user.storage

import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.api.user.storage.entities.toDomain
import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.EntityIdentifier
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseUserStorage : UserStorage {
    override suspend fun createUser(
        email: String,
        name: String,
        role: UserRole
    ): EntityIdentifier = transaction {
        UserEntity.new {
            this.name = name
            this.email = email
            this.role = role
        }.id.value.let(EntityIdentifier::parse)
    }

    override suspend fun updateUser(
        id: EntityIdentifier,
        email: String,
        name: String,
        bio: String?
    ): Unit = transaction {
        UserEntity.findByIdAndUpdate(id.value) {
            it.name = name
            it.email = email
            it.bio = bio
        }
    }

    override suspend fun getUserByEmail(email: String): UserInfoDomain? = transaction {
        UserEntity
            .find { UserTable.email eq email }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun getUserById(id: EntityIdentifier): UserInfoDomain? = transaction {
        UserEntity
            .find { UserTable.id eq id.value }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun markDeleted(id: EntityIdentifier) {
        UserEntity.findByIdAndUpdate(id.value) {
            it.isDeleted = true
        }
    }
}
