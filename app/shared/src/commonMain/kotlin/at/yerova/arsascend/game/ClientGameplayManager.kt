package at.yerova.arsascend.game

import at.yerova.arsascend.actors.BaseEntity
import at.yerova.arsascend.actors.PlayerEntity
import at.yerova.arsascend.data.DataTransferObject
import at.yerova.arsascend.data.Keys
import at.yerova.arsascend.network.EventSubscription
import at.yerova.arsascend.network.InitialSyncMessage
import at.yerova.arsascend.network.NetworkEventBus
import co.touchlab.kermit.Logger
import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.manager.ActorManager
import com.pandulapeter.kubriko.manager.Manager
import com.pandulapeter.kubriko.manager.ViewportManager

class ClientGameplayManager(private val clientId: String) : Manager() {

    private val actorManager by manager<ActorManager>()
    private val viewportManager by manager<ViewportManager>()
    private var syncSubscription: EventSubscription? = null

    override fun onInitialize(kubriko: Kubriko) {
        Logger.i("Client World is loading!")

        syncSubscription = NetworkEventBus.subscribe<InitialSyncMessage> { message ->
            handleInitialSync(message)
        }
    }

    override fun onUpdate(deltaTimeInMilliseconds: Int) {
        val myPlayer = actorManager.allActors.value
            .filterIsInstance<PlayerEntity>()
            .find { it.playerUUID == clientId }

        if (myPlayer != null) {
            viewportManager.setCameraPosition(myPlayer.body.position)
        }
    }

    override fun onDispose() {
        syncSubscription?.unsubscribe()
    }
    private fun handleInitialSync(message: InitialSyncMessage) {
        Logger.i("Empfange Initial-Sync vom Server! ${message.actorDTOs.size} Entities.")

        // 2. Erzeuge alle Entities aus den DTOs
        val newEntities = message.actorDTOs.mapNotNull { dto ->
            EntityFactory.createFromDto(dto, clientId)
        }

        // 3. Wenn wir schon Actors haben (z.B. nach einem Reconnect),
        // sollten wir die alten vielleicht erst löschen
        actorManager.removeAll()

        // 4. Füge die neuen Entities alle auf einmal zur Engine hinzu
        actorManager.add(newEntities)

        Logger.i("Initial-Sync abgeschlossen. Welt aufgebaut.")
    }



    object EntityFactory {
        fun createFromDto(dto: DataTransferObject, clientUuid: String): BaseEntity? {
            val type = dto.get(Keys.ENTITY_TYPE, "")

            return when (type) {
                "PlayerEntity" -> {
                    val uuid = dto.get(Keys.PLAYER_UUID, "")
                    PlayerEntity(playerUUID = uuid, entityId = dto.identifier).apply {
                        applySyncUpdate(dto) // Setzt direkt die richtige Position!
                    }
                }

                else -> {
                    Logger.e("Unknown Entity: $type")
                    null
                }
            }
        }
    }
}