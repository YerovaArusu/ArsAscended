package at.yerova.arsascend.actors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import at.yerova.arsascend.data.DataTransferObject
import at.yerova.arsascend.data.Keys
import com.pandulapeter.kubriko.actor.body.BoxBody
import com.pandulapeter.kubriko.collision.mask.BoxCollisionMask
import com.pandulapeter.kubriko.helpers.extensions.sceneUnit
import com.pandulapeter.kubriko.physics.PhysicsBody
import com.pandulapeter.kubriko.physics.RigidBody
import com.pandulapeter.kubriko.types.SceneSize
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * @param name The Player Name that is specified. Player Name will always be changed for now.
 * @param playerUUID The client UUID. One Client = One Player.
 */
class PlayerEntity(
    var playerUUID : String,
    override var name: String = "Player",
    override val body: BoxBody = BoxBody(initialSize = SceneSize(64.sceneUnit, 64.sceneUnit)),
    @OptIn(ExperimentalUuidApi::class) override val entityId: String = Uuid.generateV7().toString()
) : DynamicEntity(name = name,body,entityId = entityId), RigidBody{

    override val collisionMask = BoxCollisionMask(
        initialPosition = body.position,
        initialSize = body.size
    )
    override val physicsBody = PhysicsBody(
        collisionMask = collisionMask,
    )


    override fun DrawScope.draw() {
        drawRect(
            color = Color.Red,
            size = androidx.compose.ui.geometry.Size(body.size.width.raw, body.size.height.raw)
        )
    }

    override fun update(deltaTimeInMilliseconds: Int) {
        // Hier kommt später die Logik rein:
        // Wenn (isServer) -> Wende Bewegungskommandos auf physicsBody an
        // Wenn (isClient) -> Interpoliere weich zur letzten bekannten Server-Position

        // WICHTIG: Damit Sichtbarkeit und Physik synchron bleiben!
        // (Kubriko macht das in manchen Versionen automatisch, aber sicher ist sicher)
        body.position = collisionMask.position
    }

    override fun handleSync(dto: DataTransferObject) {
        playerUUID = dto.get(Keys.PLAYER_UUID,playerUUID)
        collisionMask.position = body.position
    }

    override fun createSync(dto: DataTransferObject) {
        dto[Keys.PLAYER_UUID] = playerUUID
    }
}
