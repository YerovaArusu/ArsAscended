package at.yerova.arsascend.network

import at.yerova.arsascend.actors.BaseEntity
import at.yerova.arsascend.actors.PlayerEntity
import at.yerova.arsascend.actors.TestEntity
import at.yerova.arsascend.data.DataTransferObject
import at.yerova.arsascend.data.Keys
import at.yerova.arsascend.game.ClientGameInstance
import at.yerova.arsascend.game.initialAnchors
import at.yerova.arsascend.game.isInitialSyncComplete
import co.touchlab.kermit.Logger
import java.util.concurrent.ConcurrentLinkedQueue

class ClientNetworkHandler {
    object ClientMessageQueue {
        val pendingMessages = ConcurrentLinkedQueue<NetworkMessage>()
    }

    fun onMessageReceived(jsonString: String) {
        val message = NetworkParser.parse(jsonString) ?: return

        if (message.messageTarget == MessageTargetTypes.CLIENT_TO_SERVER) {
            Logger.w("WARN: Message wont be handled since its not a Client Message")
            return
        }

        if(message.senderId != "Networking") {
            Logger.w("Message wont be handled since Server is no longer connected!")
            return
        }

        if(message is InitialSyncMessage) {
            handleInitialSync(message)
            return
        }
        //TODO: If you want to catch messages that are not meant for the Server consider catching them here

        ClientMessageQueue.pendingMessages.add(message)
    }

    private fun handleInitialSync(message: InitialSyncMessage) {
        Logger.i("Receiving ${message.actorDTOs.size} Entities from Server for initial sync")

        initialAnchors = message.actorDTOs.mapNotNull { dto ->
            EntityFactory.createFromDto(dto)
        }

        Logger.i("Handled ${initialAnchors.size} Entities")

        isInitialSyncComplete.value = true
    }



    object EntityFactory {
        fun createFromDto(dto: DataTransferObject): BaseEntity? {
            return when (val type = dto.get(Keys.ENTITY_TYPE, "")) {
                PlayerEntity::class.simpleName -> {
                    val uuid = dto.get(Keys.PLAYER_UUID, "")
                    PlayerEntity(playerUUID = uuid, entityId = dto.identifier).apply {
                        applySyncUpdate(dto) // Setzt direkt die richtige Position!
                    }
                }

                TestEntity::class.simpleName -> {
                    TestEntity(entityId = dto.identifier).apply {
                        Logger.i("Adding test entity $entityId")
                        applySyncUpdate(dto)
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