package br.com.monitoragrama.ui.detalhe

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.monitoragrama.R
import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.PrevisaoCorte
import br.com.monitoragrama.domain.model.RegrasCorte
import br.com.monitoragrama.domain.model.StatusCorte
import br.com.monitoragrama.domain.model.TrechoMonitorado
import br.com.monitoragrama.ui.common.UiState
import br.com.monitoragrama.ui.components.AlertaStatus
import br.com.monitoragrama.ui.components.AppTopBar
import br.com.monitoragrama.ui.components.EstadoCarregando
import br.com.monitoragrama.ui.components.EstadoErro
import br.com.monitoragrama.ui.components.GraficoCrescimento
import br.com.monitoragrama.ui.components.formatar
import br.com.monitoragrama.ui.components.rotulo
import br.com.monitoragrama.ui.theme.Dimens

/** Quantas medições aparecem antes de o usuário pedir o histórico inteiro. */
private const val MEDICOES_VISIVEIS = 3

@Composable
fun DetalheScreen(
    onVoltar: () -> Unit,
    onRegistrarMedicao: (String) -> Unit,
    viewModel: DetalheViewModel = viewModel(factory = DetalheViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val atual = estado
    val item = (atual as? UiState.Sucesso)?.dados

    Scaffold(
        topBar = { AppTopBar(titulo = stringResource(R.string.detalhe_titulo), onVoltar = onVoltar) },
        floatingActionButton = {
            if (item != null) {
                ExtendedFloatingActionButton(
                    onClick = { onRegistrarMedicao(item.trecho.id) },
                    icon = { Icon(painterResource(R.drawable.ic_add), contentDescription = null) },
                    text = { Text(stringResource(R.string.detalhe_registrar)) }
                )
            }
        }
    ) { padding ->
        val modificador = Modifier.padding(padding)
        when (atual) {
            UiState.Carregando -> EstadoCarregando(modificador)

            is UiState.Erro -> EstadoErro(
                mensagem = stringResource(atual.mensagem),
                modifier = modificador,
                onTentarNovamente = viewModel::recarregar
            )

            is UiState.Sucesso -> DetalheContent(atual.dados, modificador)
        }
    }
}

@Composable
private fun DetalheContent(item: TrechoMonitorado, modifier: Modifier = Modifier) {
    val trecho = item.trecho
    val previsao = item.previsao

    // Estado só de apresentação: fica no Composable e sobrevive à rotação da tela.
    var historicoCompleto by rememberSaveable { mutableStateOf(false) }

    val historico = trecho.medicoes.sortedByDescending { it.data }
    val visiveis = if (historicoCompleto) historico else historico.take(MEDICOES_VISIVEIS)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = Dimens.EspacoPadrao,
            end = Dimens.EspacoPadrao,
            top = Dimens.EspacoPadrao,
            bottom = Dimens.EspacoFab
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.EspacoMedio)
    ) {
        item {
            Text(
                text = stringResource(R.string.trecho_km, trecho.rodovia, trecho.kmInicial, trecho.kmFinal),
                style = MaterialTheme.typography.titleLarge
            )
        }
        if (previsao != null) {
            item { AlertaStatus(previsao.status, mensagemAlerta(previsao)) }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(Dimens.EspacoPadrao)) {
                    LinhaInfo(R.string.detalhe_vegetacao, stringResource(trecho.vegetacao.rotulo()))
                    LinhaInfo(R.string.detalhe_clima, stringResource(trecho.clima.rotulo()))
                    if (previsao != null) {
                        LinhaInfo(
                            R.string.detalhe_taxa,
                            stringResource(R.string.detalhe_taxa_valor, previsao.taxaCrescimentoCmDia)
                        )
                        LinhaInfo(
                            R.string.detalhe_altura,
                            stringResource(R.string.detalhe_altura_valor, previsao.alturaEstimadaCm)
                        )
                        LinhaInfo(
                            R.string.detalhe_proximo_corte,
                            stringResource(
                                R.string.detalhe_dias_valor,
                                previsao.dataProximoCorte.formatar(),
                                textoDias(previsao.diasParaCorte)
                            )
                        )
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(Dimens.EspacoPadrao)) {
                    Text(stringResource(R.string.detalhe_grafico), style = MaterialTheme.typography.titleMedium)
                    GraficoCrescimento(
                        medicoes = trecho.medicoes,
                        previsao = previsao,
                        modifier = Modifier.padding(top = Dimens.EspacoMedio)
                    )
                }
            }
        }
        item {
            Text(stringResource(R.string.detalhe_historico), style = MaterialTheme.typography.titleMedium)
        }
        if (historico.isEmpty()) {
            item { Text(stringResource(R.string.detalhe_sem_medicao)) }
        }
        items(visiveis, key = { it.data.toEpochDay() }) { medicao ->
            LinhaMedicao(medicao)
        }
        if (historico.size > MEDICOES_VISIVEIS) {
            item {
                TextButton(onClick = { historicoCompleto = !historicoCompleto }) {
                    Text(
                        if (historicoCompleto) {
                            stringResource(R.string.detalhe_ver_menos)
                        } else {
                            stringResource(R.string.detalhe_ver_todas, historico.size)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LinhaMedicao(medicao: Medicao) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(medicao.data.formatar())
            Text(stringResource(R.string.detalhe_altura_valor, medicao.alturaCm))
        }
        HorizontalDivider(Modifier.padding(top = Dimens.EspacoPequeno))
    }
}

@Composable
private fun mensagemAlerta(previsao: PrevisaoCorte): String = when (previsao.status) {
    StatusCorte.CRITICO -> stringResource(R.string.alerta_critico, RegrasCorte.ALTURA_LIMITE_CM)
    StatusCorte.ATENCAO -> stringResource(R.string.alerta_atencao, textoDias(previsao.diasParaCorte))
    StatusCorte.OK -> stringResource(R.string.alerta_ok)
}

@Composable
private fun textoDias(dias: Long): String =
    if (dias == 0L) stringResource(R.string.dias_hoje)
    else pluralStringResource(R.plurals.dias, dias.toInt(), dias.toInt())

@Composable
private fun LinhaInfo(@StringRes rotulo: Int, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.EspacoMinimo),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(stringResource(rotulo), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
