package itmo.edugoolda.api.backup

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.Koin
import java.io.File

fun Route.configureBackupRouting(koin: Koin) {
    authenticate(optional = true) {
        get("/v1/backup") {
            val file = File("database.db")
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment
                    .withParameter(
                        key = ContentDisposition.Parameters.FileName,
                        value = "database.db"
                    )
                    .toString()
            )
            call.respondFile(file)
        }
    }
}