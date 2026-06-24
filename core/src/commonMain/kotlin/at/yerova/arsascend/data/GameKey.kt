package at.yerova.arsascend.data


object Keys {
    val ENTITY_NAME = StringKey("entity_name")
    val CURRENT_HP = IntegerKey("current_hp")
    val IS_ALIVE = BooleanKey("is_alive")

    val MOVEMENT_X = FloatKey("movement_x")
    val MOVEMENT_Y = FloatKey("movement_y")
}


/**
 * The base key
 * @param id the literal String key for this Key.
 * @param serialize
 * @param deserialize
 */
open class GameKey<T>(
    val id: String,
    val serialize: (T) -> String,
    val deserialize: (String) -> T
)

class StringKey(id: String) : GameKey<String>(id, serialize = { it }, deserialize = { it })

class IntegerKey(id: String) : GameKey<Int>(id, serialize = { it.toString() }, deserialize = { it.toInt() })

class BooleanKey(id: String) : GameKey<Boolean>(id, serialize = { it.toString() }, deserialize = { it.toBooleanStrict() })

class FloatKey(id: String) : GameKey<Float>(id, serialize = { it.toString() }, deserialize = { it.toFloat() })

class StringListKey(id: String) : GameKey<List<String>>(
    id = id,
    serialize = { it.joinToString(",") },
    deserialize = { if (it.isEmpty()) emptyList() else it.split(",") }
)