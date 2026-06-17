package com.app.quran.presentation.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quran.data.model.Bookmark
import com.app.quran.ui.theme.PrimaryGreen
import com.app.quran.ui.theme.SecondaryGold
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    val surahCache by viewModel.surahCache.collectAsState()

    var showDeleteDialog by remember { mutableStateOf<Bookmark?>(null) }
    var showEditDialog by remember { mutableStateOf<Bookmark?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bookmark,
                contentDescription = null,
                tint = PrimaryGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "العلامات المحفوظة",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${bookmarks.size} علامة",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (bookmarks.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "لا توجد علامات محفوظة",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "أضف علامة أثناء الاستماع",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Bookmarks List
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(
                    items = bookmarks,
                    key = { it.id }
                ) { bookmark ->
                    val surah = surahCache[bookmark.surahId]
                    BookmarkItem(
                        bookmark = bookmark,
                        surahName = surah?.nameArabic ?: "سورة",
                        readerName = surah?.name ?: "",
                        onPlay = { viewModel.playFromBookmark(bookmark) },
                        onEdit = { showEditDialog = bookmark },
                        onDelete = { showDeleteDialog = bookmark }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { bookmark ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("حذف العلامة") },
            text = { Text("هل أنت متأكد من حذف هذه العلامة؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBookmark(bookmark)
                        showDeleteDialog = null
                    }
                ) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Edit Dialog
    showEditDialog?.let { bookmark ->
        EditBookmarkDialog(
            bookmark = bookmark,
            onDismiss = { showEditDialog = null },
            onSave = { updatedBookmark ->
                viewModel.updateBookmark(updatedBookmark)
                showEditDialog = null
            }
        )
    }
}

@Composable
fun BookmarkItem(
    bookmark: Bookmark,
    surahName: String,
    readerName: String,
    onPlay: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onPlay() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = SecondaryGold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = surahName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = readerName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = PrimaryGreen
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatTime(bookmark.positionMs),
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryGreen
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = formatDate(bookmark.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Actions
                Row {
                    IconButton(onClick = onPlay, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = PrimaryGreen
                        )
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Note (if exists)
            if (bookmark.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = bookmark.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookmarkDialog(
    bookmark: Bookmark,
    onDismiss: () -> Unit,
    onSave: (Bookmark) -> Unit
) {
    var note by remember { mutableStateOf(bookmark.note) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعديل العلامة") },
        text = {
            Column {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("ملاحظة") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(bookmark.copy(note = note))
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

private fun formatTime(positionMs: Long): String {
    val totalSeconds = positionMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}