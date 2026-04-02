package cm.daccvo.cvapp.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun LoginSuccessDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onContinue: () -> Unit
) {

    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Connexion réussie",
                fontSize = 20.sp
            )
        },

        text = {
            Text("Vous êtes maintenant connecté avec succès.")
        },

        confirmButton = {

            Button(
                onClick = onContinue
            ) {
                Text("Continuer")
            }
        }
    )
}