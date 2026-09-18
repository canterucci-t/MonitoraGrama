package br.com.monitoragrama.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = VerdePrimario,
    onPrimary = Branco,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeEscuro,
    secondary = VerdeOliva,
    onSecondary = Branco,
    tertiary = AzulTendencia,
    background = FundoClaro,
    onBackground = TextoEscuro,
    surface = Branco,
    onSurface = TextoEscuro,
    surfaceVariant = CinzaVerde,
    surfaceContainerHighest = CinzaVerde,
    error = StatusCriticoCor
)

private val DarkColors = darkColorScheme(
    primary = VerdeNoturno,
    onPrimary = VerdeEscuro,
    primaryContainer = VerdeEscuro,
    onPrimaryContainer = VerdeClaro,
    secondary = VerdeClaro,
    tertiary = AzulTendenciaClaro,
    background = FundoEscuro,
    onBackground = TextoClaro,
    surface = SuperficieEscura,
    onSurface = TextoClaro,
    surfaceVariant = CinzaVerdeEscuro,
    surfaceContainerHighest = CinzaVerdeEscuro,
    error = StatusCriticoCor
)

@Composable
fun MonitoraGramaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
