package at.yerova.arsascend.actors

import at.yerova.arsascend.EntityAddedEvent
import at.yerova.arsascend.EntityRemovedEvent
import at.yerova.arsascend.LocalGameEventBus
import at.yerova.arsascend.data.DataTransferObject
import at.yerova.arsascend.data.Keys
import at.yerova.arsascend.network.EventSubscription
import at.yerova.arsascend.network.NetworkEventBus
import at.yerova.arsascend.network.SyncMessage
import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.actor.Actor
import com.pandulapeter.kubriko.actor.body.BoxBody
import com.pandulapeter.kubriko.actor.traits.Visible
import com.pandulapeter.kubriko.helpers.extensions.rad
import com.pandulapeter.kubriko.helpers.extensions.sceneUnit
import com.pandulapeter.kubriko.types.SceneOffset
import com.pandulapeter.kubriko.types.SceneSize
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * This is pretty much the base instance all other game objects will inherit from.
 */
abstract class BaseEntity(
    open var name: String = "GameBase",
    @OptIn(ExperimentalUuidApi::class) open val entityId: String = Uuid.generateV7().toString(),
    override val body: BoxBody
) : Actor, Visible {
    private val subscriptions = mutableListOf<EventSubscription>()

    override fun onAdded(kubriko: Kubriko) {
        println("Added Anchor '$entityId'! Subscribing to Network ")

        val syncSub = NetworkEventBus.subscribe<SyncMessage> { message ->
            if (message.entityId == this.entityId) {
                applySyncUpdate(message.payload)
            }
        }

        subscriptions.add(syncSub)
        LocalGameEventBus.publish(EntityAddedEvent(this))
    }

    override fun onRemoved() {
        println("Removed Anchor '$entityId'! Unsubscribed from Network!")
        subscriptions.forEach { it.unsubscribe() }
        subscriptions.clear()
        LocalGameEventBus.publish(EntityRemovedEvent(this))
    }

    fun extractSnapshot(): DataTransferObject {
        val dto = DataTransferObject(entityId)
            .set(Keys.ENTITY_NAME, name).set(Keys.POS_X, body.position.x.raw)
            .set(Keys.POS_Y, body.position.y.raw).set(Keys.SIZE_X, body.size.width.raw)
            .set(Keys.SIZE_Y, body.size.height.raw).set(Keys.RADIANT, body.rotation.raw)
            .set(Keys.ENTITY_TYPE, this::class.simpleName ?: "Unknown")

        createSync(dto)
        return dto
    }


    fun applySyncUpdate(dto: DataTransferObject) {
        if (dto.identifier != entityId) {
            println("WARN: Faulty entityId for $entityId")
            return
        }

        body.position = SceneOffset(dto.get(Keys.POS_X, 0f).sceneUnit, dto.get(Keys.POS_Y, 0f).sceneUnit)
        body.size = SceneSize(dto.get(Keys.SIZE_X, 1f).sceneUnit, dto.get(Keys.SIZE_Y, 1f).sceneUnit)
        body.rotation = dto.get(Keys.RADIANT, 0f).rad

        name = dto.get(Keys.ENTITY_NAME, "")

        handleSync(dto)
    }

    protected abstract fun handleSync(dto: DataTransferObject)
    protected abstract fun createSync(dto: DataTransferObject)

}