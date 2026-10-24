package br.com.monitoragrama.ui

import androidx.lifecycle.SavedStateHandle
import br.com.monitoragrama.R
import br.com.monitoragrama.domain.FakeSessaoRepository
import br.com.monitoragrama.domain.model.Clima
import br.com.monitoragrama.domain.model.Medicao
import br.com.monitoragrama.domain.model.TipoVegetacao
import br.com.monitoragrama.domain.model.Trecho
import br.com.monitoragrama.domain.repository.TrechoRepository
import br.com.monitoragrama.domain.usecase.*
import br.com.monitoragrama.ui.common.UiState
import br.com.monitoragrama.ui.dashboard.DashboardViewModel
import br.com.monitoragrama.ui.detalhe.DetalheViewModel
import br.com.monitoragrama.ui.medicao.NovaMedicaoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecuperacaoTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun preparar() = Dispatchers.setMain(dispatcher)
    @After fun finalizar() = Dispatchers.resetMain()

    private class RepositorioInstavel : TrechoRepository {
        var falhar = true
        var gravacoes = 0
        val trecho = Trecho("t", "SP-000", 0, 10, TipoVegetacao.BATATAIS, Clima.SECO, emptyList())

        override fun observarTrechos(): Flow<List<Trecho>> = flow {
            check(!falhar) { "Falha de leitura" }
            emit(listOf(trecho))
        }

        override fun observarTrecho(id: String): Flow<Trecho?> = flow {
            check(!falhar) { "Falha de leitura" }
            emit(if (id == trecho.id) trecho else null)
        }

        override suspend fun registrarMedicao(trechoId: String, medicao: Medicao): Result<Unit> {
            gravacoes++
            return Result.success(Unit)
        }
    }

    @Test fun `dashboard consulta novamente depois de erro sem sair da tela`() = runTest(dispatcher) {
        val repository = RepositorioInstavel()
        val sessao = FakeSessaoRepository()
        val vm = DashboardViewModel(
            ObservarTrechosUseCase(repository, sessao, PreverCorteUseCase()),
            DefinirFiltroCriticosUseCase(sessao), SairUseCase(sessao)
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        runCurrent()
        assertTrue(vm.uiState.value is UiState.Erro)

        repository.falhar = false
        vm.recarregar()
        runCurrent()

        assertTrue(vm.uiState.value is UiState.Sucesso)
    }

    @Test fun `detalhe consulta novamente depois de erro sem sair da tela`() = runTest(dispatcher) {
        val repository = RepositorioInstavel()
        val vm = DetalheViewModel(
            SavedStateHandle(mapOf("trechoId" to "t")),
            ObservarTrechoUseCase(repository, PreverCorteUseCase())
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        runCurrent()
        assertTrue(vm.uiState.value is UiState.Erro)

        repository.falhar = false
        vm.recarregar()
        runCurrent()

        assertTrue(vm.uiState.value is UiState.Sucesso)
    }

    @Test fun `altura negativa colada nao vira positiva nem e gravada`() = runTest(dispatcher) {
        val repository = RepositorioInstavel().apply { falhar = false }
        val vm = NovaMedicaoViewModel(
            SavedStateHandle(mapOf("trechoId" to "t")),
            ObservarTrechoUseCase(repository, PreverCorteUseCase()), RegistrarMedicaoUseCase(repository)
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.trecho.collect() }
        runCurrent()
        vm.onAlturaChange("-5")
        assertEquals("-5", vm.uiState.value.alturaTexto)
        vm.salvar()
        runCurrent()

        assertEquals(R.string.medicao_erro_altura, vm.uiState.value.erro)
        assertEquals(0, repository.gravacoes)
    }

    @Test fun `medicao mostra erro de leitura e recupera ao tentar novamente`() = runTest(dispatcher) {
        val repository = RepositorioInstavel()
        val vm = NovaMedicaoViewModel(
            SavedStateHandle(mapOf("trechoId" to "t")),
            ObservarTrechoUseCase(repository, PreverCorteUseCase()), RegistrarMedicaoUseCase(repository)
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.trecho.collect() }
        runCurrent()
        assertEquals(UiState.Erro(R.string.erro_carregar_trecho), vm.trecho.value)
        vm.onAlturaChange("12,5")
        vm.salvar()
        runCurrent()
        assertEquals(0, repository.gravacoes)

        repository.falhar = false
        vm.recarregar()
        runCurrent()
        assertTrue(vm.trecho.value is UiState.Sucesso)
        assertEquals("12,5", vm.uiState.value.alturaTexto)
        vm.salvar()
        runCurrent()
        assertTrue(vm.uiState.value.salvo)
        assertEquals(1, repository.gravacoes)
    }

    @Test fun `medicao nao permite gravar trecho inexistente`() = runTest(dispatcher) {
        val repository = RepositorioInstavel().apply { falhar = false }
        val vm = NovaMedicaoViewModel(
            SavedStateHandle(mapOf("trechoId" to "ausente")),
            ObservarTrechoUseCase(repository, PreverCorteUseCase()), RegistrarMedicaoUseCase(repository)
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.trecho.collect() }
        runCurrent()
        assertEquals(UiState.Erro(R.string.detalhe_nao_encontrado), vm.trecho.value)
        vm.onAlturaChange("12,5")
        vm.salvar()
        runCurrent()
        assertEquals(0, repository.gravacoes)
    }
}
