package at.yerova.arsascend.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import at.yerova.arsascend.actors.BaseEntity
import at.yerova.arsascend.actors.PlayerEntity
import at.yerova.arsascend.actors.TestEntity
import at.yerova.arsascend.data.Keys
import at.yerova.arsascend.network.*
import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.helpers.extensions.sceneUnit
import com.pandulapeter.kubriko.manager.ActorManager
import com.pandulapeter.kubriko.manager.Manager
import com.pandulapeter.kubriko.types.SceneOffset
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class ServerGameplayManager : Manager() {

    val actorManager: ActorManager by manager<ActorManager>()

    private val logger = LoggerFactory.getLogger(ServerGameplayManager::class.java)

    override fun onInitialize(kubriko: Kubriko) {
        logger.info("Server Instance is Initializing!")

        actorManager.add(TestEntity().apply { body.position = SceneOffset((-30).sceneUnit,80.sceneUnit) })
    }

    override fun onUpdate(deltaTimeInMilliseconds: Int) {

        val players = actorManager.activeDynamicActors.value.filterIsInstance<PlayerEntity>().map { it.playerUUID }
        ServerSessionManager.sessions.keys.filterNot { it in players }.forEach {
            logger.info("Adding Player with UUID=$it to the world")
            actorManager.add(PlayerEntity(playerUUID = it))

            //For every player who just logs in we will send a Message containing all current Actors
            CoroutineScope(Dispatchers.IO).launch {
                ServerSessionManager.sessions[it]?.send(NetworkParser.encode(createInitialSyncMessage()))
            }
        }

        processClientInputs()
        // Gewinnbedingungen prüfen (z.B. Alle Spieler tot?)
        // Gegner-KI steuern
    }

    private fun processClientInputs() {
        while (true) {
            val message = ServerNetworkHandler.ServerInputQueue.commands.poll() ?: break

            when (message) {
                is PlayerCommandMessage -> handlePlayerInput(message)
                else -> {
                    logger.warn("Unknown command message $message")
                }
            }
        }
    }

    private fun handlePlayerInput(message: PlayerCommandMessage) {

        val player = actorManager.activeDynamicActors.value
            .filterIsInstance<PlayerEntity>()
            .find { it.playerUUID == message.playerId } ?: return

        if (message.command == "MOVE") {
            val parameters = message.parameters ?: return

            val moveX = parameters.get(Keys.MOVEMENT_X, 0f)
            val moveY = parameters.get(Keys.MOVEMENT_Y, 0f)

            val speed = 100f

            player.physicsBody.velocity = SceneOffset(
                x = (moveX.times(speed)).sceneUnit,
                y = (moveY.times(speed)).sceneUnit
            )
        }

    }

    private fun createInitialSyncMessage(): InitialSyncMessage {
        return InitialSyncMessage(
            actorManager.allActors.value.filterIsInstance<BaseEntity>().map(BaseEntity::extractSnapshot)
        )
    }
}
