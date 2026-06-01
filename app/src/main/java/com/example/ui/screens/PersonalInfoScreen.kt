package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersonalInfoRecord
import com.example.ui.parseHexColor
import com.example.viewmodel.PersonalInfoViewModel

private val SoftBackground = Color(0xFFF8F9FC)
private val LightCardBg = Color(0xFFFFFFFF)
private val DarkSlateText = Color(0xFF1A1C1E)
private val SubtitleText = Color(0xFF5A5D6B)
private val BorderColor = Color(0xFFE2E4EB)
private val AccentPrimary = Color(0xFF4A6572)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    viewModel: PersonalInfoViewModel,
    modifier: Modifier = Modifier
) {
    val records by viewModel.records.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isDialogOpen by viewModel.isFormDialogOpen.collectAsState()

    var selectedRecordForView by remember { mutableStateOf<PersonalInfoRecord?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize().background(SoftBackground),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                containerColor = AccentPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_record_fab").padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add new profile record", modifier = Modifier.size(24.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SoftBackground)
                .padding(horizontal = 16.dp)
        ) {
            // Header Block
            HeaderSection()

            // Search Bar Component
            SearchBarComponent(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Relationship Categories Filter Chips Row
            CategoryChipsRow(
                selectedCategory = selectedCategory,
                onCategorySelect = { viewModel.selectCategory(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Records List View
            if (records.isEmpty()) {
                EmptyStateView(
                    hasSearch = searchQuery.isNotEmpty() || selectedCategory != "All",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    items(records, key = { it.id }) { record ->
                        RecordCard(
                            record = record,
                            isExpanded = selectedRecordForView?.id == record.id,
                            onCardClick = {
                                selectedRecordForView = if (selectedRecordForView?.id == record.id) null else record
                            },
                            onEdit = { viewModel.openEditDialog(record) },
                            onDelete = { viewModel.deleteRecord(record) }
                        )
                    }
                }
            }
        }

        // Add/Edit Dialog Block
        if (isDialogOpen) {
            AddEditRecordDialog(viewModel = viewModel)
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Personal Directory",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DarkSlateText,
            lineHeight = 34.sp
        )
        Text(
            text = "Secure local binder for personal profiles & contacts.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = SubtitleText
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarComponent(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search name, email, phone or category...", fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search magnifier icon", tint = SubtitleText) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear search input query")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentPrimary,
            unfocusedBorderColor = BorderColor,
            focusedContainerColor = LightCardBg,
            unfocusedContainerColor = LightCardBg
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .testTag("search_bar_input")
    )
}

@Composable
fun CategoryChipsRow(
    selectedCategory: String,
    onCategorySelect: (String) -> Unit
) {
    val categories = listOf("All", "Family", "Friend", "Work", "Emergency", "Other")
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().testTag("category_filter_row")
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            val containerColor = if (isSelected) AccentPrimary else Color.Transparent
            val textColor = if (isSelected) Color.White else SubtitleText
            val borderModifier = if (isSelected) Modifier else Modifier.border(1.dp, BorderColor, RoundedCornerShape(18.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(containerColor)
                    .then(borderModifier)
                    .clickable { onCategorySelect(category) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("category_chip_$category"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun RecordCard(
    record: PersonalInfoRecord,
    isExpanded: Boolean,
    onCardClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val avatarColor = parseHexColor(record.avatarColorHex)
    val initials = record.fullName.trim().split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    val categoryColor = when (record.relationship) {
        "Family" -> Color(0xFFE57373)
        "Work" -> Color(0xFF64B5F6)
        "Emergency" -> Color(0xFF81C784)
        "Friend" -> Color(0xFFFFB74D)
        else -> Color(0xFFBA68C8)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("record_card_${record.id}"),
        colors = CardDefaults.cardColors(containerColor = LightCardBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar representation
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(avatarColor.copy(alpha = 0.12f))
                        .border(1.dp, avatarColor.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = avatarColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name and Description Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = record.fullName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkSlateText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Relationship Tag Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(categoryColor.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = record.relationship,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = categoryColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = record.phone.ifBlank { "No phone registered" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SubtitleText
                    )
                }
            }

            // Expanded detail action drawer
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Divider(color = BorderColor, modifier = Modifier.padding(vertical = 8.dp))

                    if (record.email.isNotBlank()) {
                        DetailItemRow(icon = Icons.Outlined.Email, label = "Email", value = record.email)
                    }
                    if (record.note.isNotBlank()) {
                        DetailItemRow(icon = Icons.Outlined.Notes, label = "Details / Address", value = record.note)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Buttons row
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F)),
                            modifier = Modifier.testTag("delete_btn_${record.id}")
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete contact", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onEdit,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                            modifier = Modifier.testTag("edit_btn_${record.id}"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit contact", modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "$label icon",
            tint = SubtitleText,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, fontSize = 10.sp, color = SubtitleText, fontWeight = FontWeight.Bold)
            Text(text = value, fontSize = 13.sp, color = DarkSlateText)
        }
    }
}

@Composable
fun EmptyStateView(
    hasSearch: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (hasSearch) Icons.Default.SearchOff else Icons.Default.FolderOpen,
                contentDescription = "Empty list icon indicator",
                tint = BorderColor,
                modifier = Modifier.size(68.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (hasSearch) "No Matching Profiles" else "Binder is Completely Empty",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkSlateText
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (hasSearch) "Try adjusting your keyword filter or switching relationship category chips." 
                       else "Tap the floating ADD button in the bottom corner to insert your first personal contact profile record.",
                fontSize = 13.sp,
                color = SubtitleText,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordDialog(
    viewModel: PersonalInfoViewModel
) {
    val name by viewModel.formName.collectAsState()
    val phone by viewModel.formPhone.collectAsState()
    val email by viewModel.formEmail.collectAsState()
    val relationship by viewModel.formRelationship.collectAsState()
    val note by viewModel.formNote.collectAsState()
    val colorHex by viewModel.formColor.collectAsState()
    val isEditMode = viewModel.editingRecord.collectAsState().value != null

    val relationshipCategories = listOf("Family", "Friend", "Work", "Emergency", "Other")
    var isDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { viewModel.closeDialog() },
        title = {
            Text(
                text = if (isEditMode) "Edit Personal Record" else "Add New Personal Record",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = DarkSlateText
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("Full Name *") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_record_name_input")
                )

                // Phone Number
                OutlinedTextField(
                    value = phone,
                    onValueChange = { viewModel.updatePhone(it) },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_record_phone_input")
                )

                // Email Address
                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text("Email Address") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_record_email_input")
                )

                // Relationship Spinner Dropdown selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = relationship,
                        onValueChange = {},
                        label = { Text("Category Connection") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { isDropdownExpanded = !isDropdownExpanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Toggle category selection dropdown menu")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentPrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth().clickable { isDropdownExpanded = !isDropdownExpanded }.testTag("add_record_category_trigger")
                    )

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        relationshipCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    viewModel.updateRelationship(category)
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Description Notes Info
                OutlinedTextField(
                    value = note,
                    onValueChange = { viewModel.updateNote(it) },
                    label = { Text("Special Notes / Remarks") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth().height(90.dp).testTag("add_record_note_input")
                )

                // Color Hex Chooser (Visual Indicator circles)
                Text("Avatar Accent Theme Color", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SubtitleText)
                val colorOptions = listOf("#6750A4", "#3F51B5", "#2196F3", "#009688", "#4CAF50", "#FF9800", "#E91E63", "#9C27B0")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colorOptions.forEach { colorStr ->
                        val colorOptionObj = parseHexColor(colorStr)
                        val isSelectedColor = colorHex.equals(colorStr, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(colorOptionObj)
                                .border(
                                    width = if (isSelectedColor) 2.dp else 0.dp,
                                    color = if (isSelectedColor) Color.Black else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.updateColor(colorStr) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.saveRecord() },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                modifier = Modifier.testTag("save_record_submit_btn")
            ) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.closeDialog() }) {
                Text("Cancel", color = SubtitleText)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = LightCardBg
    )
}
