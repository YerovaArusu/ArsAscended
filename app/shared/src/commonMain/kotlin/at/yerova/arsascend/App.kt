package at.yerova.arsascend

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.yerova.arsascend.game.ClientGameInstance
import at.yerova.arsascend.game.screens.*
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
@Preview
fun App() {
    MaterialTheme {
        // Der Controller steuert die gesamte App-Navigation
        val navController = rememberNavController()

        // Die Client-ID wird einmalig beim Start der App generiert
        val clientId = remember { UUID.randomUUID().toString() }

        // Wir erstellen EINE Game-Instance für die gesamte Lebensdauer der App
        val gameInstance = remember { ClientGameInstance() }

        // Scope für asynchrone Netzwerk-Aufrufe (Ktor) aus dem UI heraus
        val coroutineScope = rememberCoroutineScope()

        NavHost(
            navController = navController,
            startDestination = LoadingRoute // Wir starten beim Loading Screen
        ) {

            composable<LoadingRoute> {
                LoadingScreen(
                    onLoadingFinished = {
                        // Wir wechseln zum Main Menu...
                        navController.navigate(MainMenuRoute) {
                            // ... und werfen den Loading Screen aus der Back-History.
                            // So kann der User mit der "Zurück"-Taste am Handy nicht
                            // versehentlich wieder im Ladebildschirm landen!
                            popUpTo(LoadingRoute) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainMenuRoute> {
                MainMenuScreen(
                    onNavigateToConnect = { navController.navigate(ConnectRoute) },
                    onNavigateToSettings = { navController.navigate(SettingsRoute) }
                )
            }

            composable<ConnectRoute> {
                ConnectScreen(
                    onConnectRequest = { ip, playerName ->
                        // Coroutine starten, da Netzwerk-IO (connect) suspendiert!
                        coroutineScope.launch {
                            val success = gameInstance.gameNetworkClient.connect(
                                serverIp = ip,
                                playerName = playerName,
                                clientId = clientId
                            )

                            if (success) {
                                // Verbindung steht -> Ab ins Spiel!
                                navController.navigate(GameRoute)
                            } else {
                                // TODO: Hier später einen kleinen Toast/Snackbar Error anzeigen
                                println("Fehler: Konnte nicht verbinden!")
                            }
                        }
                    },
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable<SettingsRoute> {
                SettingsScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable<GameRoute> {
                // Hier läuft später dein Kubriko-Game
                GameScreen(gameInstance, clientId)
            }
        }
    }
}