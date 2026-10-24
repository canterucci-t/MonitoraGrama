package br.com.monitoragrama.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.monitoragrama.R
import br.com.monitoragrama.ui.theme.Dimens
import br.com.monitoragrama.ui.theme.MonitoraGramaTheme

@Composable
fun LoginScreen(
    onLoginSucesso: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(estado.autenticado) {
        if (estado.autenticado) {
            viewModel.onNavegacaoConcluida()
            onLoginSucesso()
        }
    }

    LoginContent(
        estado = estado,
        onMatriculaChange = viewModel::onMatriculaChange,
        onSenhaChange = viewModel::onSenhaChange,
        onEntrar = viewModel::onEntrar
    )
}

@Composable
private fun LoginContent(
    estado: LoginUiState,
    onMatriculaChange: (String) -> Unit,
    onSenhaChange: (String) -> Unit,
    onEntrar: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.EspacoGrande),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_grass),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.IconeLogo)
            )
            Spacer(Modifier.height(Dimens.EspacoPadrao))
            Text(
                text = stringResource(R.string.login_titulo),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.login_subtitulo),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Dimens.EspacoExtra))

            OutlinedTextField(
                value = estado.matricula,
                onValueChange = onMatriculaChange,
                label = { Text(stringResource(R.string.login_matricula)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Dimens.EspacoMedio))
            // Estado de apresentação: vive na tela e sobrevive à rotação.
            var senhaVisivel by rememberSaveable { mutableStateOf(false) }
            OutlinedTextField(
                value = estado.senha,
                onValueChange = onSenhaChange,
                label = { Text(stringResource(R.string.login_senha)) },
                singleLine = true,
                visualTransformation = if (senhaVisivel) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    TextButton(onClick = { senhaVisivel = !senhaVisivel }) {
                        Text(
                            stringResource(
                                if (senhaVisivel) R.string.login_ocultar_senha
                                else R.string.login_mostrar_senha
                            )
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onEntrar() }),
                isError = estado.erro != null,
                supportingText = estado.erro?.let { { Text(stringResource(it)) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Dimens.EspacoGrande))
            Button(onClick = onEntrar, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.login_entrar))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    MonitoraGramaTheme {
        LoginContent(LoginUiState(), {}, {}, {})
    }
}
