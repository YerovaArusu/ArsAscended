package at.yerova.arsascend.game

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

actual class InputHandler actual constructor(
    private val onInputChanged: (x: Float, y: Float) -> Unit
) {
    private var lastSentX = 0f
    private var lastSentY = 0f

    @Composable
    actual fun RenderOverlay() {
        Box(modifier = Modifier.fillMaxSize()) {

            // Unten links mit etwas Abstand zum Rand
            JoyStick(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(32.dp),
                size = 140.dp,
                onMove = { x, y ->
                    if (abs(x - lastSentX) > 0.05f || abs(y - lastSentY) > 0.05f) {
                        lastSentX = x
                        lastSentY = y
                        onInputChanged(x, y)
                    }
                },
                onRelease = {
                    lastSentX = 0f
                    lastSentY = 0f
                    onInputChanged(0f, 0f)
                }
            )
        }
    }
}


@Composable
fun JoyStick(
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    baseColor: Color = Color.DarkGray.copy(alpha = 0.5f),
    thumbColor: Color = Color.LightGray.copy(alpha = 0.8f),
    onMove: (x: Float, y: Float) -> Unit,
    onRelease: () -> Unit
) {
    var thumbPosition by remember { mutableStateOf(Offset.Zero) }
    var center by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = modifier
            .size(size)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { _ ->
                        // Optional: Du könntest hier den Stick genau unter den Finger springen lassen
                    },
                    onDragEnd = {
                        thumbPosition = center
                        onRelease()
                    },
                    onDragCancel = {
                        thumbPosition = center
                        onRelease()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newPosition = thumbPosition + dragAmount

                        val deltaX = newPosition.x - center.x
                        val deltaY = newPosition.y - center.y
                        val distance = hypot(deltaX, deltaY)

                        val maxRadius = size.toPx() / 2f

                        if (distance <= maxRadius) {
                            thumbPosition = newPosition
                        } else {
                            val angle = atan2(deltaY, deltaX)
                            thumbPosition = center + Offset(
                                x = cos(angle) * maxRadius,
                                y = sin(angle) * maxRadius
                            )
                        }

                        val normalizedX = (thumbPosition.x - center.x) / maxRadius
                        val normalizedY = (thumbPosition.y - center.y) / maxRadius

                        onMove(normalizedX, normalizedY)
                    }
                )
            }
    ) {
        val canvasCenter = Offset(size.toPx() / 2f, size.toPx() / 2f)
        if (center == Offset.Zero) {
            center = canvasCenter
            thumbPosition = canvasCenter
        }

        val maxRadius = size.toPx() / 2f
        val thumbRadius = maxRadius / 3f

        drawCircle(
            color = baseColor,
            radius = maxRadius,
            center = center
        )

        drawCircle(
            color = thumbColor,
            radius = thumbRadius,
            center = thumbPosition
        )
    }
}