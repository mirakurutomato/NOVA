package com.example.nova.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext // Added
import androidx.appcompat.app.AppCompatDelegate // Added
import com.example.nova.R
import com.example.nova.ui.theme.glass

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("nova_settings", android.content.Context.MODE_PRIVATE)
    // var isJapanese by remember { mutableStateOf(false) } // Removed, using AppCompat directly
    // var isMetric by remember { mutableStateOf(prefs.getBoolean("metric", true)) } // Removed, removed from UI

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
    ) {
        Text(
            stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Setting
            item { 
                SettingItem(
                    title = stringResource(R.string.pref_language),
                    value = if (AppCompatDelegate.getApplicationLocales().toLanguageTags().contains("ja")) "日本語" else "English",
                    onClick = { 
                        val currentLocales = AppCompatDelegate.getApplicationLocales()
                        val newLocale = if (currentLocales.toLanguageTags().contains("ja")) "en" else "ja"
                        val localeList = androidx.core.os.LocaleListCompat.forLanguageTags(newLocale)
                        AppCompatDelegate.setApplicationLocales(localeList)
                    }
                ) 
            }
            
            // Unit Setting (Removed)
            // item { ... }
            
            item { Divider(color = Color.Gray.copy(alpha = 0.2f)) }
            
            item { SettingItem(stringResource(R.string.pref_version), "1.0.0") }
            
            // Privacy Policy
            item {
                Text(
                    stringResource(R.string.pref_privacy),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glass()
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.privacy_content),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // Copyright Footer (Updated)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "© 2026 奥河 董馬（Toma Okugawa）. All Rights Reserved.",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SettingItem(title: String, value: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glass() // Use Glass item
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}
