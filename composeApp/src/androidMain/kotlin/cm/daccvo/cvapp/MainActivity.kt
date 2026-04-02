package cm.daccvo.cvapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cm.daccvo.cvapp.ui.LoginScreen
import cm.daccvo.cvapp.ui.LoginSuccessDialog
import cm.daccvo.cvapp.ui.ProfileScreen
import cm.daccvo.cvapp.ui.RefreshScreen

class MainActivity : ComponentActivity() {
    private var showSuccess by mutableStateOf(false)
    private val authLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 2. C'est ici que tu reçois le retour après le login
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val status = data?.getStringExtra("status")

            if (status == "SUCCESS") {
                // Bravo ! Tu as ton JWT de 10 min, envoie-le à ton backend CV
                println("Token reçu ")
                showSuccess = true

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)



        setContent {

            if (showSuccess) {
                LoginSuccessDialog(

                    show = showSuccess,

                    onDismiss = {
                        showSuccess = false
                    },

                    onContinue = {
                        showSuccess = false
                        //onNavigateProfile()
                    }
                )
            }

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {

                composable("login") {
                    LoginScreen(
                        onLoginClick = {
                            launchAuth()
                        },
                        onProfilClick = {
                            navController.navigate("profile")
                        }
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        userName = "John Doe",
                        email = "john@gmail.com",
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onRefreshClick = {
                            navController.navigate("refresh")
                        }
                    )
                }

                composable("refresh") {
                    RefreshScreen(
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }

    }

    // 1. Déclarer le launcher


    private fun launchAuth() {
        val authUri = Uri.parse("authapp://login")
            .buildUpon()
            .appendQueryParameter("service", "CV")
            .build()

        val intent = Intent(Intent.ACTION_VIEW, authUri).apply {
            // Indispensable pour que Auth App puisse voir 'callingPackage'
            setPackage("cm.daccvo.auth")
        }

        // 3. Lancer l'activité
        authLauncher.launch(intent)
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}