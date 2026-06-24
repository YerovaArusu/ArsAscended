package at.yerova.arsascend

import at.yerova.arsascend.game.ServerGameInstance
import at.yerova.arsascend.network.ServerSessionManager
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlin.time.Duration.Companion.seconds

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val serverGameInstance = ServerGameInstance()
    val networkHandler = serverGameInstance.networkHandler

    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/game") {
            val clientId = call.request.queryParameters["clientId"]

            val clientVersion = call.request.queryParameters["version"] ?: "unknown"
            val playerName = call.request.queryParameters["playerName"] ?: "UnknownPlayer"

            if (clientId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Missing clientId parameter"))
                return@webSocket
            }

            val existingSession = ServerSessionManager.sessions[clientId]
            if (existingSession != null) {
                println("Server: Warnung! Client [$clientId] verbindet sich neu. Alte Session wird gekickt.")
                existingSession.close(CloseReason(CloseReason.Codes.NORMAL, "Logged in from another location"))
            }

            ServerSessionManager.sessions[clientId] = this
            println("Server: Client verbunden [$clientId] (Name: $playerName, Version: $clientVersion)")

            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val receivedText = frame.readText()
                        networkHandler.onMessageReceived(this, receivedText)
                    }
                }
            } catch (e: Exception) {
                println("Server: Verbindung beendet [$clientId]: ${e.message}")
            } finally {
                if (ServerSessionManager.sessions[clientId] == this) {
                    ServerSessionManager.sessions.remove(clientId)
                    println("Server: Client endgültig getrennt [$clientId]")
                }
            }
        }
    }
}