package com.app.quran.presentation.player

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quran.service.bluetooth.BluetoothHelper
import com.app.quran.ui.theme.PrimaryGreen
import com.app.quran.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val playerState by viewModel.playerState.collectAsState()
    val currentReader by viewModel.currentReader.collectAsState()
    val shuffleMode by viewModel.shuffleMode.collectAsState()
    val sleepTimerState by viewModel.sleepTimerState.collectAsState()
    val repeatAyahState by viewModel.repeatAyahState.collectAsState()

    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showRepeatDialog by remember { mutableStateOf(false) }

    // Animation for playing state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (playerState.isPlaying) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bluetooth Status Card
        BluetoothStatusCard()

        Spacer(modifier = Modifier.height(24.dp))

        // Player Card
        AnimatedContent(
            targetState = playerState.currentSurah,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) togetherWith
                        fadeOut(animationSpec = tween(400))
            },
            label = "surah_animation"
        ) { surah ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Cover Art / Icon with animation
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(if (playerState.isPlaying) scale else 1f)
                            .clip(CircleShape)
                            .background(PrimaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Quran",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reader Name
                    Text(
                        text = currentReader?.name ?: "الشيخ مصطفى الفرجاني",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Surah Name
                    Text(
                        text = surah?.nameArabic ?: "اختر سورة",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = surah?.name ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sleep Timer indicator
                    if (sleepTimerState.isActive) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = PrimaryGreen
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${sleepTimerState.remainingMinutes} دقيقة متبقية",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimaryGreen
                            )
                        }
                    }

                    // Repeat Ayah indicator
                    if (repeatAyahState.isEnabled) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .background(
                                    SecondaryGold.copy(alpha = 0.2f),
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = SecondaryGold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (repeatAyahState.repeatCount == -1) "تكرار ∞"
                                       else "تكرار ${repeatAyahState.currentCount}/${repeatAyahState.repeatCount}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryGold
                            )
                        }
                    }

                    // Progress Bar
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Slider(
                            value = if (playerState.duration > 0) {
                                playerState.currentPositionMs.toFloat() / playerState.duration.toFloat()
                            } else 0f,
                            onValueChange = { value ->
                                val newPosition = (value * playerState.duration).toLong()
                                viewModel.seekTo(newPosition)
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = SecondaryGold,
                                activeTrackColor = PrimaryGreen,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatDuration(playerState.currentPositionMs),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatDuration(playerState.duration),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Control Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Shuffle Button
                        IconButton(
                            onClick = { viewModel.toggleShuffle() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = if (shuffleMode) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Previous Button
                        IconButton(
                            onClick = { viewModel.skipToPrevious() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous",
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Play/Pause Button
                        FloatingActionButton(
                            onClick = {
                                if (playerState.isPlaying) {
                                    viewModel.pause()
                                } else {
                                    if (playerState.currentSurah == null) {
                                        viewModel.playRandomSurah()
                                    } else {
                                        viewModel.play()
                                    }
                                }
                            },
                            containerColor = PrimaryGreen,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        // Next Button
                        IconButton(
                            onClick = { viewModel.skipToNext() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next",
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Repeat Button (for Ayah)
                        IconButton(
                            onClick = { showRepeatDialog = true },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "Repeat Ayah",
                                tint = if (repeatAyahState.isEnabled) SecondaryGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Secondary controls row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Sleep Timer Button
                        OutlinedButton(
                            onClick = { showSleepTimerDialog = true },
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (sleepTimerState.isActive) "${sleepTimerState.remainingMinutes}m"
                                       else "مؤقت"
                            )
                        }

                        // Add Bookmark Button
                        OutlinedButton(
                            onClick = {
                                playerState.currentSurah?.let {
                                    viewModel.addBookmark(it.id, playerState.currentPositionMs)
                                }
                            },
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("علامة")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = { viewModel.playRandomSurah() },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Icon(Icons.Default.Shuffle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تشغيل عشوائي")
            }

            OutlinedButton(
                onClick = { /* TODO: Open library */ },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Icon(Icons.Default.LibraryMusic, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("المكتبة")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Loading indicator
        AnimatedVisibility(
            visible = playerState.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CircularProgressIndicator(
                color = PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Sleep Timer Dialog
    if (showSleepTimerDialog) {
        SleepTimerDialog(
            currentState = sleepTimerState,
            onDismiss = { showSleepTimerDialog = false },
            onSetTimer = { minutes ->
                if (minutes > 0) {
                    viewModel.startSleepTimer(minutes)
                } else {
                    viewModel.cancelSleepTimer()
                }
                showSleepTimerDialog = false
            }
        )
    }

    // Repeat Ayah Dialog
    if (showRepeatDialog) {
        RepeatAyahDialog(
            currentState = repeatAyahState,
            currentPosition = playerState.currentPositionMs,
            onDismiss = { showRepeatDialog = false },
            onEnableRepeat = { repeatCount ->
                viewModel.enableRepeatAyah(repeatCount)
                showRepeatDialog = false
            },
            onDisableRepeat = {
                viewModel.disableRepeatAyah()
                showRepeatDialog = false
            }
        )
    }
}

@Composable
fun BluetoothStatusCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = "Bluetooth",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "بلوثوث السيارة",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "جاهز للتشغيل التلقائي",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Connected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SleepTimerDialog(
    currentState: com.app.quran.service.audio.SleepTimerState,
    onDismiss: () -> Unit,
    onSetTimer: (Int) -> Unit
) {
    var selectedMinutes by remember { mutableIntStateOf(15) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = PrimaryGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("مؤقت النوم")
            }
        },
        text = {
            Column {
                listOf(15, 30, 45, 60, 90, 0).forEach { minutes ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = if (minutes == 0) !currentState.isActive else selectedMinutes == minutes,
                            onClick = { selectedMinutes = minutes }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (minutes == 0) "بدون نهاية" else "$minutes دقيقة"
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSetTimer(selectedMinutes) }) {
                Text("تطبيق")
            }
        },
        dismissButton = {
            Row {
                if (currentState.isActive) {
                    TextButton(onClick = { onSetTimer(0) }) {
                        Text("إلغاء المؤقت", color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("إلغاء")
                }
            }
        }
    )
}

@Composable
fun RepeatAyahDialog(
    currentState: com.app.quran.service.audio.RepeatAyahState,
    currentPosition: Long,
    onDismiss: () -> Unit,
    onEnableRepeat: (Int) -> Unit,
    onDisableRepeat: () -> Unit
) {
    var selectedCount by remember { mutableIntStateOf(3) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = null,
                    tint = SecondaryGold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("تكرار آية")
            }
        },
        text = {
            Column {
                Text(
                    text = "تكرار من الموضع الحالي (${formatDuration(currentPosition)})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                listOf(1, 3, 5, 10, -1).forEach { count ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCount == count,
                            onClick = { selectedCount = count }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (count) {
                                -1 -> "تكرار لا نهائي (∞)"
                                else -> "$count مرات"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onEnableRepeat(selectedCount) }) {
                Text("تفعيل")
            }
        },
        dismissButton = {
            Row {
                if (currentState.isEnabled) {
                    TextButton(onClick = onDisableRepeat) {
                        Text("إلغاء التكرار", color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("إلغاء")
                }
            }
        }
    )
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}