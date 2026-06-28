package at.yerova.arsascend.network

import at.yerova.arsascend.EntityAddedEvent
import at.yerova.arsascend.EntityRemovedEvent
import at.yerova.arsascend.LocalGameEventBus
import at.yerova.arsascend.actors.BaseEntity
import at.yerova.arsascend.game.ServerGameplayManager
import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.manager.ActorManager
import com.pandulapeter.kubriko.manager.Manager
import io.ktor.websocket.Frame
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class ServerNetworkManager(val networkHandler: ServerNetworkHandler) : Manager() {
    private val actorManager by manager<ActorManager>()

    private val logger = LoggerFactory.getLogger(ServerGameplayManager::class.java)


    override fun onInitialize(kubriko: Kubriko) {
        LocalGameEventBus.subscribe<EntityAddedEvent> { event ->
            logger.info("Server bemerkt Spawn von: ${event.entity.entityId}. Sende Broadcast!")

            val spawnMsg = SpawnEntityMessage(event.entity.extractSnapshot())
            val json = NetworkParser.encode(spawnMsg)

            CoroutineScope(Dispatchers.IO).launch {
                ServerSessionManager.sessions.values.forEach { session ->
                    session.send(json)
                }
            }
        }

        LocalGameEventBus.subscribe<EntityRemovedEvent> { event ->
            logger.info("Server bemerkt Despawn von: ${event.entity.entityId}. Sende Broadcast!")

            val despawnMsg = DespawnEntityMessage(event.entity.entityId)
            val json = NetworkParser.encode(despawnMsg)

            CoroutineScope(Dispatchers.IO).launch {
                ServerSessionManager.sessions.values.forEach { session ->
                    session.send(json)
                }
            }
        }

    }

    override fun onUpdate(deltaTimeInMilliseconds: Int) {
        val activeSessions = ServerSessionManager.sessions.values
        if (activeSessions.isEmpty()) return

        actorManager.allActors.value.filterIsInstance<BaseEntity>().forEach {
            val dto = it.extractSnapshot()
            val syncMsg = SyncMessage(entityId = it.entityId, payload = dto)
            val jsonString = NetworkParser.encode(syncMsg)

            activeSessions.forEach { session ->
                scope.launch {
                    try {
                        session.send(Frame.Text(jsonString))
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }
}