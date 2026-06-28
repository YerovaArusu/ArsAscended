package at.yerova.arsascend.game

import androidx.compose.runtime.Composable
import at.yerova.arsascend.network.PlayerCommandMessage
import at.yerova.arsascend.data.DataTransferObject
import at.yerova.arsascend.data.Keys

expect class InputHandler(
    onInputChanged: (x: Float, y: Float) -> Unit,
    onLookChanged: (aimX: Float, aimY: Float) -> Unit,
    onActionTriggered: (actionType: String, screenX: Float, screenY: Float) -> Unit
) {
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