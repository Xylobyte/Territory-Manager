package fr.swiftapp.territorymanager.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipWithSubItems(chipLabel: String, chipItems: List<String>, value: Int, onClick: (index: Int) -> Unit) {
    var showSubList by remember { mutableStateOf(false) }

    var localValue by remember {
        mutableIntStateOf(value)
    }

    ExposedDropdownMenuBox(
        expanded = showSubList,
        onExpandedChange = {
            if (localValue == 0) showSubList = !showSubList
            else {
                onClick(0)
                localValue = 0
            }
        },
        modifier = Modifier.padding(start = 10.dp)
    ) {
        FilterChip(
            modifier = Modifier.menuAnchor(),
            selected = value != 0,
            onClick = {},
            label = { Text(text = chipLabel + chipItems[value]) },
            trailingIcon = {
                if (value != 0)
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "")
                else
                    Icon(
                        modifier = Modifier.rotate(if (showSubList) 180f else 0f),
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = ""
                    )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                selectedLabelColor = MaterialTheme.colorScheme.primary,
                selectedTrailingIconColor = MaterialTheme.colorScheme.primary,
            )
        )
        ExposedDropdownMenu(
            expanded = showSubList,
            onDismissRequest = { showSubList = false },
        ) {
            chipItems.forEachIndexed { i, subListItem ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        showSubList = false
                        localValue = i
                        onClick(i)
                    },
                    text = { Text(text = subListItem) }
                )
            }
        }
    }
}