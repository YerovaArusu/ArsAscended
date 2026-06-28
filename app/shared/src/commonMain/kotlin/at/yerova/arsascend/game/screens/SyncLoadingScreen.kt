package at.yerova.arsascend.game.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import at.yerova.arsascend.game.isInitialSyncComplete
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun SyncLoadingScreen(
    onSyncFinished: () -> Unit,
    onTimeout: () -> Unit // Z.B. um zurück ins Hauptmenü zu springen
) {

    val isSynced by isInitialSyncComplete.collectAsState()

    var hasTimedOut by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(10.seconds)

        if (!isInitialSyncComplete.value) {
            hasTimedOut = true
        }
    }

    LaunchedEffect(isSynced) {
        if (isSynced) {
            onSyncFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            if (hasTimedOut) {
                Text(
                    text = "Connection to the Server has timed out!",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onTimeout) {
                    Text("Back to the menu")
                }
            } else {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading Server world",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}