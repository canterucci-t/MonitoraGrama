package br.com.monitoragrama.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

private val Base = Typography()

val Typography = Base.copy(
    titleLarge = Base.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = Base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = Base.labelLarge.copy(fontWeight = FontWeight.SemiBold)
)
