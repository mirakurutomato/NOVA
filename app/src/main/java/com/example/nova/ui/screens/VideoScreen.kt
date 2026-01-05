package com.example.nova.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nova.HudOverlay

@Composable
fun VideoScreen(
    videoRendererComposable: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Native OpenGL Surface
        videoRendererComposable()

        // 2. HUD Overlay
        HudOverlay()
    }
}
