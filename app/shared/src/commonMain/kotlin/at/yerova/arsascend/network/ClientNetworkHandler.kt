package at.yerova.arsascend.network

import co.touchlab.kermit.Logger

class ClientNetworkHandler {

    fun onMessageReceived(jsonString: String) {
        val message = NetworkParser.parse(jsonString) ?: return

        if (message.messageTarget == MessageTargetTypes.CLIENT_TO_SERVER) {
            Logger.w("WARN: Message wont be handled since its not a Client Message")
            return
        }

        if(message.senderId != "Networking") {
            Logger.w("Message wont be handled since Server is no longer connected!")
            return
        }

        NetworkEventBus.publish(message)
    }
}