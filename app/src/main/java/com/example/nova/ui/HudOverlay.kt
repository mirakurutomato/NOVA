package com.example.nova

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nova.ui.theme.NovaEnterpriseBlue
// import com.example.nova.ui.theme.NovaWarningRed
import com.example.nova.ui.theme.glass

@Composable
fun HudOverlay() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar: Status
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.8f)) // Solid Monotone
                .padding(16.dp), // Increased padding
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "NOVA SYSTEMS // FPV", // Updated Text
                color = Color.White, // Monotone White
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusIndicator(active = true, label = stringResource(R.string.hud_system))
                Spacer(modifier = Modifier.width(8.dp))
                StatusIndicator(active = false, label = stringResource(R.string.hud_link))
            }
        }

        // Bottom Bar: Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp), // Lift up to avoid nav bar
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            // Enhance Toggle
            Button(
                onClick = { /* TODO Toggle Algorithm */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                modifier = Modifier
                    .border(1.dp, NovaEnterpriseBlue, RoundedCornerShape(50))
            ) {
                Text(stringResource(R.string.hud_enhance_on), color = NovaEnterpriseBlue)
            }

            Spacer(modifier = Modifier.width(32.dp))

            // Record Button
            FloatingActionButton(
                onClick = { /* TODO Record */ },
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Text(stringResource(R.string.hud_rec))
            }
            
            Spacer(modifier = Modifier.width(32.dp))

            // Settings
            IconButton(
                onClick = { /* TODO Settings */ },
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(50))
            ) {
                Text(stringResource(R.string.hud_set), color = Color.White)
            }
        }
    }
}

@Composable
fun StatusIndicator(active: Boolean, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(if (active) Color.Green else Color.Red, RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}
