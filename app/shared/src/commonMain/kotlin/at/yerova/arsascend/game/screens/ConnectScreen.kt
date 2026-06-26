package at.yerova.arsascend

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
fun ConnectScreen(
    onConnectRequest: (ip: String, playerName: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var serverIp by remember { mutableStateOf("127.0.0.1:8080") }
    var playerName by remember { mutableStateOf("Hero") }

    var isConnecting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Server verbinden") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text("Spielername") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = serverIp,
                onValueChange = { serverIp = it },
                label = { Text("Server IP & Port") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    isConnecting = true
                    onConnectRequest(serverIp, playerName)
                },
                enabled = !isConnecting, // Verhindert Spam-Klicks
                modifier = Modifier.height(50.dp).fillMaxWidth(0.8f)
            ) {
                if (isConnecting) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Verbindung herstellen")
                }
            }
        }
    }
}