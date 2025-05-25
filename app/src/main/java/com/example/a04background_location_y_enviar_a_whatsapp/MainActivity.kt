package com.example.a04background_location_y_enviar_a_whatsapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.net.toUri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.core.content.ContextCompat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a04background_location_y_enviar_a_whatsapp.ui.theme._04BackgroundLocationyEnviaraWhatsappTheme
import com.google.android.gms.location.LocationServices
import android.widget.Toast

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _04BackgroundLocationyEnviaraWhatsappTheme {
                EnviarUbicacionWhatsappUI()
            }
        }
    }
}

@Composable
fun EnviarUbicacionWhatsappUI() {
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* el resultado se maneja dentro del botón */ }
    )
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Mensaje") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (hasPermission) {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                val uri = "https://api.whatsapp.com/send?text=${
                                    Uri.encode("$message\n\nUbicación:\nhttps://maps.google.com/?q=${location.latitude},${location.longitude}")
                                }".toUri()
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                //intent.setPackage("com.whatsapp")
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar por WhatsApp")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _04BackgroundLocationyEnviaraWhatsappTheme {
        Greeting("Android")
    }
}