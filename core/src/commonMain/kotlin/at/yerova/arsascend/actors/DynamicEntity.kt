package at.yerova.arsascend.actors

import com.pandulapeter.kubriko.actor.body.BoxBody
import com.pandulapeter.kubriko.actor.traits.Dynamic
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

abstract class DynamicEntity(override var name: String = "Dynamic Object", override val body: BoxBody,
                             @OptIn(ExperimentalUuidApi::class) override val entityId: String = Uuid.generateV7().toString()) :
    BaseEntity(name = name, body = body, entityId = entityId), Dynamic {
}