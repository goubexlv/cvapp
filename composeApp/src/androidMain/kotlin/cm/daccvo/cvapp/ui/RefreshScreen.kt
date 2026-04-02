package cm.daccvo.cvapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RefreshScreen(
    onBack: () -> Unit
) {

    var isRefreshing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (isRefreshing) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(20.dp))
            Text("Refreshing...")
        }

        Button(
            onClick = {
                isRefreshing = true
            }
        ) {
            Text("Refresh Token")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onBack
        ) {
            Text("Retour")
        }
    }
}