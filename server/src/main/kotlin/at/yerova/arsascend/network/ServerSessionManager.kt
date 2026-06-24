package at.yerova.arsascend.network

import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

object ServerSessionManager {
    // Speichert alle aktiven WebSocket-Verbindungen (threadsicher)
    val sessions = ConcurrentHashMap<String, WebSocketSession>()
}
