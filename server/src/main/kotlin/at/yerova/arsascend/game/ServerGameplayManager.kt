package at.yerova.arsascend.game

import at.yerova.arsascend.actors.BaseEntity
import at.yerova.arsascend.actors.PlayerEntity
import at.yerova.arsascend.network.InitialSyncMessage
import at.yerova.arsascend.network.NetworkParser
import at.yerova.arsascend.network.ServerSessionManager
import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.manager.ActorManager
import com.pandulapeter.kubriko.manager.Manager
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class ServerGameplayManager : Manager() {

    val actorManager: ActorManager by manager<ActorManager>()

    private val logger = LoggerFactory.getLogger(ServerGameplayManager::class.java)

    override fun onInitialize(kubriko: Kubriko) {
        logger.info("Server Instance is Initializing!")
    }

    override fun onUpdate(deltaTimeInMilliseconds: Int) {

        val players =actorManager.activeDynamicActors.value.filterIsInstance<PlayerEntity>().map { it.playerUUID }
        ServerSessionManager.sessions.keys.filterNot { it in players }.forEach {
            logger.info("Adding Player with UUID=$it to the world")
            actorManager.add(PlayerEntity(playerUUID = it))

            //For every player who just logs in we will send a Message containing all current Actors
            CoroutineScope(Dispatchers.IO).launch {
                ServerSessionManager.sessions[it]?.send(NetworkParser.encode(createInitialSyncMessage()))
            }
        }

        // Gewinnbedingungen prüfen (z.B. Alle Spieler tot?)
        // Gegner-KI steuern
    }

    private fun createInitialSyncMessage(): InitialSyncMessage {
        return InitialSyncMessage(actorManager.allActors.value.filterIsInstance<BaseEntity>().map(BaseEntity::extractSnapshot))
    }
}
