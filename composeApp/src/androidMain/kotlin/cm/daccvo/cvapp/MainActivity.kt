package cm.daccvo.cvapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
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
    private var accessToken by mutableStateOf("")

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
                startExchange()


            }
        }
    }

    private val exchangeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Récupération des tokens de manière sécurisée (pas via l'URL)
            val accessTokens = result.data?.getStringExtra("accessToken")

            if (accessTokens != null) {
                // Stocke tes tokens et connecte l'utilisateur
                accessToken = accessTokens
                showSuccess = true
                println("Connexion réussie ! AccessToken: $accessToken")
            }
        } else {
            // Gérer l'annulation (bouton retour)
            println("L'utilisateur a annulé la connexion")
        }
    }

    private val logoutLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // La déconnexion globale a réussi !
            // Rediriger vers l'écran de login de CV App

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)


        setContent {

            if (showSuccess) {
                LoginSuccessDialog(

                    show = showSuccess,
                    token = accessToken,
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
                            startLogout()
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

    private fun startExchange() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("authapp://exchange-token?service=CV")
            // On force le package pour être sûr d'ouvrir la bonne App Auth
            setPackage("cm.daccvo.auth")
        }
        exchangeLauncher.launch(intent)
    }
    

    fun startLogout() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("authapp://logout")
            setPackage("cm.daccvo.auth")
        }
        logoutLauncher.launch(intent)
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}