package br.com.monitoragrama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.monitoragrama.navigation.AppNavHost
import br.com.monitoragrama.navigation.Rotas
import br.com.monitoragrama.ui.theme.MonitoraGramaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Sessão guardada nas preferências: o app reabre direto no painel.
        val container = (application as MonitoraGramaApp).container
        val rotaInicial = if (container.sessaoAtiva()) Rotas.DASHBOARD else Rotas.LOGIN

        setContent {
            MonitoraGramaTheme {
                AppNavHost(rotaInicial = rotaInicial)
            }
        }
    }
}
