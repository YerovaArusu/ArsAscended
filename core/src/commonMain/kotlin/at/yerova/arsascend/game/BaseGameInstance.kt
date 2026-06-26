package at.yerova.arsascend.game

import com.pandulapeter.kubriko.collision.CollisionManager
import com.pandulapeter.kubriko.manager.StateManager
import com.pandulapeter.kubriko.physics.PhysicsManager

open class BaseGameInstance {
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
