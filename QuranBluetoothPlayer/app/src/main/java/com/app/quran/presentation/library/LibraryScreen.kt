package com.app.quran.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quran.data.model.Surah
import com.app.quran.ui.theme.PrimaryGreen
import com.app.quran.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredSurahs by viewModel.filteredSurahs.collectAsState()
    val selectedSurah by viewModel.selectedSurah.collectAsState()
    val surahCount = filteredSurahs.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("ابحث عن سورة...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                cursorColor = PrimaryGreen
            )
        )

        // Surah count header
        Text(
            text = "$surahCount سورة",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Surah List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(
                items = filteredSurahs,
                key = { it.id }
            ) { surah ->
                SurahListItem(
                    surah = surah,
                    isPlaying = selectedSurah?.id == surah.id,
                    onClick = { viewModel.selectSurah(surah) },
                    onPlayClick = { viewModel.playSurah(surah) }
                )
            }
        }
    }
}

@Composable
fun SurahListItem(
    surah: Surah,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPlaying) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Surah Number
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isPlaying) SecondaryGold else PrimaryGreen,
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "%03d".format(surah.id),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Surah Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = surah.nameArabic,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = surah.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Surah Type Badge
            Surface(
                color = if (surah.type == com.app.quran.data.model.SurahType.MEACCAN) {
                    PrimaryGreen.copy(alpha = 0.1f)
                } else {
                    SecondaryGold.copy(alpha = 0.1f)
                },
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = if (surah.type == com.app.quran.data.model.SurahType.MEACCAN) "مكية" else "مدنية",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (surah.type == com.app.quran.data.model.SurahType.MEACCAN) {
                        PrimaryGreen
                    } else {
                        SecondaryGold
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Play Button
            IconButton(onClick = onPlayClick) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = if (isPlaying) SecondaryGold else PrimaryGreen
                )
            }
        }
    }
}