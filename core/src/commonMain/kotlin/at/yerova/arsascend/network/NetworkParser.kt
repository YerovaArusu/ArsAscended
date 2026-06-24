package at.yerova.arsascend.network

import kotlinx.serialization.json.Json

object NetworkParser {
    val jsonFormat = Json { 
        ignoreUnknownKeys = true // Verhindert Abstürze, wenn neue Felder dazukommen
        classDiscriminator = "type" // Der unsichtbare Header aus der Sealed Class
    }

    fun parse(jsonString: String): NetworkMessage? {
        return try {
            jsonFormat.decodeFromString<NetworkMessage>(jsonString)
        } catch (e: Exception) {
            println("ERROR: Konnte Netzwerk-Paket nicht parsen: ${e.message}")
            null
        }
    }

    fun encode(message: NetworkMessage): String {
        return jsonFormat.encodeToString(message)
    }
}