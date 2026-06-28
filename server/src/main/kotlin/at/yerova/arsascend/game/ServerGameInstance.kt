package at.yerova.arsascend.game

import at.yerova.arsascend.network.ServerNetworkHandler
import at.yerova.arsascend.network.ServerNetworkManager
import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.manager.ActorManager
import org.slf4j.LoggerFactory

//TODO: Integrate some way of loading the map.
class ServerGameInstance : BaseGameInstance() {
    private val logger = LoggerFactory.getLogger(ServerGameInstance::class.java)

    val networkHandler = ServerNetworkHandler()
    val serverNetworkManager by lazy { ServerNetworkManager(networkHandler) }
    val serverGameplayManager by lazy { ServerGameplayManager() }
    val actorManager by lazy {
        ActorManager.newInstance(
            // initialActors = ..., TODO: from a Save-File we will load all the objects and load them as initially.
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
            serverGameplayManager,
            tickSource = engineTickSource
        )
    }

    fun start() {
        logger.info("Starting Server Game-Instance")
        kubriko
        engineTickSource.start()
    }

    fun dispose() {
        kubriko.dispose()
        engineTickSource.stop()
    }
}