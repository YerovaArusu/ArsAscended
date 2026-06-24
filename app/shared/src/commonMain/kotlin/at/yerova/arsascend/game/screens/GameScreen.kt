package at.yerova.arsascend.game.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import at.yerova.arsascend.game.ClientGameInstance
import at.yerova.arsascend.game.InputHandler
import at.yerova.arsascend.game.createMovementCommand
import com.pandulapeter.kubriko.KubrikoViewport

@Composable
fun GameScreen(gameInstance: ClientGameInstance, clientId: String) {

    val inputHandler = remember {
        InputHandler(
            onInputChanged = { x, y ->
                val command = createMovementCommand(
                    playerId = clientId,
                    x = x,
                    y = y
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