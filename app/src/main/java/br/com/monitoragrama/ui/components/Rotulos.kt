package br.com.monitoragrama.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import br.com.monitoragrama.R
import br.com.monitoragrama.domain.model.Clima
import br.com.monitoragrama.domain.model.StatusCorte
import br.com.monitoragrama.domain.model.TipoVegetacao
import br.com.monitoragrama.ui.theme.StatusAtencaoCor
import br.com.monitoragrama.ui.theme.StatusCriticoCor
import br.com.monitoragrama.ui.theme.StatusOkCor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

// Converte modelos do domínio em recursos de UI (textos, ícones e cores).
// O domínio não conhece Android; a tradução acontece somente aqui.

@StringRes
fun TipoVegetacao.rotulo(): Int = when (this) {
    TipoVegetacao.BRAQUIARIA -> R.string.vegetacao_braquiaria
    TipoVegetacao.BATATAIS -> R.string.vegetacao_batatais
    TipoVegetacao.ESMERALDA -> R.string.vegetacao_esmeralda
}

@StringRes
fun Clima.rotulo(): Int = when (this) {
    Clima.CHUVOSO -> R.string.clima_chuvoso
    Clima.TEMPERADO -> R.string.clima_temperado
    Clima.SECO -> R.string.clima_seco
}

@StringRes
fun StatusCorte.rotulo(): Int = when (this) {
    StatusCorte.OK -> R.string.status_ok
    StatusCorte.ATENCAO -> R.string.status_atencao
    StatusCorte.CRITICO -> R.string.status_critico
}

@DrawableRes
fun StatusCorte.icone(): Int = when (this) {
    StatusCorte.OK -> R.drawable.ic_check_circle
    StatusCorte.ATENCAO -> R.drawable.ic_warning
    StatusCorte.CRITICO -> R.drawable.ic_error
}

fun StatusCorte.cor(): Color = when (this) {
    StatusCorte.OK -> StatusOkCor
    StatusCorte.ATENCAO -> StatusAtencaoCor
    StatusCorte.CRITICO -> StatusCriticoCor
}

private val formatoData: DateTimeFormatter =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.forLanguageTag("pt-BR"))

fun LocalDate.formatar(): String = format(formatoData)
