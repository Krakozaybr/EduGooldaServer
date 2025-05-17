package itmo.edugoolda.api.lessons.storage.lessons

import itmo.edugoolda.api.lessons.domain.*
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.Paged
import kotlinx.datetime.Instant

interface LessonsStorage {
    suspend fun createLesson(
        authorId: EntityIdentifier,
        name: String,
        description: String?,
        isEstimatable: Boolean,
        deadline: Instant?,
        opensAt: Instant?,
        groupIds: List<EntityIdentifier>
    ): LessonFullDetailsDomain

    suspend fun checkIsLessonAuthor(lessonId: EntityIdentifier, userId: EntityIdentifier): Boolean

    suspend fun checkIsLessonStudent(lessonId: EntityIdentifier, userId: EntityIdentifier): Boolean

    suspend fun deleteLesson(lessonId: EntityIdentifier)

    suspend fun getLessonsForTeacher(
        userId: EntityIdentifier,
        skip: Int,
        maxCount: Int,
        search: String?,
        groupId: EntityIdentifier?
    ): Paged<LessonInfoDomain>

    suspend fun getLessonsForStudent(
        userId: EntityIdentifier,
        skip: Int,
        maxCount: Int,
        search: String?,
        groupId: EntityIdentifier?
    ): Paged<LessonInfoDomain>

    suspend fun getLessonStudentDetails(
        studentId: EntityIdentifier,
        lessonId: EntityIdentifier
    ): LessonStudentDetailsDomain

    suspend fun getLessonTeacherDetails(
        lessonId: EntityIdentifier
    ): LessonFullDetailsDomain

    suspend fun sendMessage(
        authorId: EntityIdentifier,
        solutionId: EntityIdentifier,
        message: String
    )

    suspend fun getLessonEntity(
        lessonId: EntityIdentifier
    ): LessonEntityDomain?

    suspend fun getSolutionEntity(
        lessonId: EntityIdentifier,
        studentId: EntityIdentifier
    ): SolutionEntityDomain?

    suspend fun getSolutionEntity(
        solutionId: EntityIdentifier
    ): SolutionEntityDomain?

    suspend fun getSolutionDetails(
        solutionId: EntityIdentifier
    ): SolutionDetailsDomain?

    suspend fun createSolution(
        teacherId: EntityIdentifier,
        lessonId: EntityIdentifier,
        studentId: EntityIdentifier,
    ): SolutionEntityDomain

    suspend fun getMessageEntity(
        id: EntityIdentifier
    ): MessageEntityDomain?

    suspend fun deleteMessage(
        id: EntityIdentifier
    )

    suspend fun setSolutionStatus(
        solutionId: EntityIdentifier,
        status: SolutionStatus
    )

    suspend fun getSolutionsForTeacher(
        teacherId: EntityIdentifier,
        skip: Int,
        maxCount: Int,
        lessonId: EntityIdentifier?,
        groupId: EntityIdentifier?,
        status: SolutionStatus?
    ): Paged<SolutionInfoDomain>
}