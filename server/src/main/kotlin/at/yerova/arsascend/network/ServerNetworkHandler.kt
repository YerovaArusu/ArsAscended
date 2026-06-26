package at.yerova.arsascend.network
import io.ktor.websocket.WebSocketSession
import org.slf4j.LoggerFactory

class ServerNetworkHandler {

    private val logger = LoggerFactory.getLogger(ServerNetworkHandler::class.java)
    fun onMessageReceived(session: WebSocketSession, jsonString: String) {
        val message = NetworkParser.parse(jsonString) ?: return

        if (message.messageTarget == MessageTargetTypes.SERVER_TO_CLIENT) {
            logger.warn("Message wont be handled since its not a server Message")
            return
        }

        //TODO: Once we have proper client ids add a method to check if the senderId matches anyone connected.
        if(message.senderId != PLACEHOLDER_NETWORK_TARGET) { //"Networking" is a placeholder for now
            logger.warn("Message wont be handled since client is no longer connected!")
            return
        }

        when (message) {
            is PlayerCommandMessage -> handlePlayerCommand(session, message)
            else -> logger.warn("Unerwarteter Message-Typ auf dem Server empfangen.")
        }
    }

    private fun handlePlayerCommand(session: WebSocketSession, msg: PlayerCommandMessage) {
        logger.info("Server verarbeitet Befehl '${msg.command}' von ${msg.playerId}")
    }
}