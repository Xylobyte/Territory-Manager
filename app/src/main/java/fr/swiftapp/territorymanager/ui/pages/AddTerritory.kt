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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.swiftapp.territorymanager.R
import fr.swiftapp.territorymanager.data.Territory
import fr.swiftapp.territorymanager.data.TerritoryDatabase
import fr.swiftapp.territorymanager.utils.convertDate
import kotlinx.coroutines.launch

@Composable
fun AddTerritoryPage(database: TerritoryDatabase, navController: NavHostController) {
    var number by remember {
        mutableStateOf("0")
    }
    var name by remember {
        mutableStateOf("")
    }
    var date by remember {
        mutableStateOf("")
    }

    var error by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()
    val insertItem: () -> Unit = {
        val territory = Territory(
            number = number.toInt(),
            name = name,
            givenDate = "",
            returnDate = convertDate(date),
            isAvailable = true,
            givenName = ""
        )

        coroutineScope.launch {
            database.territoryDao().insert(territory)
        }
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
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            singleLine = true,
            onValueChange = { text -> name = text },
            label = { Text("Nom du territoire") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        MaskField(
            date = date,
            text = "Date (jj/mm/aa)",
            mask = "xx/xx/xx",
            maskNumber = 'x',
            onDateChanged = { date = it }
        )

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            if (number.toIntOrNull() == null || number.isBlank() || name.isBlank() || date.length != 6)
                error = true
            else {
                insertItem()
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

        if (error)
            Text(
                text = "Verifiez les valeurs",
                modifier = Modifier.padding(0.dp, 5.dp),
                color = Color.Red
            )
    }
}

@Composable
fun MaskField(
    date: String,
    text: String,
    modifier: Modifier = Modifier,
    mask: String = "000 000 00 00",
    maskNumber: Char = '0',
    onDateChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = date,
        onValueChange = { it ->
            onDateChanged(it.take(mask.count { it == maskNumber }))
        },
        label = {
            Text(text = text)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = PhoneVisualTransformation(mask, maskNumber),
        modifier = modifier.fillMaxWidth(),
    )
}

class PhoneVisualTransformation(val mask: String, val maskNumber: Char) : VisualTransformation {

    private val maxLength = mask.count { it == maskNumber }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.length > maxLength) text.take(maxLength) else text

        val annotatedString = buildAnnotatedString {
            if (trimmed.isEmpty()) return@buildAnnotatedString

            var maskIndex = 0
            var textIndex = 0
            while (textIndex < trimmed.length && maskIndex < mask.length) {
                if (mask[maskIndex] != maskNumber) {
                    val nextDigitIndex = mask.indexOf(maskNumber, maskIndex)
                    append(mask.substring(maskIndex, nextDigitIndex))
                    maskIndex = nextDigitIndex
                }
                append(trimmed[textIndex++])
                maskIndex++
            }
        }

        return TransformedText(annotatedString, PhoneOffsetMapper(mask, maskNumber))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhoneVisualTransformation) return false
        if (mask != other.mask) return false
        return maskNumber == other.maskNumber
    }

    override fun hashCode(): Int {
        return mask.hashCode()
    }
}

private class PhoneOffsetMapper(val mask: String, val numberChar: Char) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        var noneDigitCount = 0
        var i = 0
        while (i < offset + noneDigitCount) {
            if (mask[i++] != numberChar) noneDigitCount++
        }
        return offset + noneDigitCount
    }

    override fun transformedToOriginal(offset: Int): Int =
        offset - mask.take(offset).count { it != numberChar }
}