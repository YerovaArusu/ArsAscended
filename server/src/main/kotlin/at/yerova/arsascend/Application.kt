package at.yerova.arsascend

import at.yerova.arsascend.game.ServerGameInstance
import at.yerova.arsascend.network.ServerSessionManager
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val serverGameInstance = ServerGameInstance()
    val networkHandler = serverGameInstance.networkHandler

    serverGameInstance.start()
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    monitor.subscribe(ApplicationStopped) {
        log.info("Application stopping!")

        try {
            ServerSessionManager.sessions.values.forEach { session ->
                CoroutineScope(Dispatchers.IO).launch {
                    session.close(CloseReason(CloseReason.Codes.GOING_AWAY, "Server Closed!"))
                }
            }

            //TODO: Do Game Instance Server shutdown.
            serverGameInstance.dispose()

        } catch (e: Exception) {
            log.error("Error while Shutting down! ${e.message}")
        }
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
                log.warn("Client [$clientId] is reconnecting. Old Session will be disposed.")
                existingSession.close(CloseReason(CloseReason.Codes.NORMAL, "Logged in from another location"))
            }

            ServerSessionManager.sessions[clientId] = this
            log.error("Connected client [$clientId] (Name: $playerName, Version: $clientVersion)!")

            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val receivedText = frame.readText()
                        networkHandler.onMessageReceived(this, receivedText)
                    }
                }
            } catch (e: Exception) {
                log.error("Connection for Client [$clientId] stopped: ${e.message}")
            } finally {
                if (ServerSessionManager.sessions[clientId] == this) {
                    ServerSessionManager.sessions.remove(clientId)
                    log.info("Session for Client [$clientId] closed")
                }
            }
        }
    }
}