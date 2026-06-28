package at.yerova.arsascend.actors

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import at.yerova.arsascend.data.DataTransferObject
import com.pandulapeter.kubriko.actor.body.BoxBody
import com.pandulapeter.kubriko.helpers.extensions.sceneUnit
import com.pandulapeter.kubriko.types.SceneOffset
import com.pandulapeter.kubriko.types.SceneSize
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TestEntity(
    override var name: String = "Test Entity",
    override val body: BoxBody = BoxBody(initialSize = SceneSize(64.sceneUnit, 64.sceneUnit)),
    @OptIn(ExperimentalUuidApi::class) override val entityId: String = Uuid.generateV7().toString()
) : DynamicEntity(entityId = entityId, body = body, name = name) {
    override fun handleSync(dto: DataTransferObject) {
    }

    override fun createSync(dto: DataTransferObject) {
    }

    override fun DrawScope.draw() {
        drawRect(
            color = Color.Blue,
            size = Size(body.size.width.raw, body.size.height.raw)
        )
    }

    override fun update(deltaTimeInMilliseconds: Int) {
    }
}