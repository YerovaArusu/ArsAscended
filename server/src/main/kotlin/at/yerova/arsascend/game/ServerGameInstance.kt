package at.yerova.arsascend.game

import at.yerova.arsascend.network.ServerNetworkHandler
import at.yerova.arsascend.network.ServerNetworkManager
import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.manager.ActorManager

class ServerGameInstance : BaseGameInstance() {
    val networkHandler = ServerNetworkHandler()
    val serverNetworkManager by lazy { ServerNetworkManager(networkHandler) }
    val serverGameplayManager by lazy { ServerGameplayManager() }
    val actorManager by lazy {
        ActorManager.newInstance(
            shouldPutFarAwayActorsToSleep = false,
            isLoggingEnabled = true,
            instanceNameForLogging = "ServerActors"
        )
    }

    val kubriko by lazy {
        Kubriko.newInstance(
            stateManager,
            actorManager,
            physicsManager,
            collisionManager,
            serverNetworkManager,
            serverGameplayManager
        )
    }

    fun dispose() {
        kubriko.dispose()
    }
}