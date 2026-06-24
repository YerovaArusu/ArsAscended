package at.yerova.arsascend.game

import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.manager.Manager

class ServerGameplayManager : Manager() {
    
    override fun onInitialize(kubriko: Kubriko) {
        // Map laden (ohne Shader, nur Physik/Kollision!)
        println("Server lädt die unsichtbare Physik-Map...")
    }

    override fun onUpdate(deltaTimeInMilliseconds: Int) {
        // Gewinnbedingungen prüfen (z.B. Alle Spieler tot?)
        // Gegner-KI steuern
    }
}