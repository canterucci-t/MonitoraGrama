package br.com.monitoragrama.ui.medicao

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.monitoragrama.R
import br.com.monitoragrama.ui.common.UiState
import br.com.monitoragrama.ui.components.AppTopBar
import br.com.monitoragrama.ui.components.EstadoCarregando
import br.com.monitoragrama.ui.components.EstadoErro
import br.com.monitoragrama.ui.components.formatar
import br.com.monitoragrama.ui.theme.Dimens
import java.time.LocalDate

@Composable
fun NovaMedicaoScreen(
    onVoltar: () -> Unit,
    onMedicaoSalva: () -> Unit,
    viewModel: NovaMedicaoViewModel = viewModel(factory = NovaMedicaoViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val estadoTrecho by viewModel.trecho.collectAsStateWithLifecycle()

    LaunchedEffect(estado.salvo) {
        if (estado.salvo) onMedicaoSalva()
    }

    Scaffold(
        topBar = { AppTopBar(titulo = stringResource(R.string.medicao_titulo), onVoltar = onVoltar) }
    ) { padding ->
        val trecho = when (val atual = estadoTrecho) {
            UiState.Carregando -> {
                EstadoCarregando(Modifier.padding(padding))
                return@Scaffold
            }
            is UiState.Erro -> {
                EstadoErro(
                    mensagem = stringResource(atual.mensagem),
                    modifier = Modifier.padding(padding),
                    onTentarNovamente = viewModel::recarregar
                )
                return@Scaffold
            }
            is UiState.Sucesso -> atual.dados
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.EspacoPadrao)
        ) {
            Text(
                text = stringResource(R.string.trecho_km, trecho.rodovia, trecho.kmInicial, trecho.kmFinal),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(Dimens.EspacoPequeno))
            Text(
                text = stringResource(R.string.medicao_descricao, LocalDate.now().formatar()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(Dimens.EspacoGrande))
            OutlinedTextField(
                value = estado.alturaTexto,
                onValueChange = viewModel::onAlturaChange,
                label = { Text(stringResource(R.string.medicao_altura)) },
                singleLine = true,
                enabled = !estado.salvando && !estado.salvo,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { viewModel.salvar() }),
                isError = estado.erro != null,
                supportingText = estado.erro?.let { { Text(stringResource(it)) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Dimens.EspacoGrande))
            Button(
                onClick = viewModel::salvar,
                enabled = !estado.salvando && !estado.salvo,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(if (estado.salvando) R.string.medicao_salvando else R.string.medicao_salvar))
            }
        }
    }
}
