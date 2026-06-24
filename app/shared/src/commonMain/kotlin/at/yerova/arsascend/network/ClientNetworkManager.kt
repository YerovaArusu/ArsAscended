package at.yerova.arsascend.network

import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.manager.Manager

class ClientNetworkManager(val networkHandler: ClientNetworkHandler) : Manager() {

    var timeSinceLastServerUpdate = 0
    override fun onUpdate(deltaTimeInMilliseconds: Int) {
        timeSinceLastServerUpdate += deltaTimeInMilliseconds

        if (timeSinceLastServerUpdate > 3000) {
            // TODO: UI Manager benachrichtigen: "Zeige Verbindungsabbruch-Symbol"
        }
    }

    fun onPacketReceived() {
        timeSinceLastServerUpdate = 0
    }
}