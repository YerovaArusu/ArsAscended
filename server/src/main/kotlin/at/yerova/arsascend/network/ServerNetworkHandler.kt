package at.yerova.arsascend.network
import io.ktor.websocket.WebSocketSession

class ServerNetworkHandler {

    fun onMessageReceived(session: WebSocketSession, jsonString: String) {
        val message = NetworkParser.parse(jsonString) ?: return

        if (message.messageTarget == MessageTargetTypes.SERVER_TO_CLIENT) {
            println("WARN: Message wont be handled since its not a server Message")
            return
        }

        //TODO: Once we have proper client ids add a method to check if the senderId matches anyone connected.
        if(message.senderId != PLACEHOLDER_NETWORK_TARGET) { //"Networking" is a placeholder for now
            println("WARN: Message wont be handled since client is no longer connected!")
            return
        }

        when (message) {
            is PlayerCommandMessage -> handlePlayerCommand(session, message)
            else -> println("WARN: Unerwarteter Message-Typ auf dem Server empfangen.")
        }
    }

    private fun handlePlayerCommand(session: WebSocketSession, msg: PlayerCommandMessage) {
        println("Server verarbeitet Befehl '${msg.command}' von ${msg.playerId}")
    }
}