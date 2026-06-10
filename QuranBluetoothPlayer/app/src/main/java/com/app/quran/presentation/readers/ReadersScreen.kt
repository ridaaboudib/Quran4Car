package com.app.quran.presentation.readers

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
import com.app.quran.data.model.Reader
import com.app.quran.ui.theme.PrimaryGreen
import com.app.quran.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadersScreen(
    viewModel: ReadersViewModel = hiltViewModel()
) {
    val readers by viewModel.readers.collectAsState()
    val selectedReader by viewModel.selectedReader.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

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
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = PrimaryGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "المشايخ",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${readers.size} قارئ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Readers List
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(
                items = readers,
                key = { it.id }
            ) { reader ->
                ReaderCard(
                    reader = reader,
                    isSelected = selectedReader?.id == reader.id,
                    isDefault = reader.isDefault,
                    onClick = { viewModel.selectReader(reader) },
                    onSetDefault = { viewModel.setDefaultReader(reader) }
                )
            }
        }

        // Add Reader Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("إضافة قارئ جديد")
            }
        }
    }

    // Add Reader Dialog
    if (showAddDialog) {
        AddReaderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, country, description, pathPrefix ->
                viewModel.addNewReader(name, country, description, pathPrefix)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ReaderCard(
    reader: Reader,
    isSelected: Boolean,
    isDefault: Boolean,
    onClick: () -> Unit,
    onSetDefault: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reader Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = if (isDefault) SecondaryGold else PrimaryGreen,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Reader Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = reader.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = SecondaryGold,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = "افتراضي",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = reader.country,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (reader.description.isNotBlank()) {
                    Text(
                        text = reader.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Set Default Button
            if (!isDefault) {
                IconButton(onClick = onSetDefault) {
                    Icon(
                        imageVector = Icons.Default.StarOutline,
                        contentDescription = "Set as default",
                        tint = SecondaryGold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReaderDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, country: String, description: String, pathPrefix: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var pathPrefix by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إضافة قارئ جديد",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الشيخ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("البلد") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pathPrefix,
                    onValueChange = { pathPrefix = it },
                    label = { Text("مسار الملفات (مثال: quran_farjani/)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, country, description, pathPrefix) },
                enabled = name.isNotBlank() && pathPrefix.isNotBlank()
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}