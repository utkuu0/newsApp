package com.example.technews.presentation.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.technews.ui.theme.GradientEnd
import com.example.technews.ui.theme.GradientMiddle
import com.example.technews.ui.theme.GradientStart

@Composable
fun SettingsScreen(onBackClick: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
        val state by viewModel.state.collectAsState()
        val context = LocalContext.current

        var hasNotificationPermission by remember {
                mutableStateOf(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                        } else {
                                true
                        }
                )
        }

        val permissionLauncher =
                rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                        hasNotificationPermission = isGranted
                        if (isGranted) {
                                viewModel.setNotificationsEnabled(true)
                        }
                }

        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                // Premium Header
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .background(
                                                Brush.verticalGradient(
                                                        colors =
                                                                listOf(
                                                                        GradientStart,
                                                                        GradientMiddle
                                                                )
                                                )
                                        )
                                        .statusBarsPadding()
                                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                IconButton(
                                        onClick = onBackClick,
                                        modifier =
                                                Modifier.size(44.dp)
                                                        .background(
                                                                Color.White.copy(alpha = 0.2f),
                                                                CircleShape
                                                        )
                                ) {
                                        Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Geri",
                                                tint = Color.White
                                        )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                        Text(
                                                text = "Ayarlar",
                                                style =
                                                        MaterialTheme.typography.headlineMedium
                                                                .copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                        )
                                        Text(
                                                text = "Uygulamayı özelleştirin",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.8f)
                                        )
                                }
                        }
                }

                // Settings Content
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                        // Appearance Section
                        SettingsSection(title = "Görünüm", icon = Icons.Default.Palette) {
                                PremiumToggleItem(
                                        icon =
                                                if (state.isDarkMode) Icons.Default.DarkMode
                                                else Icons.Default.LightMode,
                                        iconTint =
                                                if (state.isDarkMode) GradientMiddle
                                                else GradientStart,
                                        title = "Karanlık Mod",
                                        description =
                                                if (state.isDarkMode) "Karanlık tema aktif"
                                                else "Açık tema aktif",
                                        isChecked = state.isDarkMode,
                                        onCheckedChange = { viewModel.setDarkMode(it) }
                                )
                        }

                        // Notifications Section
                        SettingsSection(title = "Bildirimler", icon = Icons.Default.Notifications) {
                                PremiumToggleItem(
                                        icon =
                                                if (state.isNotificationsEnabled &&
                                                                hasNotificationPermission
                                                )
                                                        Icons.Default.Notifications
                                                else Icons.Default.NotificationsOff,
                                        iconTint =
                                                if (state.isNotificationsEnabled &&
                                                                hasNotificationPermission
                                                )
                                                        GradientStart
                                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                        title = "Günlük Bildirimler",
                                        description = "Uygulamayı açmadığınızda hatırlatma alın",
                                        isChecked =
                                                state.isNotificationsEnabled &&
                                                        hasNotificationPermission,
                                        onCheckedChange = { enabled ->
                                                if (enabled &&
                                                                !hasNotificationPermission &&
                                                                Build.VERSION.SDK_INT >=
                                                                        Build.VERSION_CODES.TIRAMISU
                                                ) {
                                                        permissionLauncher.launch(
                                                                Manifest.permission
                                                                        .POST_NOTIFICATIONS
                                                        )
                                                } else {
                                                        viewModel.setNotificationsEnabled(enabled)
                                                }
                                        }
                                )
                        }

                        // About Section
                        SettingsSection(title = "Hakkında", icon = Icons.Default.Info) {
                                AboutItem(
                                        icon = Icons.Default.Code,
                                        title = "Teknoloji Haberleri",
                                        subtitle = "Versiyon 1.1.0"
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                AboutItem(
                                        icon = Icons.Default.Favorite,
                                        title = "Sevgiyle yapıldı",
                                        subtitle = "© 2024 TechNews"
                                )
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                }
        }
}

@Composable
private fun SettingsSection(title: String, icon: ImageVector, content: @Composable () -> Unit) {
        Column {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                ) {
                        Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = GradientStart,
                                modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = title,
                                style =
                                        MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                        ),
                                color = MaterialTheme.colorScheme.onBackground
                        )
                }

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) { Column(modifier = Modifier.padding(8.dp)) { content() } }
        }
}

@Composable
private fun PremiumToggleItem(
        icon: ImageVector,
        iconTint: Color,
        title: String,
        description: String,
        isChecked: Boolean,
        onCheckedChange: (Boolean) -> Unit
) {
        val scale by
                animateFloatAsState(
                        targetValue = if (isChecked) 1f else 0.95f,
                        animationSpec = tween(200),
                        label = "scale"
                )

        val backgroundColor by
                animateColorAsState(
                        targetValue =
                                if (isChecked) GradientStart.copy(alpha = 0.1f)
                                else Color.Transparent,
                        animationSpec = tween(300),
                        label = "backgroundColor"
                )

        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(backgroundColor)
                                .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Box(
                        modifier =
                                Modifier.size(48.dp)
                                        .scale(scale)
                                        .background(
                                                if (isChecked)
                                                        Brush.linearGradient(
                                                                colors =
                                                                        listOf(
                                                                                GradientStart.copy(
                                                                                        alpha = 0.2f
                                                                                ),
                                                                                GradientMiddle.copy(
                                                                                        alpha = 0.2f
                                                                                )
                                                                        )
                                                        )
                                                else
                                                        Brush.linearGradient(
                                                                colors =
                                                                        listOf(
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surfaceVariant,
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surfaceVariant
                                                                        )
                                                        ),
                                                RoundedCornerShape(14.dp)
                                        ),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = iconTint
                        )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = title,
                                style =
                                        MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Medium
                                        ),
                                color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }

                Switch(
                        checked = isChecked,
                        onCheckedChange = onCheckedChange,
                        colors =
                                SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = GradientStart,
                                        checkedBorderColor = GradientStart,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color(0xFF4B5563),
                                        uncheckedBorderColor = Color(0xFF6B7280)
                                )
                )
        }
}

@Composable
private fun AboutItem(icon: ImageVector, title: String, subtitle: String) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Box(
                        modifier =
                                Modifier.size(48.dp)
                                        .background(
                                                Brush.linearGradient(
                                                        colors =
                                                                listOf(
                                                                        GradientStart.copy(
                                                                                alpha = 0.1f
                                                                        ),
                                                                        GradientEnd.copy(
                                                                                alpha = 0.1f
                                                                        )
                                                                )
                                                ),
                                                RoundedCornerShape(14.dp)
                                        ),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = GradientStart
                        )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                        Text(
                                text = title,
                                style =
                                        MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Medium
                                        ),
                                color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }
        }
}
