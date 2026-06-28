package at.yerova.arsascend.game

import com.pandulapeter.kubriko.collision.CollisionManager
import com.pandulapeter.kubriko.helpers.TickSource
import com.pandulapeter.kubriko.manager.StateManager
import com.pandulapeter.kubriko.physics.PhysicsManager
import com.pandulapeter.kubriko.types.SceneOffset

open class BaseGameInstance {
    /** 20 Ticks = 50ms per Tick (Just like the Minecraft-Standard)
     *
     * NOTE: This is only for the engine. Not for the Graphical stuff.
     */
    val engineTickSource = TickSource.fixedFrequency(20)

    val stateManager by lazy {
        StateManager.newInstance(
            shouldAutoStart = true,
            isLoggingEnabled = true,
            instanceNameForLogging = "ServerState"
        )
    }

    val physicsManager by lazy {
        PhysicsManager.newInstance(
            initialSimulationSpeed = 1f,
            initialGravity = SceneOffset.Zero,
            isLoggingEnabled = true,
            instanceNameForLogging = "ServerPhysics"
        )
    }

    val collisionManager by lazy {
        CollisionManager.newInstance(
            isLoggingEnabled = true,
            instanceNameForLogging = "ServerCollision"
        )
    }
}
