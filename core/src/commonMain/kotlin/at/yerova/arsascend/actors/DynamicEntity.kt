package at.yerova.arsascend.actors

import androidx.compose.ui.graphics.drawscope.DrawScope
import at.yerova.arsascend.data.DataTransferObject
import com.pandulapeter.kubriko.actor.body.BoxBody
import com.pandulapeter.kubriko.actor.traits.Dynamic
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

open class DynamicEntity(override var name: String = "Dynamic Object", override val body: BoxBody,
                             @OptIn(ExperimentalUuidApi::class) override val entityId: String = Uuid.generateV7().toString()) :
    BaseEntity(name = name, body = body, entityId = entityId), Dynamic {
    override fun handleSync(dto: DataTransferObject) {
        TODO("Not yet implemented")
    }

    override fun createSync(dto: DataTransferObject) {
        TODO("Not yet implemented")
    }

    override fun DrawScope.draw() {
        TODO("Not yet implemented")
    }

    override fun update(deltaTimeInMilliseconds: Int) {
        TODO("Not yet implemented")
    }


}