package at.yerova.arsascend.data

import kotlinx.serialization.Serializable

/**
 * @param identifier a name that needs to be specified for the DTO
 */
@Serializable
class DataTransferObject(val identifier: String) {
    var data: HashMap<String, String> = HashMap()

    fun <T> set(key: GameKey<T>, value: T, overrideExisting: Boolean = false) :DataTransferObject {
        if (!data.containsKey(key.id) || overrideExisting) {
            data[key.id] = key.serialize(value)
        }
        return this
    }

    operator fun <T> set(key: GameKey<T>, value: T) : DataTransferObject {
        set(key, value, overrideExisting = true)
        return this
    }

    fun remove(key: GameKey<*>) : DataTransferObject{
        data.remove(key.id)
        return this
    }

    fun <T> get(key: GameKey<T>, defaultValue: T): T {
        val rawValue = data[key.id] ?: return defaultValue
        return try {
            key.deserialize(rawValue)
        } catch (e: Exception) {
            println("WARN: Could not parse '${key.id}' from value '$rawValue'")
            defaultValue
        }
    }

    fun <T> getOrNull(key: GameKey<T>): T? {
        val rawValue = data[key.id] ?: return null
        return try {
            key.deserialize(rawValue)
        } catch (e: Exception) {
            println("WARN: Could not parse '${key.id}' from value '$rawValue'")
            null
        }
    }


    fun has(key: GameKey<*>): Boolean {
        return data.containsKey(key.id)
    }

    fun clear() :DataTransferObject{
        data.clear()
        return this
    }

    val isEmpty: Boolean get() = data.isEmpty()
    val isNotEmpty: Boolean get() = data.isNotEmpty()
    val size: Int get() = data.size

    fun mergeWith(other: DataTransferObject, overrideExisting: Boolean = true) :DataTransferObject{
        for ((keyString, valueString) in other.data) {
            if (!data.containsKey(keyString) || overrideExisting) {
                data[keyString] = valueString
            }
        }
        return this
    }


    override fun toString(): String {
        if (data.isEmpty()) return "DTO(id='$identifier') [Empty]"

        val formattedData = data.entries.joinToString(separator = " | ") { (k, v) ->
            "$k: $v"
        }
        return "DTO(id='$identifier') { $formattedData }"
    }
}