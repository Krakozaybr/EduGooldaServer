package itmo.edugoolda.api.group

import io.ktor.server.routing.*
import itmo.edugoolda.api.group.route.v1.group.*
import itmo.edugoolda.api.group.route.v1.requests_and_invitations.*
import itmo.edugoolda.api.group.route.v1.subject.createSubjectRoute
import itmo.edugoolda.api.group.route.v1.subject.deleteSubjectRoute
import itmo.edugoolda.api.group.route.v1.subject.subjectDetailsRoute
import itmo.edugoolda.api.group.route.v1.subject.subjectsListRoute
import org.koin.core.Koin

fun Route.configureGroupRouting(koin: Koin) {

    // V1
    route("/v1") {
        // General
        groupCreateRoute(koin)
        groupUpdateRoute(koin)
        groupSetActiveRoute(koin)
        groupDeleteRoute(koin)
        userGroupsRoute(koin)
        groupStudentsRoute(koin)
        groupRemoveStudentRoute(koin)
        groupDetailsRoute(koin)
        groupSetFavourite(koin)
        groupLeaveRoute(koin)

        // Subjects
        subjectDetailsRoute(koin)
        subjectsListRoute(koin)
        createSubjectRoute(koin)
        deleteSubjectRoute(koin)

        // Joining requests
        joiningInformationRoute(koin)
        joinGroupByCodeRoute(koin)
        joinGroupByLinkRoute(koin)
        groupJoinRequestsRoute(koin)
        joinRequestsRoute(koin)
        joinRequestActionRoute(koin)
        bannedUsersRoute(koin)
        unbanUserRoute(koin)
    }
}