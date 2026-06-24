package at.yerova.arsascend.game

import at.yerova.arsascend.network.ClientNetworkManager
import at.yerova.arsascend.network.ClientNetworkHandler
import at.yerova.arsascend.network.GameNetworkClient
import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.helpers.extensions.sceneUnit
import com.pandulapeter.kubriko.manager.ActorManager
import com.pandulapeter.kubriko.manager.ViewportManager
import com.pandulapeter.kubriko.shaders.ShaderManager
import com.pandulapeter.kubriko.sprites.SpriteManager

class ClientGameInstance : BaseGameInstance() {
    val actorManager by lazy { ActorManager.newInstance(shouldPutFarAwayActorsToSleep = true) }

    val spriteManager by lazy { SpriteManager.newInstance() }
    val shaderManager by lazy { ShaderManager.newInstance() }

    val viewportManager by lazy {
        ViewportManager.newInstance(
            aspectRatioMode = ViewportManager.AspectRatioMode.FitVertical(1080.sceneUnit),
            minimumScaleFactor = 0.5f,
            maximumScaleFactor = 2f
        )
    }

    val networkHandler = ClientNetworkHandler()
    val clientNetworkManager by lazy { ClientNetworkManager(networkHandler) }
    val clientGameplayManager by lazy { ClientGameplayManager() }

    val gameNetworkClient = GameNetworkClient(networkHandler)

    val kubriko by lazy {
        Kubriko.newInstance(
            stateManager,
            actorManager,
            physicsManager,
            collisionManager,
            spriteManager,
            shaderManager,
            viewportManager,
            clientNetworkManager,
            clientGameplayManager
        )
    }

    fun dispose() {
        gameNetworkClient.disconnect()
        kubriko.dispose()
    }
}