package br.com.monitoragrama.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import br.com.monitoragrama.R
import br.com.monitoragrama.ui.theme.Dimens

// O que o app mostra enquanto carrega e quando alguma coisa falha.

@Composable
fun EstadoCarregando(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun EstadoErro(
    mensagem: String,
    modifier: Modifier = Modifier,
    onTentarNovamente: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.EspacoGrande),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(Dimens.IconeLogo)
        )
        Spacer(Modifier.height(Dimens.EspacoPadrao))
        Text(
            text = mensagem,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        if (onTentarNovamente != null) {
            Spacer(Modifier.height(Dimens.EspacoPadrao))
            Button(onClick = onTentarNovamente) {
                Text(stringResource(R.string.tentar_novamente))
            }
        }
    }
}
