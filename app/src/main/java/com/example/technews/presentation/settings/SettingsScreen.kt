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
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.technews.ui.theme.GradientStart

@Composable
fun SettingsScreen(
        categoryColor: Color = GradientStart,
        onLanguageChange: (String) -> Unit = {},
        viewModel: SettingsViewModel = hiltViewModel()
) {
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
                                                                        categoryColor,
                                                                        categoryColor.copy(alpha = 0.8f)
                                                                )
                                                )
                                        )
                                        .statusBarsPadding()
                                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
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

                // Settings Content
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                        // Appearance Section
                        SettingsSection(title = "Görünüm", icon = Icons.Default.Palette, accentColor = categoryColor) {
                                PremiumToggleItem(
                                        icon =
                                                if (state.isDarkMode) Icons.Default.DarkMode
                                                else Icons.Default.LightMode,
                                        iconTint =
                                                if (state.isDarkMode) categoryColor.copy(alpha = 0.7f)
                                                else categoryColor,
                                        title = "Karanlık Mod",
                                        description =
                                                if (state.isDarkMode) "Karanlık tema aktif"
                                                else "Açık tema aktif",
                                        isChecked = state.isDarkMode,
                                        onCheckedChange = { viewModel.setDarkMode(it) },
                                        accentColor = categoryColor
                                )
                        }

                        // Language Section
                        SettingsSection(title = "Dil", icon = Icons.Default.Language, accentColor = categoryColor) {
                                LanguageItem(
                                        currentLanguage = state.appLanguage,
                                        accentColor = categoryColor,
                                        onLanguageSelect = { language ->
                                                viewModel.setAppLanguage(language)
                                                onLanguageChange(language)
                                        }
                                )
                        }

                        // Notifications Section
                        SettingsSection(title = "Bildirimler", icon = Icons.Default.Notifications, accentColor = categoryColor) {
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
                                                        categoryColor
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
                                        },
                                        accentColor = categoryColor
                                )
                        }

                        // About Section
                        SettingsSection(title = "Hakkında", icon = Icons.Default.Info, accentColor = categoryColor) {
                                AboutItem(
                                        icon = Icons.Default.Code,
                                        title = "Teknoloji Haberleri",
                                        subtitle = "Versiyon 1.3",
                                        accentColor = categoryColor
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                AboutItem(
                                        icon = Icons.Default.Favorite,
                                        title = "Sevgiyle yapıldı",
                                        subtitle = "© 2024 TechNews",
                                        accentColor = categoryColor
                                )
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                }
        }
}

@Composable
private fun SettingsSection(title: String, icon: ImageVector, accentColor: Color, content: @Composable () -> Unit) {
        Column {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                ) {
                        Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = accentColor,
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
        onCheckedChange: (Boolean) -> Unit,
        accentColor: Color
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
                                if (isChecked) accentColor.copy(alpha = 0.1f)
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
                                                                                accentColor.copy(
                                                                                        alpha = 0.2f
                                                                                ),
                                                                                accentColor.copy(
                                                                                        alpha = 0.15f
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
                                        checkedTrackColor = accentColor,
                                        checkedBorderColor = accentColor,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color(0xFF4B5563),
                                        uncheckedBorderColor = Color(0xFF6B7280)
                                )
                )
        }
}

@Composable
private fun AboutItem(icon: ImageVector, title: String, subtitle: String, accentColor: Color) {
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
                                                                        accentColor.copy(
                                                                                alpha = 0.1f
                                                                        ),
                                                                        accentColor.copy(
                                                                                alpha = 0.05f
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
                                tint = accentColor
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

@Composable
private fun LanguageItem(
        currentLanguage: String,
        accentColor: Color,
        onLanguageSelect: (String) -> Unit
) {
        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
                LanguageSelectionDialog(
                        currentLanguage = currentLanguage,
                        accentColor = accentColor,
                        onLanguageSelect = { language ->
                                onLanguageSelect(language)
                                showDialog = false
                        },
                        onDismiss = { showDialog = false }
                )
        }

        Row(
                modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showDialog = true }
                        .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Box(
                        modifier = Modifier.size(48.dp)
                                .background(
                                        Brush.linearGradient(
                                                colors = listOf(
                                                        accentColor.copy(alpha = 0.2f),
                                                        accentColor.copy(alpha = 0.15f)
                                                )
                                        ),
                                        RoundedCornerShape(14.dp)
                                ),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = accentColor
                        )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = "Uygulama Dili",
                                style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = "Dili değiştirmek için tıklayın",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }

                Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                                containerColor = accentColor.copy(alpha = 0.15f)
                        )
                ) {
                        Text(
                                text = getLanguageDisplayName(currentLanguage),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                ),
                                color = accentColor
                        )
                }
        }
}

@Composable
private fun LanguageSelectionDialog(
        currentLanguage: String,
        accentColor: Color,
        onLanguageSelect: (String) -> Unit,
        onDismiss: () -> Unit
) {
        val languages = listOf(
                "system" to "Sistem Dili",
                "tr" to "Türkçe",
                "en" to "English"
        )

        AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                        Text(
                                text = "Dil Seçin",
                                style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                )
                        )
                },
                text = {
                        Column {
                                languages.forEach { (code, name) ->
                                        val isSelected = currentLanguage == code
                                        Row(
                                                modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(
                                                                if (isSelected) accentColor.copy(alpha = 0.1f)
                                                                else Color.Transparent
                                                        )
                                                        .clickable { onLanguageSelect(code) }
                                                        .padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                RadioButton(
                                                        selected = isSelected,
                                                        onClick = { onLanguageSelect(code) },
                                                        colors = RadioButtonDefaults.colors(
                                                                selectedColor = accentColor,
                                                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                        text = name,
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                                        ),
                                                        color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface
                                                )
                                        }
                                }
                        }
                },
                confirmButton = {
                        TextButton(onClick = onDismiss) {
                                Text("Kapat", color = accentColor)
                        }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
        )
}

private fun getLanguageDisplayName(languageCode: String): String {
        return when (languageCode) {
                "tr" -> "Türkçe"
                "en" -> "English"
                "system" -> "Sistem"
                else -> languageCode
        }
}

