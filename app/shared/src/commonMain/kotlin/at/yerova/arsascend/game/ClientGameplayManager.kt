package at.yerova.arsascend.game

import at.yerova.arsascend.actors.BaseEntity
import at.yerova.arsascend.actors.PlayerEntity
import at.yerova.arsascend.actors.TestEntity
import at.yerova.arsascend.data.DataTransferObject
import at.yerova.arsascend.data.Keys
import at.yerova.arsascend.network.ClientNetworkHandler
import at.yerova.arsascend.network.DespawnEntityMessage
import at.yerova.arsascend.network.EventSubscription
import at.yerova.arsascend.network.InitialSyncMessage
import at.yerova.arsascend.network.NetworkEventBus
import at.yerova.arsascend.network.SpawnEntityMessage
import co.touchlab.kermit.Logger
import co.touchlab.kermit.loggerConfigInit
import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.manager.ActorManager
import com.pandulapeter.kubriko.manager.Manager
import com.pandulapeter.kubriko.manager.ViewportManager

class ClientGameplayManager(private val clientId: String) : Manager() {

    private val actorManager by manager<ActorManager>()
    private val viewportManager by manager<ViewportManager>()

    private val subscribedMessages = mutableListOf<EventSubscription>()

    override fun onInitialize(kubriko: Kubriko) {
        Logger.i("Client World is loading!")

        subscribedMessages.add(NetworkEventBus.subscribe<SpawnEntityMessage> { message ->
            Logger.i("Ein neues Entity ist gespawnt: ${message.entityDto.identifier}")

            val newEntity = ClientNetworkHandler.EntityFactory.createFromDto(message.entityDto)
            if (newEntity != null) {
                actorManager.add(newEntity)
            }
        })

        subscribedMessages.add(NetworkEventBus.subscribe<DespawnEntityMessage> { message ->
            Logger.i("Entity despawnt: ${message.entityId}")

            // Finde das Entity und lösche es aus dem Client
            val entityToRemove = actorManager.allActors.value
                .filterIsInstance<BaseEntity>()
                .find { it.entityId == message.entityId }

            if (entityToRemove != null) {
                actorManager.remove(entityToRemove)
            }
        })
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
        for (subscription in subscribedMessages) {
            subscription.unsubscribe()
        }
    }

}