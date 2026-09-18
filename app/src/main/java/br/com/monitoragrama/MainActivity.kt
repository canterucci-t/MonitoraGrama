package br.com.monitoragrama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.monitoragrama.ui.navigation.AppNavHost
import br.com.monitoragrama.ui.theme.MonitoraGramaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonitoraGramaTheme {
                AppNavHost()
            }
        }
    }
}
