package fr.swiftapp.territorymanager.ui.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.swiftapp.territorymanager.data.Territory
import fr.swiftapp.territorymanager.settings.getNameList
import fr.swiftapp.territorymanager.settings.updateNamesList
import fr.swiftapp.territorymanager.utils.reverseDate
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TerritoryListItem(
    territory: Territory,
    isEdit: Boolean = false,
    onSave: (newTerritory: Territory) -> Unit,
    editItem: () -> Unit
) {
    var isOpen by remember {
        mutableStateOf(false)
    }

    DialogName(isOpen = isOpen, close = { name ->
        isOpen = false
        if (name != null) {
            onSave(
                territory.copy(
                    isAvailable = false,
                    givenName = name,
                    returnDate = "",
                    givenDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                )
            )
        }
    })

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(90.dp),
        onClick = {
            if (!isEdit) {
                isOpen = true
            } else {
                editItem()
            }
        }
    ) {
        Row(
            modifier = Modifier.padding(15.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = territory.number.toString(),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(15.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = territory.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                if (!territory.isAvailable) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = territory.givenName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .weight(0.1f)
                    .fillMaxWidth()
            )

            Text(
                text = formatDate(reverseDate(if (territory.isAvailable) territory.returnDate else territory.givenDate)),
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(15.dp))
            Switch(checked = !territory.isAvailable, onCheckedChange = {
                if (it)
                    isOpen = true
                else {
                    onSave(
                        territory.copy(
                            isAvailable = true,
                            returnDate = LocalDate.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        )
                    )
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DialogName(isOpen: Boolean, close: (name: String?) -> Unit) {
    if (isOpen) {
        var name by remember { mutableStateOf("") }
        val context = LocalContext.current
        val names = mutableListOf<String>()
        val namesDropDown = remember { mutableStateListOf<String>() }

        val coroutineScope = rememberCoroutineScope()
        val getAll: () -> Unit = {
            coroutineScope.launch {
                val data = getNameList(context)
                if (data != "") {
                    names.addAll(data.split(','))
                    namesDropDown.addAll(names)
                }
            }
        }
        getAll()

        val updateNames: () -> Unit = {
            coroutineScope.launch {
                updateNamesList(context, names.joinToString(","))
            }
        }

        AlertDialog(
            onDismissRequest = {
                close(null)
            }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            namesDropDown.clear()
                            names.forEach {
                                if (it.lowercase().contains(name.lowercase()))
                                    namesDropDown.add(it)
                            }

                            name = it
                        },
                        label = { Text("Nom du proclamateur") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(0.dp, 200.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.shapes.medium
                            )
                    ) {
                        item {
                            DropdownMenuItem(
                                onClick = {
                                    names.add(name)
                                    updateNames()
                                    close(name)
                                },
                                text = {
                                    Text(text = "Enregistrer ce proclamateur et attribuer")
                                }
                            )
                        }
                        items(namesDropDown) {
                            DropdownMenuItem(
                                onClick = {
                                    name = it
                                },
                                text = {
                                    Text(text = it)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        TextButton(
                            onClick = {
                                close(null)
                            }
                        ) {
                            Text("Annuler")
                        }
                        TextButton(
                            onClick = {
                                close(name)
                            }
                        ) {
                            Text("Valider")
                        }
                    }
                }
            }
        }
    }
}

fun formatDate(textDate: String): String {
    if (textDate.length != 6) {
        return "Date invalide"
    }

    val jour = textDate.substring(0, 2)
    val mois = textDate.substring(2, 4)
    val annee = textDate.substring(4, 6)

    return "$jour/$mois/$annee"
}