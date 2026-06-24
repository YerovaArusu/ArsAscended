package at.yerova.arsascend.actors

import at.yerova.arsascend.data.DataTransferObject
import at.yerova.arsascend.data.Keys
import at.yerova.arsascend.network.EventSubscription
import at.yerova.arsascend.network.NetworkEventBus
import at.yerova.arsascend.network.SyncMessage
import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.actor.Actor
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

abstract class SyncingAnchor(var name: String = "Anchor") : Actor {
    private val subscriptions = mutableListOf<EventSubscription>()

    @OptIn(ExperimentalUuidApi::class)
    val entityId: String = Uuid.generateV7().toString()

    override fun onAdded(kubriko: Kubriko) {
        println("Added Anchor '$entityId'! Subscribing to Network ")

        val syncSub = NetworkEventBus.subscribe<SyncMessage> { message ->
            if (message.entityId == this.entityId) {
                applySyncUpdate(message.payload)
            }
        }

        subscriptions.add(syncSub)
    }

    override fun onRemoved() {
        println("Removed Anchor '$entityId'! Unsubscribed from Network!")
        subscriptions.forEach { it.unsubscribe() }
        subscriptions.clear()
    }

    fun extractSnapshot(): DataTransferObject {
        val dto = DataTransferObject(entityId)
            .set(Keys.ENTITY_NAME, name)

        createSync(dto)

        return dto
    }


    fun applySyncUpdate(dto: DataTransferObject) {
        if(dto.identifier != entityId) {
            println("WARN: Faulty Message for $entityId")
            return
        }

        if(dto.has(Keys.ENTITY_NAME)) {
            name = dto.get(Keys.ENTITY_NAME, "")
        }
        handleSync(dto)
    }

    protected abstract fun handleSync(dto: DataTransferObject)
    protected abstract fun createSync(dto: DataTransferObject)

}