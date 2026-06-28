package at.yerova.arsascend

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.yerova.arsascend.game.ClientGameInstance
import at.yerova.arsascend.game.screens.*
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        val clientId = remember { UUID.randomUUID().toString() }

        val gameInstance = remember { ClientGameInstance(clientId) }

        val coroutineScope = rememberCoroutineScope()

        NavHost(
            navController = navController,
            startDestination = StartUpScreen // Wir starten beim Loading Screen
        ) {

            composable<StartUpScreen> {
                StartUpScreen(
                    onLoadingFinished = {
                        navController.navigate(MainMenuRoute) {
                            popUpTo(StartUpScreen) { inclusive = true }
                        }
                    }
                )
            }

            composable<SyncLoadingScreen> {
                SyncLoadingScreen(
                    onSyncFinished = {
                        navController.navigate(GameRoute) {
                            popUpTo(SyncLoadingScreen) { inclusive = true }
                        }
                    },
                    onTimeout = {
                        gameInstance.gameNetworkClient.disconnect()
                        navController.navigateUp()
                    }
                )
            }

            composable<GameRoute> {
                LaunchedEffect(Unit) {
                    gameInstance.start()
                }

                GameScreen(gameInstance, clientId)
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
                        coroutineScope.launch {
                            val success = gameInstance.gameNetworkClient.connect(
                                serverIp = ip,
                                playerName = playerName,
                                clientId = clientId
                            )

                            if (success) {
                                Logger.i("Connected to ${ip}:${playerName}. Going into $ConnectRoute")
                                navController.navigate(SyncLoadingScreen)
                            } else {
                                Logger.e("Couldn't Connect to ${ip}: $playerName")
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
                LaunchedEffect(Unit) {
                    gameInstance.start()
                }
                GameScreen(gameInstance, clientId)
            }
        }
    }
}