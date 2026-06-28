package at.yerova.arsascend

import at.yerova.arsascend.actors.BaseEntity
import kotlin.reflect.KClass

sealed class GameEvent

data class EntityAddedEvent(val entity: BaseEntity) : GameEvent()
data class EntityRemovedEvent(val entity: BaseEntity) : GameEvent()

object LocalGameEventBus {
    val listeners = mutableMapOf<KClass<*>, MutableList<(Any) -> Unit>>()

    inline fun <reified T : GameEvent> subscribe(crossinline onEvent: (T) -> Unit) {
        val clazz = T::class
        val list = listeners.getOrPut(clazz) { mutableListOf() }
        list.add { event -> if (event is T) onEvent(event) }
    }

    fun publish(event: GameEvent) {
        listeners[event::class]?.forEach { it.invoke(event) }
    }
}