package com.app.quran.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quran.ui.theme.PrimaryGreen
import com.app.quran.ui.theme.SecondaryGold
import com.app.quran.ui.theme.ThemeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val autoPlayEnabled by viewModel.autoPlayEnabled.collectAsState()
    val shuffleMode by viewModel.shuffleMode.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val bluetoothAutoDetect by viewModel.bluetoothAutoDetect.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()
    val wifiOnlyDownload by viewModel.wifiOnlyDownload.collectAsState()
    val autoResume by viewModel.autoResume.collectAsState()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showCarKeywordsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = PrimaryGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "الإعدادات",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // Playback Settings Section
        SettingsSection(title = "إعدادات التشغيل") {
            SettingsSwitchItem(
                icon = Icons.Default.PlayCircle,
                title = "تشغيل تلقائي",
                description = "بدء التشغيل عند اتصال البلوثوث",
                checked = autoPlayEnabled,
                onCheckedChange = { viewModel.toggleAutoPlay() }
            )

            SettingsSwitchItem(
                icon = Icons.Default.Shuffle,
                title = "وضع عشوائي",
                description = "تشغيل السور بشكل عشوائي",
                checked = shuffleMode,
                onCheckedChange = { viewModel.toggleShuffleMode() }
            )

            SettingsSwitchItem(
                icon = Icons.Default.Repeat,
                title = "تكرار",
                description = "تكرار السورة الحالية",
                checked = repeatMode,
                onCheckedChange = { viewModel.toggleRepeatMode() }
            )

            SettingsSliderItem(
                icon = Icons.Default.VolumeUp,
                title = "مستوى الصوت",
                value = volume,
                onValueChange = { viewModel.updateVolume(it) }
            )
        }

        // Bluetooth Settings Section
        SettingsSection(title = "إعدادات البلوثوث") {
            SettingsSwitchItem(
                icon = Icons.Default.Bluetooth,
                title = "اكتشاف تلقائي",
                description = "بدء التشغيل عند اكتشاف جهاز سيارة",
                checked = bluetoothAutoDetect,
                onCheckedChange = { viewModel.toggleBluetoothAutoDetect() }
            )

            SettingsSwitchItem(
                icon = Icons.Default.Refresh,
                title = "استئناف تلقائي",
                description = "الاستمرار من آخر موضع عند إعادة الاتصال",
                checked = autoResume,
                onCheckedChange = { viewModel.toggleAutoResume() }
            )

            SettingsClickItem(
                icon = Icons.Default.Search,
                title = "كلمات مفتاحية للسيارات",
                description = "إدارة كلمات البحث لجهاز السيارة",
                onClick = { showCarKeywordsDialog = true }
            )
        }

        // Download Settings Section
        SettingsSection(title = "إعدادات التنزيل") {
            SettingsSwitchItem(
                icon = Icons.Default.Wifi,
                title = "تنزيل عبر WiFi فقط",
                description = "عدم استخدام البيانات المحمولة",
                checked = wifiOnlyDownload,
                onCheckedChange = { viewModel.toggleWifiOnlyDownload() }
            )

            SettingsClickItem(
                icon = Icons.Default.Download,
                title = "إدارة التنزيلات",
                description = "تحميل أو حذف ملفات الصوت",
                onClick = { /* TODO: Open download manager */ }
            )
        }

        // Theme Settings Section
        SettingsSection(title = "المظهر") {
            SettingsClickItem(
                icon = Icons.Default.Palette,
                title = "السمة",
                description = when (currentTheme) {
                    ThemeType.LIGHT -> "فاتح (كريمي)"
                    ThemeType.DARK -> "داكن"
                    ThemeType.ISLAMIC_GREEN -> "أخضر إسلامي"
                    ThemeType.ROYAL_GOLD -> "ذهبي ملكي"
                },
                onClick = { showThemeDialog = true }
            )
        }

        // About Section
        SettingsSection(title = "حول التطبيق") {
            SettingsInfoItem(
                icon = Icons.Default.Info,
                title = "الإصدار",
                value = "1.0.0"
            )

            SettingsInfoItem(
                icon = Icons.Default.Folder,
                title = "المصدر",
                value = "الشيخ مصطفى الفرجاني"
            )

            SettingsInfoItem(
                icon = Icons.Default.LibraryBooks,
                title = "عدد السور",
                value = "114 سورة"
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            onDismiss = { showThemeDialog = false },
            onSelectTheme = { theme ->
                viewModel.setTheme(theme)
                showThemeDialog = false
            }
        )
    }

    // Car Keywords Dialog
    if (showCarKeywordsDialog) {
        CarKeywordsDialog(
            currentKeywords = viewModel.carKeywords,
            onDismiss = { showCarKeywordsDialog = false },
            onSaveKeywords = { keywords ->
                viewModel.updateCarKeywords(keywords)
                showCarKeywordsDialog = false
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = PrimaryGreen,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                content = content
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = PrimaryGreen,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsSliderItem(
    icon: ImageVector,
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.padding(start = 40.dp),
            colors = SliderDefaults.colors(
                thumbColor = SecondaryGold,
                activeTrackColor = PrimaryGreen,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: ThemeType,
    onDismiss: () -> Unit,
    onSelectTheme: (ThemeType) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Palette, contentDescription = null, tint = PrimaryGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("اختر السمة")
            }
        },
        text = {
            Column {
                ThemeType.entries.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectTheme(theme) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentTheme == theme,
                            onClick = { onSelectTheme(theme) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = when (theme) {
                                    ThemeType.LIGHT -> "فاتح (كريمي)"
                                    ThemeType.DARK -> "داكن"
                                    ThemeType.ISLAMIC_GREEN -> "أخضر إسلامي"
                                    ThemeType.ROYAL_GOLD -> "ذهبي ملكي"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = when (theme) {
                                    ThemeType.LIGHT -> "ألوان مريحة للعين"
                                    ThemeType.DARK -> "للاستخدام الليلي"
                                    ThemeType.ISLAMIC_GREEN -> "تصميم إسلامي أصيل"
                                    ThemeType.ROYAL_GOLD -> "أناقة ملكية"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun CarKeywordsDialog(
    currentKeywords: Set<String>,
    onDismiss: () -> Unit,
    onSaveKeywords: (Set<String>) -> Unit
) {
    var keywordsText by remember { mutableStateOf(currentKeywords.joinToString("\n")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("كلمات مفتاحية للسيارات")
            }
        },
        text = {
            Column {
                Text(
                    text = "أدخل الكلمات المفتاحية للكشف عن أجهزة السيارة (سطر لكل كلمة):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = keywordsText,
                    onValueChange = { keywordsText = it },
                    modifier = Modifier.height(200.dp),
                    maxLines = 10
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val keywords = keywordsText
                        .split("\n")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .toSet()
                    onSaveKeywords(keywords)
                }
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}