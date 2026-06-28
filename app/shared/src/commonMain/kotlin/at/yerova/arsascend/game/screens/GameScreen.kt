package at.yerova.arsascend.game.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import at.yerova.arsascend.data.DataTransferObject
import at.yerova.arsascend.data.Keys
import at.yerova.arsascend.game.ClientGameInstance
import at.yerova.arsascend.game.InputHandler
import at.yerova.arsascend.game.createMovementCommand
import at.yerova.arsascend.network.PlayerCommandMessage
import com.pandulapeter.kubriko.KubrikoViewport

@Composable
fun GameScreen(gameInstance: ClientGameInstance, clientId: String) {

    val inputHandler = remember {
        InputHandler(
            onInputChanged = { x, y ->
                val command = createMovementCommand(clientId, x, y)
                gameInstance.gameNetworkClient.sendMessage(command)
            },
            onLookChanged = { screenX, screenY ->

                val screenWidth = gameInstance.viewportManager.size.value.width // Oder Window-Größe
                val isLookingRight = screenX > (screenWidth / 2)

            },
            onActionTriggered = { actionType, screenX, screenY ->
                val camPos = gameInstance.viewportManager.cameraPosition.value
                val windowSize = gameInstance.viewportManager.size.value

                // Berechnung der World-Koordinate:
                // Welt-Position = Kamera-Zentrum + (Maus-Position - Halbe Bildschirmgröße)
                val worldX = camPos.x.raw + (screenX - (windowSize.width / 2f))
                val worldY = camPos.y.raw + (screenY - (windowSize.height / 2f))

                val payload = DataTransferObject("action_payload")
                    .set(Keys.TARGET_X, worldX)
                    .set(Keys.TARGET_Y, worldY)

                val command = PlayerCommandMessage(
                    playerId = clientId,
                    command = actionType, // Z.B. "SKILL_POISON_POOL"
                    parameters = payload
                )
                gameInstance.gameNetworkClient.sendMessage(command)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        KubrikoViewport(kubriko = gameInstance.kubriko)
        inputHandler.RenderOverlay()
    }
}