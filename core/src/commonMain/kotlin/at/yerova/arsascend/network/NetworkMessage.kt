package at.yerova.arsascend.network

import at.yerova.arsascend.data.DataTransferObject
import kotlinx.serialization.Serializable
import kotlin.time.Clock

const val PLACEHOLDER_NETWORK_TARGET: String = "Networking"

@Serializable
enum class MessageTargetTypes {
    CLIENT_TO_SERVER,
    SERVER_TO_CLIENT,
    BI_DIRECTIONAL
}

@Serializable
sealed class NetworkMessage {

    // TODO Wer hat es gesendet? (Wird später durch echte IDs ersetzt)
    val senderId: String = PLACEHOLDER_NETWORK_TARGET
    val timestampMs: Long = Clock.System.now().toEpochMilliseconds()
    abstract val messageTarget: MessageTargetTypes
}

@Serializable
data class SyncMessage(
    val entityId: String,
    val payload: DataTransferObject,
    override val messageTarget: MessageTargetTypes = MessageTargetTypes.SERVER_TO_CLIENT
) : NetworkMessage()


@Serializable
data class PlayerCommandMessage(
    val playerId: String,
    val command: String,
    val parameters: DataTransferObject? = null,
    override val messageTarget: MessageTargetTypes = MessageTargetTypes.CLIENT_TO_SERVER
) : NetworkMessage()

@Serializable
data class InitialSyncMessage(
    val actorDTOs: List<DataTransferObject>,
    override val messageTarget: MessageTargetTypes = MessageTargetTypes.SERVER_TO_CLIENT
) : NetworkMessage()

@Serializable
data class SpawnEntityMessage(
    val entityDto: DataTransferObject,
    override val messageTarget: MessageTargetTypes = MessageTargetTypes.SERVER_TO_CLIENT
) : NetworkMessage()

@Serializable
data class DespawnEntityMessage(
    val entityId: String,
    override val messageTarget: MessageTargetTypes = MessageTargetTypes.SERVER_TO_CLIENT
) : NetworkMessage()