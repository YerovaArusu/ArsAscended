package at.yerova.arsascend.network

import at.yerova.arsascend.data.Keys

class ClientNetworkHandler {

    fun onMessageReceived(jsonString: String) {
        val message = NetworkParser.parse(jsonString) ?: return

        if (message.messageTarget == MessageTargetTypes.CLIENT_TO_SERVER) {
            println("WARN: Message wont be handled since its not a Client Message")
            return
        }

        if(message.senderId != "Networking") {
            println("WARN: Message wont be handled since Server is no longer connected!")
            return
        }

        NetworkEventBus.publish(message)

        // (Optional) Nur noch fürs Debugging:
        when (message) {
            is SyncMessage -> {
                // println("Client: Update für ${message.entityId} in den Bus geworfen.")
            }
            else -> println("WARN: Unerwarteter Message-Typ auf dem Client empfangen.")
        }
    }


    private fun handleSyncUpdate(msg: SyncMessage) {
        // Hier greifen wir auf das DTO zu und updaten die lokale Engine
        val targetEntityId = msg.entityId
        val payload = msg.payload

        println("Client: Update für $targetEntityId empfangen. HP: ${payload.getOrNull(Keys.CURRENT_HP)}")

    }
}