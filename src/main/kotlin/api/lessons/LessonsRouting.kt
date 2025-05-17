package itmo.edugoolda.api.lessons

import io.ktor.server.routing.*
import itmo.edugoolda.api.lessons.route.v1.*
import org.koin.core.Koin

fun Route.configureLessonsRouting(koin: Koin) {
    // V1
    route("/v1") {
        createLessonRoute(koin)
        deleteLessonRoute(koin)
        lessonDetailsRoute(koin)
        lessonListRoute(koin)
        studentSendMessageRoute(koin)
        teacherSendMessageRoute(koin)
        deleteMessageRoute(koin)
        setSolutionStatusRoute(koin)
        solutionListRoute(koin)
        solutionDetailsRoute(koin)
    }
}