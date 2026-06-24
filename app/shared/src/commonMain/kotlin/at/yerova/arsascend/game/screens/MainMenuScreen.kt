package at.yerova.arsascend

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenuScreen(
    onNavigateToConnect: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Spieltitel
            Text(
                text = "Ars Ascend",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Buttons
            Button(
                onClick = onNavigateToConnect,
                modifier = Modifier.width(200.dp).height(50.dp)
            ) {
                Text("Spielen", fontSize = 18.sp)
            }

            OutlinedButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.width(200.dp).height(50.dp)
            ) {
                Text("Einstellungen", fontSize = 18.sp)
            }

            // Optional: Ein "Beenden"-Button macht auf Desktop Sinn,
            // auf Android übernimmt das die System-Zurück-Taste.
        }
    }
}