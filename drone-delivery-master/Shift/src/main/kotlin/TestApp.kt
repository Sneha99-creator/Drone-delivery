import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File
import java.io.FilenameFilter
import java.util.stream.Collectors
import java.util.stream.Stream

data class LengthRequest(val str: String)
data class LengthResponse(val length: Int)

data class PathRequest(val possiblePath: String)
data class PathResponse(val paths: Collection<String>)

fun getFiles(basePath: String, beginsWith: String) : Collection<String> {

    val textFilter = FilenameFilter { dir, name ->
        val lowercaseName = name.toLowerCase()
        if (lowercaseName.startsWith(beginsWith)) {
            true
        } else {
            false
        }
    }

    val files : Array<File> = File(basePath).listFiles(textFilter)

    return files.map { file ->
        file.absolutePath
    }
}

fun stringLength(str: String) : Int {
    return str.length
}

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(CORS) {
        host("localhost:4200")
    }
    install(Routing) {
        get("/") {
            call.respondText("Hello World")
        }
        post("/length") {
            val request = call.receive<LengthRequest>()
            call.respond(LengthResponse(stringLength(request.str)))
        }
        post("/paths") {
            val request = call.receive<PathRequest>()

            val inputString = request.possiblePath
            val lastSlash = inputString.lastIndexOf("/")

            if(lastSlash < 0) {
                call.respond(PathResponse(listOf()))
            } else {
                val response = PathResponse(getFiles(inputString.substring(0, lastSlash + 1), inputString.substring(lastSlash + 1)))
                call.respond(response)
            }
        }
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8081, watchPaths = listOf("TestAppKt"), module = Application::module).start()
}

