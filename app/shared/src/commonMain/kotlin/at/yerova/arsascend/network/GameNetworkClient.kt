package at.yerova.arsascend.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class GameNetworkClient(private val networkHandler: ClientNetworkHandler) {
    
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 15.seconds
        }
    }

    private var session: DefaultClientWebSocketSession? = null
    private var listenJob: Job? = null

    // Baut die WebSocket-Verbindung auf
    suspend fun connect(serverIp: String, playerName: String, clientId: String): Boolean {
        return try {
            // Wichtig: webSocketSession() hält die Verbindung offen, 
            // bis wir sie manuell schließen oder ein Fehler auftritt.
            session = client.webSocketSession(
                urlString = "ws://$serverIp/game?clientId=$clientId&playerName=$playerName"
            )
            
            // Starte die Lausch-Schleife im Hintergrund
            startListening()
            true
        } catch (e: Exception) {
            println("Verbindung fehlgeschlagen: ${e.message}")
            false
        }
    }

    private fun startListening() {
        listenJob?.cancel() // Alte Jobs aufräumen
        listenJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                for (frame in session!!.incoming) {
                    if (frame is Frame.Text) {
                        val receivedText = frame.readText()
                        // Ab hier übernimmt dein vorhandener Code!
                        networkHandler.onMessageReceived(receivedText)
                    }
                }
            } catch (e: Exception) {
                println("Client: Verbindung vom Server getrennt -> ${e.message}")
            }
        }
    }

    // Hilfsfunktion zum Senden der DTOs/Messages
    fun sendMessage(message: NetworkMessage) {
        val jsonString = NetworkParser.encode(message)
        CoroutineScope(Dispatchers.Default).launch {
            try {
                session?.send(Frame.Text(jsonString))
            } catch (e: Exception) {
                println("Fehler beim Senden: ${e.message}")
            }
        }
    }

    fun disconnect() {
        listenJob?.cancel()
        CoroutineScope(Dispatchers.Default).launch {
            session?.close()
        }
    }
}