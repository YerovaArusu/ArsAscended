package at.yerova.arsascend.network

import kotlinx.serialization.json.Json

object NetworkParser {

    val jsonFormat = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "type"
    }

    fun parse(jsonString: String): NetworkMessage? {
        return try {
            jsonFormat.decodeFromString<NetworkMessage>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    fun encode(message: NetworkMessage): String {
        return jsonFormat.encodeToString(message)
    }
}