package fr.swiftapp.territorymanager.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipWithSubItems(chipLabel: String, chipItems: List<String>, onClick: (name: String) -> Unit) {
    var isSelected by remember { mutableStateOf(false) }
    var showSubList by remember { mutableStateOf(false) }
    var filterName by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = showSubList,
        onExpandedChange = { showSubList = !showSubList }
    ) {
        FilterChip(
            modifier = Modifier.menuAnchor(),
            selected = isSelected,
            onClick = {},
            label = { Text(text = filterName.ifEmpty { chipLabel + chipItems.first() }) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.rotate(if (showSubList) 180f else 0f),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "List"
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                selectedLabelColor = MaterialTheme.colorScheme.primary,
                selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
            )
        )
        ExposedDropdownMenu(
            expanded = showSubList,
            onDismissRequest = { showSubList = false },
        ) {
            chipItems.forEach { subListItem ->
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        filterName = chipLabel + subListItem
                        showSubList = false
                        isSelected = subListItem != chipItems.first()
                        onClick(subListItem)
                    }
                ) {
                    Text(text = subListItem)
                }
            }
        }
    }
}