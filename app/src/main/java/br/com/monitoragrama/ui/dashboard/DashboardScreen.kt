package br.com.monitoragrama.ui.dashboard

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.monitoragrama.R
import br.com.monitoragrama.domain.model.Clima
import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.StatusCorte
import br.com.monitoragrama.domain.model.TipoVegetacao
import br.com.monitoragrama.domain.model.Trecho
import br.com.monitoragrama.domain.model.TrechoMonitorado
import br.com.monitoragrama.domain.usecase.PreverCorteUseCase
import br.com.monitoragrama.ui.components.AppTopBar
import br.com.monitoragrama.ui.components.StatusBadge
import br.com.monitoragrama.ui.components.cor
import br.com.monitoragrama.ui.components.formatar
import br.com.monitoragrama.ui.theme.ALPHA_FUNDO_STATUS
import br.com.monitoragrama.ui.theme.Dimens
import br.com.monitoragrama.ui.theme.MonitoraGramaTheme
import java.time.LocalDate

@Composable
fun DashboardScreen(
    onTrechoClick: (String) -> Unit,
    onSair: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardContent(estado, onTrechoClick, onSair)
}

@Composable
private fun DashboardContent(
    estado: DashboardUiState,
    onTrechoClick: (String) -> Unit,
    onSair: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                titulo = stringResource(R.string.dashboard_titulo),
                acoes = {
                    IconButton(onClick = onSair) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout),
                            contentDescription = stringResource(R.string.dashboard_sair)
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            estado.carregando -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            estado.trechos.isEmpty() -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(stringResource(R.string.dashboard_vazio)) }

            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(Dimens.EspacoPadrao),
                verticalArrangement = Arrangement.spacedBy(Dimens.EspacoMedio)
            ) {
                item { ResumoStatus(estado) }
                items(estado.trechos, key = { it.trecho.id }) { item ->
                    TrechoCard(item, onClick = { onTrechoClick(item.trecho.id) })
                }
            }
        }
    }
}

@Composable
private fun ResumoStatus(estado: DashboardUiState) {
    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.EspacoPequeno)) {
        ResumoCard(estado.totalCritico, R.string.dashboard_resumo_critico, StatusCorte.CRITICO.cor(), Modifier.weight(1f))
        ResumoCard(estado.totalAtencao, R.string.dashboard_resumo_atencao, StatusCorte.ATENCAO.cor(), Modifier.weight(1f))
        ResumoCard(estado.totalOk, R.string.dashboard_resumo_ok, StatusCorte.OK.cor(), Modifier.weight(1f))
    }
}

@Composable
private fun ResumoCard(total: Int, @StringRes rotulo: Int, cor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = cor.copy(alpha = ALPHA_FUNDO_STATUS),
            contentColor = cor
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(Dimens.EspacoMedio),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(total.toString(), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(rotulo), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun TrechoCard(item: TrechoMonitorado, onClick: () -> Unit) {
    val trecho = item.trecho
    val previsao = item.previsao
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(Dimens.EspacoPadrao),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.trecho_km, trecho.rodovia, trecho.kmInicial, trecho.kmFinal),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(Dimens.EspacoMinimo))
                if (previsao == null) {
                    Text(stringResource(R.string.detalhe_sem_medicao), style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(
                        stringResource(R.string.trecho_altura_estimada, previsao.alturaEstimadaCm),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(R.string.trecho_proximo_corte, previsao.dataProximoCorte.formatar()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(Dimens.EspacoPequeno))
                    StatusBadge(previsao.status)
                }
            }
            Spacer(Modifier.width(Dimens.EspacoPequeno))
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = stringResource(R.string.trecho_abrir_detalhe)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    val trecho = Trecho(
        id = "preview",
        rodovia = "SP-348 Bandeirantes",
        kmInicial = 20,
        kmFinal = 35,
        vegetacao = TipoVegetacao.BRAQUIARIA,
        clima = Clima.CHUVOSO,
        medicoes = listOf(Medicao(LocalDate.now().minusDays(2), 27.0))
    )
    val trechos = listOf(TrechoMonitorado(trecho, PreverCorteUseCase()(trecho)))
    MonitoraGramaTheme {
        DashboardContent(DashboardUiState(carregando = false, trechos = trechos), {}, {})
    }
}
