package at.yerova.arsascend.network

import at.yerova.arsascend.actors.BaseEntity
import com.pandulapeter.kubriko.manager.ActorManager
import com.pandulapeter.kubriko.manager.Manager
import io.ktor.websocket.Frame
import kotlinx.coroutines.launch

class ServerNetworkManager(val networkHandler: ServerNetworkHandler) : Manager() {
    private val actorManager by manager<ActorManager>()

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