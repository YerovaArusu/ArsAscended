package at.yerova.arsascend.game

import at.yerova.arsascend.actors.BaseEntity
import at.yerova.arsascend.network.ClientNetworkHandler
import at.yerova.arsascend.network.ClientNetworkManager
import at.yerova.arsascend.network.GameNetworkClient
import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.helpers.extensions.sceneUnit
import com.pandulapeter.kubriko.manager.ActorManager
import com.pandulapeter.kubriko.manager.ViewportManager
import com.pandulapeter.kubriko.shaders.ShaderManager
import com.pandulapeter.kubriko.sprites.SpriteManager
import com.pandulapeter.kubriko.types.TargetFrameRate
import kotlinx.coroutines.flow.MutableStateFlow

val isInitialSyncComplete = MutableStateFlow(false)
var initialAnchors: List<BaseEntity> = emptyList()

class ClientGameInstance(private val clientId: String) : BaseGameInstance() {


    val actorManager by lazy {
        ActorManager.newInstance(
            initialActors = initialAnchors, shouldPutFarAwayActorsToSleep = true
        )
    }

    val spriteManager by lazy { SpriteManager.newInstance() }
    val shaderManager by lazy { ShaderManager.newInstance() }

    val viewportManager by lazy {
        ViewportManager.newInstance(
            aspectRatioMode = ViewportManager.AspectRatioMode.FitVertical(1080.sceneUnit),
            minimumScaleFactor = 0.5f,
            maximumScaleFactor = 2f,
            initialTargetFrameRate = TargetFrameRate.Limit(60) //Are 60 FPS for rendering Sufficient?
        )
    }

    val networkHandler = ClientNetworkHandler()
    val clientNetworkManager by lazy { ClientNetworkManager(networkHandler) }

    val clientGameplayManager by lazy { ClientGameplayManager(clientId) }

    val gameNetworkClient = GameNetworkClient(networkHandler)

    val kubriko by lazy {
        Kubriko.newInstance(
            stateManager,
            clientNetworkManager,
            actorManager,
            physicsManager,
            collisionManager,
            clientGameplayManager,
            viewportManager,
            spriteManager,
            shaderManager,
            tickSource = engineTickSource
        )
    }


    fun start() {
        kubriko
        engineTickSource.start()
    }

    fun dispose() {
        gameNetworkClient.disconnect()
        kubriko.dispose()
        engineTickSource.stop()
    }
}