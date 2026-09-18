package br.com.monitoragrama.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.res.stringResource
import br.com.monitoragrama.R
import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.PrevisaoCorte
import br.com.monitoragrama.domain.model.RegrasCorte
import br.com.monitoragrama.ui.theme.Dimens
import br.com.monitoragrama.ui.theme.StatusCriticoCor
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Folga acima do maior valor para o gráfico não encostar no topo. */
private const val MARGEM_TOPO = 1.15

/**
 * Gráfico de linha: medições reais (linha cheia), tendência prevista até a data
 * de corte (tracejada) e o limite de 30 cm (linha vermelha).
 */
@Composable
fun GraficoCrescimento(
    medicoes: List<Medicao>,
    previsao: PrevisaoCorte?,
    modifier: Modifier = Modifier
) {
    val pontos = medicoes.sortedBy { it.data }
    if (pontos.isEmpty()) return

    val corMedicao = MaterialTheme.colorScheme.primary
    val corTendencia = MaterialTheme.colorScheme.tertiary
    val corGrade = MaterialTheme.colorScheme.outlineVariant

    val primeira = pontos.first()
    val ultima = pontos.last()
    val projecao: Medicao? = previsao?.let {
        val dias = ChronoUnit.DAYS.between(ultima.data, it.dataProximoCorte)
        Medicao(it.dataProximoCorte, ultima.alturaCm + it.taxaCrescimentoCmDia * dias)
    }
    val dataFinal = projecao?.data ?: ultima.data
    val totalDias = ChronoUnit.DAYS.between(primeira.data, dataFinal).coerceAtLeast(1)
    val alturaMaxima = (pontos.map { it.alturaCm } + listOfNotNull(projecao?.alturaCm) +
        RegrasCorte.ALTURA_LIMITE_CM).max() * MARGEM_TOPO

    Column(modifier) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(Dimens.AlturaGrafico)
        ) {
            val raio = Dimens.RaioPonto.toPx()
            val traco = Dimens.TracoTracejado.toPx()
            inset(raio) {
                fun posicao(data: LocalDate, altura: Double) = Offset(
                    x = size.width * ChronoUnit.DAYS.between(primeira.data, data) / totalDias.toFloat(),
                    y = size.height * (1f - (altura / alturaMaxima).toFloat())
                )

                drawLine(corGrade, Offset(0f, size.height), Offset(size.width, size.height))

                val yLimite = posicao(primeira.data, RegrasCorte.ALTURA_LIMITE_CM).y
                drawLine(
                    color = StatusCriticoCor,
                    start = Offset(0f, yLimite),
                    end = Offset(size.width, yLimite),
                    strokeWidth = Dimens.EspessuraLimite.toPx()
                )

                projecao?.let {
                    drawLine(
                        color = corTendencia,
                        start = posicao(ultima.data, ultima.alturaCm),
                        end = posicao(it.data, it.alturaCm),
                        strokeWidth = Dimens.EspessuraLinha.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(traco, traco))
                    )
                }

                val caminho = Path()
                pontos.forEachIndexed { i, m ->
                    val p = posicao(m.data, m.alturaCm)
                    if (i == 0) caminho.moveTo(p.x, p.y) else caminho.lineTo(p.x, p.y)
                }
                drawPath(caminho, corMedicao, style = Stroke(width = Dimens.EspessuraLinha.toPx()))
                pontos.forEach { drawCircle(corMedicao, raio, posicao(it.data, it.alturaCm)) }
            }
        }
        Spacer(Modifier.height(Dimens.EspacoPequeno))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ItemLegenda(corMedicao, stringResource(R.string.detalhe_legenda_medicao))
            ItemLegenda(corTendencia, stringResource(R.string.detalhe_legenda_tendencia))
            ItemLegenda(StatusCriticoCor, stringResource(R.string.detalhe_legenda_limite))
        }
    }
}

@Composable
private fun ItemLegenda(cor: Color, texto: String) {
    Box(
        Modifier
            .size(Dimens.TamanhoLegenda)
            .background(cor, CircleShape)
    )
    Spacer(Modifier.width(Dimens.EspacoMinimo))
    Text(texto, style = MaterialTheme.typography.labelMedium)
    Spacer(Modifier.width(Dimens.EspacoMedio))
}
