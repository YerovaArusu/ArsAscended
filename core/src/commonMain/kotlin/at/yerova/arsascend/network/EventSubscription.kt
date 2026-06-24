package at.yerova.arsascend.network

import kotlin.reflect.KClass

class EventSubscription(private val unsubscribeAction: () -> Unit) {
    fun unsubscribe() = unsubscribeAction()
}

object NetworkEventBus {
    val listeners = mutableMapOf<KClass<*>, MutableList<(Any) -> Unit>>()

    /**
     * Abonniert einen bestimmten Message-Typ.
     * Beispiel: NetworkEventBus.subscribe<SyncMessage> { msg -> ... }
     */
    inline fun <reified T : NetworkMessage> subscribe(crossinline onEvent: (T) -> Unit): EventSubscription {
        val clazz = T::class
        val list = listeners.getOrPut(clazz) { mutableListOf() }

        val wrapper: (Any) -> Unit = { event ->
            if (event is T) {
                onEvent(event)
            }
        }

        list.add(wrapper)

        return EventSubscription {
            list.remove(wrapper)
        }
    }

    /**
     * Feuert eine Nachricht an alle Subscriber dieses Typs ab.
     */
    fun publish(message: NetworkMessage) {
        val clazz = message::class

        listeners[clazz]?.toList()?.forEach { listener ->
            listener.invoke(message)
        }
    }
}