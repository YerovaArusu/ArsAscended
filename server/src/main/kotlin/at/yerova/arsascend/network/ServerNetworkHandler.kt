package at.yerova.arsascend.network

import io.ktor.websocket.*
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue

class ServerNetworkHandler {
    object ServerInputQueue {
        val commands = ConcurrentLinkedQueue<NetworkMessage>()
    }

    private val logger = LoggerFactory.getLogger(ServerNetworkHandler::class.java)

    fun onMessageReceived(session: WebSocketSession, jsonString: String) {
        val message = NetworkParser.parse(jsonString) ?: return

        if (message.messageTarget == MessageTargetTypes.SERVER_TO_CLIENT) {
            logger.warn("Message wont be handled since its not a server Message")
            return
        }

        //TODO: Once we have proper client ids add a method to check if the senderId matches anyone connected.
        if (message.senderId != PLACEHOLDER_NETWORK_TARGET) { //"Networking" is a placeholder for now
            logger.warn("Message wont be handled since client is no longer connected!")
            return
        }

        //TODO: If we have some messages that the the game instance shouldn't listen to then add the handler for that message here and return at the end

        ServerInputQueue.commands.add(message)

    }

}