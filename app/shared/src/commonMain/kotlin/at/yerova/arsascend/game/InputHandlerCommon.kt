package at.yerova.arsascend.game

import androidx.compose.runtime.Composable
import at.yerova.arsascend.network.PlayerCommandMessage
import at.yerova.arsascend.data.DataTransferObject
import at.yerova.arsascend.data.Keys

expect class InputHandler(
    onInputChanged: (x: Float, y: Float) -> Unit
) {
    /**
     * Das Compose-Overlay, das über dem Kubriko-Canvas gerendert wird.
     */
    @Composable
    fun RenderOverlay()
}

fun createMovementCommand(playerId: String, x: Float, y: Float): PlayerCommandMessage {
    val payload = DataTransferObject("movement_update")
        .set(Keys.MOVEMENT_X, x)
        .set(Keys.MOVEMENT_Y, y)

    return PlayerCommandMessage(
        playerId = playerId,
        command = "MOVE",
        parameters = payload
    )
}