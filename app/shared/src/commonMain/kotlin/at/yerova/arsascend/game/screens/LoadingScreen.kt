package at.yerova.arsascend.game.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

// Importiere hier deine Ressourcen
import arsascend.app.shared.generated.resources.Res
import arsascend.app.shared.generated.resources.* // Deine Bilder
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LoadingScreen(onLoadingFinished: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(3000.milliseconds)
        onLoadingFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(Res.drawable.yerova_logo),
            contentDescription = "Developer Yerova's Logo",
            modifier = Modifier
                .align(Alignment.Center)
                .size(250.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Powered by",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                painter = painterResource(Res.drawable.kubriko_logo),
                contentDescription = "Kubriko Engine Logo",
                modifier = Modifier.height(40.dp)
            )
        }
    }
}