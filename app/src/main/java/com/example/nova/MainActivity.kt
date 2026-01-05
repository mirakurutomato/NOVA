package com.example.nova

import android.Manifest
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.widget.Toast
// import android.widget.Toast // Duplicate removed
// import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.Settings
// import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.navigation.compose.rememberNavController
import com.example.nova.ui.navigation.BottomNavigationBar
import com.example.nova.ui.navigation.NavGraph
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.nova.ui.VideoRenderer
import com.example.nova.ui.theme.NovaTheme
// import com.example.nova.ui.theme.NovaAccentCyan // Legacy
import com.example.nova.ui.theme.NovaBlack
import com.example.nova.ui.theme.NovaEnterpriseBlue
// import com.example.nova.ui.theme.NovaWarningRed // Legacy

import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var videoRenderer: VideoRenderer

    // Native methods
    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                // Permissions granted
            } else {
                Toast.makeText(this, "Permissions required for FPV", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Fullscreen Immersive Mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        videoRenderer = VideoRenderer(this) { surfaceTexture ->
             // TODO: Pass surfaceTexture to Native/USB Camera (libuvc/camera2)
             // For now, this callback indicates GL context is ready.
        }

        setContent {
            NovaTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        videoRendererComposable = {
                            AndroidView(
                                factory = { context ->
                                    GLSurfaceView(context).apply {
                                        setEGLContextClientVersion(3)
                                        setRenderer(videoRenderer)
                                        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    )
                }
            }
        }

        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }
    }
}
