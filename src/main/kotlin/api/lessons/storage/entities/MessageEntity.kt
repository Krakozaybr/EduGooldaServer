package itmo.edugoolda.api.lessons.storage.entities

import itmo.edugoolda.api.lessons.domain.MessageEntityDomain
import itmo.edugoolda.api.lessons.domain.SolutionMessageDomain
import itmo.edugoolda.api.lessons.storage.tables.MessagesTable
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.api.user.storage.entities.toDomain
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.BaseEntity
import itmo.edugoolda.utils.database.BaseEntityClass
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class MessageEntity(id: EntityID<UUID>) : BaseEntity(id, MessagesTable) {
    companion object : BaseEntityClass<MessageEntity>(MessagesTable)

    var text by MessagesTable.text
    var solutionId by MessagesTable.solutionId
    var authorId by MessagesTable.authorId

    var author by UserEntity referencedOn MessagesTable.authorId
    var solution by SolutionEntity referencedOn MessagesTable.solutionId
}

fun MessageEntity.toSolutionMessageDomain() = SolutionMessageDomain(
    id = EntityIdentifier.parse(id.value),
    sentAt = createdAt.toInstant(TimeZone.UTC),
    message = text,
    author = author.toDomain(),
)

fun MessageEntity.toMessageEntityDomain() = MessageEntityDomain(
    id = EntityIdentifier.parse(id.value),
    text = text,
    solutionId = EntityIdentifier.parse(solutionId.value),
    authorId = EntityIdentifier.parse(authorId.value),
)
