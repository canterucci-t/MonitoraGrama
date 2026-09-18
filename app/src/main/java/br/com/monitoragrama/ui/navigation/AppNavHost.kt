package br.com.monitoragrama.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.monitoragrama.ui.dashboard.DashboardScreen
import br.com.monitoragrama.ui.detalhe.DetalheScreen
import br.com.monitoragrama.ui.login.LoginScreen
import br.com.monitoragrama.ui.medicao.NovaMedicaoScreen

/** Rotas do fluxo mapeado no wireframe: Login → Dashboard → Detalhe → Nova medição. */
object Rotas {
    const val ARG_TRECHO_ID = "trechoId"

    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val DETALHE = "detalhe/{$ARG_TRECHO_ID}"
    const val MEDICAO = "medicao/{$ARG_TRECHO_ID}"

    fun detalhe(trechoId: String) = "detalhe/$trechoId"
    fun medicao(trechoId: String) = "medicao/$trechoId"
}

private val argumentoTrecho = listOf(navArgument(Rotas.ARG_TRECHO_ID) { type = NavType.StringType })

/** Ignora cliques repetidos enquanto a tela está saindo (evita voltar duas vezes). */
private fun NavBackStackEntry.estaAtiva() = lifecycle.currentState == Lifecycle.State.RESUMED

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    fun voltar(entrada: NavBackStackEntry) {
        if (entrada.estaAtiva()) navController.popBackStack()
    }

    NavHost(navController = navController, startDestination = Rotas.LOGIN) {
        composable(Rotas.LOGIN) {
            LoginScreen(
                onLoginSucesso = {
                    navController.navigate(Rotas.DASHBOARD) {
                        popUpTo(Rotas.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Rotas.DASHBOARD) { entrada ->
            DashboardScreen(
                onTrechoClick = { id ->
                    if (entrada.estaAtiva()) navController.navigate(Rotas.detalhe(id))
                },
                onSair = {
                    navController.navigate(Rotas.LOGIN) {
                        popUpTo(Rotas.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }
        composable(Rotas.DETALHE, arguments = argumentoTrecho) { entrada ->
            DetalheScreen(
                onVoltar = { voltar(entrada) },
                onRegistrarMedicao = { id ->
                    if (entrada.estaAtiva()) navController.navigate(Rotas.medicao(id))
                }
            )
        }
        composable(Rotas.MEDICAO, arguments = argumentoTrecho) { entrada ->
            NovaMedicaoScreen(
                onVoltar = { voltar(entrada) },
                onMedicaoSalva = { voltar(entrada) }
            )
        }
    }
}
