package fr.swiftapp.territorymanager.ui.dialogs

import android.icu.text.CaseMap.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmButtonColor: Color,
    confirmButtonTextColor: Color,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onCancel()
        },
        confirmButton = {
            Button(
                onClick = { onConfirm() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = confirmButtonColor
                )
            ) {
                Text(text = confirmButtonText, color = confirmButtonTextColor)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                }
            ) {
                Text(text = "Annuler")
            }
        },
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(text = message)
        }
    )
}
