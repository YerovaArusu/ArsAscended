package at.yerova.arsascend.network

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

const val version  :String = "0.0.1" //TODO: Placeholder :)
class GameNetworkClient(private val networkHandler: ClientNetworkHandler) {

    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 15.seconds
        }
    }

    private var session: DefaultClientWebSocketSession? = null
    private var listenJob: Job? = null

    suspend fun connect(serverIp: String, playerName: String, clientId: String): Boolean {
        return try {
            session = client.webSocketSession(
                urlString = "ws://$serverIp/game?clientId=$clientId&version=$version&playerName=$playerName"
            )
            
            startListening()
            true
        } catch (e: Exception) {
            Logger.e("Connection failed: ${e.message}")
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
                        networkHandler.onMessageReceived(receivedText)
                    }
                }
            } catch (e: Exception) {

                Logger.e("Connection to server lost: ${e.message}")
            }
        }
    }

    fun sendMessage(message: NetworkMessage) {
        val jsonString = NetworkParser.encode(message)
        CoroutineScope(Dispatchers.Default).launch {
            try {
                session?.send(Frame.Text(jsonString))
            } catch (e: Exception) {
                Logger.e("Error when sending: ${e.message}")
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