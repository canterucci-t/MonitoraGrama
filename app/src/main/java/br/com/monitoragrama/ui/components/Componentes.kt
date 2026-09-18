package br.com.monitoragrama.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import br.com.monitoragrama.R
import br.com.monitoragrama.domain.model.StatusCorte
import br.com.monitoragrama.ui.theme.ALPHA_FUNDO_STATUS
import br.com.monitoragrama.ui.theme.Dimens

/** Barra superior padrão do app, com botão de voltar opcional. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    titulo: String,
    onVoltar: (() -> Unit)? = null,
    acoes: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            if (onVoltar != null) {
                IconButton(onClick = onVoltar) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.voltar)
                    )
                }
            }
        },
        actions = acoes,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

/** Selo colorido com o status de corte do trecho. */
@Composable
fun StatusBadge(status: StatusCorte, modifier: Modifier = Modifier) {
    val cor = status.cor()
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = cor.copy(alpha = ALPHA_FUNDO_STATUS),
        contentColor = cor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.EspacoPequeno, vertical = Dimens.EspacoMinimo),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(status.icone()),
                contentDescription = null,
                modifier = Modifier.size(Dimens.IconePequeno)
            )
            Spacer(Modifier.width(Dimens.EspacoMinimo))
            Text(stringResource(status.rotulo()), style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Faixa de alerta exibida no topo do detalhe do trecho. */
@Composable
fun AlertaStatus(status: StatusCorte, mensagem: String, modifier: Modifier = Modifier) {
    val cor = status.cor()
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cor.copy(alpha = ALPHA_FUNDO_STATUS),
            contentColor = cor
        )
    ) {
        Row(
            modifier = Modifier.padding(Dimens.EspacoPadrao),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(status.icone()), contentDescription = null)
            Spacer(Modifier.width(Dimens.EspacoMedio))
            Text(mensagem, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
