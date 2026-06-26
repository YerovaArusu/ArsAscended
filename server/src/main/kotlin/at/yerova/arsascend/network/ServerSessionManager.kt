package at.yerova.arsascend.network

import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

object ServerSessionManager {
    val sessions = ConcurrentHashMap<String, WebSocketSession>()
}
