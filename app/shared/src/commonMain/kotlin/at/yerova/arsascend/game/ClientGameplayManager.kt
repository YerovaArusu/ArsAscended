package at.yerova.arsascend.game

import com.pandulapeter.kubriko.Kubriko
import com.pandulapeter.kubriko.manager.Manager

class ClientGameplayManager : Manager() {

    override fun onInitialize(kubriko: Kubriko) {
        println("Client lädt die hübsche Map...")
    }

    override fun onUpdate(deltaTimeInMilliseconds: Int) {
        // Kamera auf den lokalen Spieler zentrieren!
        // Viewport-Alpha für Fade-Ins berechnen (wie bei Annoyed Penguins)
        // Partikel-Effekte updaten
    }
}