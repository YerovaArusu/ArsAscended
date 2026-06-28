package at.yerova.arsascend.game

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent

actual class InputHandler actual constructor(
    private val onInputChanged: (x: Float, y: Float) -> Unit,
    private val onLookChanged: (aimX: Float, aimY: Float) -> Unit,
    private val onActionTriggered: (actionType: String, screenX: Float, screenY: Float) -> Unit
) {
    private var currentX = 0f
    private var currentY = 0f

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    actual fun RenderOverlay() {val focusRequester = remember { FocusRequester() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { keyEvent ->
                    handleKeyEvent(keyEvent)
                }
                .onPointerEvent(PointerEventType.Move) { pointerEvent ->
                    val position = pointerEvent.changes.first().position
                    onLookChanged(position.x, position.y)
                }
                .onPointerEvent(PointerEventType.Press) { pointerEvent ->
                    val change = pointerEvent.changes.first()
                    val position = change.position

                    if (pointerEvent.buttons.isPrimaryPressed) {
                        onActionTriggered("SKILL_POISON_POOL", position.x, position.y)
                    }
                }
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    private fun handleKeyEvent(event: KeyEvent): Boolean {
        var newX = currentX
        var newY = currentY

        val isPressed = event.type == KeyEventType.KeyDown

        when (event.key) {
            Key.W, Key.DirectionUp -> newY = if (isPressed) -1f else 0f
            Key.S, Key.DirectionDown -> newY = if (isPressed) 1f else 0f
            Key.A, Key.DirectionLeft -> newX = if (isPressed) -1f else 0f
            Key.D, Key.DirectionRight -> newX = if (isPressed) 1f else 0f
            else -> return false // Taste ignoriert
        }

        if (newX != currentX || newY != currentY) {
            currentX = newX
            currentY = newY
            onInputChanged(currentX, currentY)
        }

        return true
    }
}