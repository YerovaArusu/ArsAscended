package at.yerova.arsascend.game

import com.pandulapeter.kubriko.collision.CollisionManager
import com.pandulapeter.kubriko.manager.StateManager
import com.pandulapeter.kubriko.physics.PhysicsManager

open class BaseGameInstance {
    // 1. Core Logic Managers
    val stateManager by lazy {
        StateManager.newInstance(
            shouldAutoStart = true,
            isLoggingEnabled = true,
            instanceNameForLogging = "ServerState"
        )
    }

    // 2. Physics & Collision (Die "Wahrheit")
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
