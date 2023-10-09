package fr.swiftapp.territorymanager.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.swiftapp.territorymanager.R
import fr.swiftapp.territorymanager.data.Territory
import fr.swiftapp.territorymanager.data.TerritoryDatabase
import fr.swiftapp.territorymanager.ui.dialogs.ConfirmationDialog
import fr.swiftapp.territorymanager.utils.convertDate
import fr.swiftapp.territorymanager.utils.reverseDate
import kotlinx.coroutines.launch

@Composable
fun EditTerritory(database: TerritoryDatabase, navController: NavHostController, id: Int?) {
    val territory =
        id?.let { database.territoryDao().getById(it).collectAsState(initial = null).value }

    var number by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    var givenDate by remember {
        mutableStateOf("")
    }
    var returnDate by remember {
        mutableStateOf("")
    }
    var givenName by remember {
        mutableStateOf("")
    }

    LaunchedEffect(territory) {
        number = territory?.number.toString()
        name = territory?.name.toString()
        givenDate = reverseDate(territory?.givenDate.toString())
        returnDate = reverseDate(territory?.returnDate.toString())
        givenName = reverseDate(territory?.givenName.toString())
    }

    var error by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()
    val updatetItem: () -> Unit = {
        val newTerritory = territory?.let {
            Territory(
                id = it.id,
                number = number.toInt(),
                name = name,
                givenDate = convertDate(givenDate),
                returnDate = convertDate(returnDate),
                isAvailable = returnDate.isNotBlank(),
                givenName = givenName
            )
        }

        coroutineScope.launch {
            if (newTerritory != null) {
                database.territoryDao().update(newTerritory)
            }
        }
    }

    val deleteItem: () -> Unit = {
        coroutineScope.launch {
            if (territory != null) {
                database.territoryDao().delete(territory)
            }
        }
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp, 0.dp)
    ) {
        OutlinedTextField(
            value = number,
            singleLine = true,
            onValueChange = { text -> number = text },
            label = { Text("Numéro") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            singleLine = true,
            onValueChange = { text -> name = text },
            label = { Text("Nom du territoire") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        MaskField(
            date = givenDate,
            text = "Date de sortie (jj/mm/aa)",
            mask = "xx/xx/xx",
            maskNumber = 'x',
            onDateChanged = { givenDate = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        MaskField(
            date = returnDate,
            text = "Date de retour (jj/mm/aa)",
            mask = "xx/xx/xx",
            maskNumber = 'x',
            onDateChanged = { returnDate = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = givenName,
            singleLine = true,
            onValueChange = { text -> givenName = text },
            label = { Text("Nom du proclamateur") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                capitalization = KeyboardCapitalization.Words
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            if (number.toIntOrNull() == null || number.isBlank() || name.isBlank() || (givenDate.length != 6 && givenDate != "") || (returnDate.length != 6 && returnDate != "") || givenName.isBlank())
                error = true
            else {
                updatetItem()
                navController.popBackStack()
            }
        }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rounded_save_24),
                    contentDescription = "Enregistrer"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Enregistrer")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Supprimer", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }

        if (error)
            Text(
                text = "Verifiez les valeurs",
                modifier = Modifier.padding(0.dp, 5.dp),
                color = Color.Red
            )

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (showDialog)
        ConfirmationDialog(
            title = "Suppression",
            message = "Voulez-vous vraiment supprimer ce territoire ?",
            confirmButtonColor = MaterialTheme.colorScheme.errorContainer,
            confirmButtonTextColor = MaterialTheme.colorScheme.onErrorContainer,
            confirmButtonText = "Supprimer",
            onConfirm = {
                showDialog = false
                deleteItem()
                navController.popBackStack()
            },
            onCancel = {
                showDialog = false
            })
}
