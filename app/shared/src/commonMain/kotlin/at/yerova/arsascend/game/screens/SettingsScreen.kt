package at.yerova.arsascend.game.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var masterVolume by remember { mutableFloatStateOf(0.8f) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Einstellungen") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            //Example. Not sure if this shall be implemented.
            Text("Audio", style = MaterialTheme.typography.titleLarge)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Gesamtlautstärke")
            Slider(
                value = masterVolume,
                onValueChange = { masterVolume = it },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            
            // Hier ist Platz für weitere Einstellungen (Auflösung, Steuerung etc.)
        }
    }
}