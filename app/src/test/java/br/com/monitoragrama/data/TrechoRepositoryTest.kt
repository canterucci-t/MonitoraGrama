package br.com.monitoragrama.data

import br.com.monitoragrama.data.local.*
import br.com.monitoragrama.data.repository.TrechoRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class TrechoRepositoryTest {
    private class DaoEmMemoria : TrechoDao {
        val dados = MutableStateFlow<List<TrechoComMedicoes>>(emptyList())
        var falharCarga = true
        var cargas = 0

        override fun observarTodos() = dados
        override fun observarPorId(id: String) = dados.map { lista -> lista.find { it.trecho.id == id } }
        override suspend fun contar() = dados.value.size
        override suspend fun salvarMedicao(medicao: MedicaoEntity) = Unit
        override suspend fun inserirTrechos(trechos: List<TrechoEntity>) = Unit
        override suspend fun inserirMedicoes(medicoes: List<MedicaoEntity>) = Unit
        override suspend fun salvarCarga(trechos: List<TrechoEntity>, medicoes: List<MedicaoEntity>) {
            cargas++
            check(!falharCarga) { "Falha na carga inicial" }
            dados.value = trechos.map { trecho ->
                TrechoComMedicoes(trecho, medicoes.filter { it.trechoId == trecho.id })
            }
        }
    }

    @Test fun `falha na carga chega ao observador e nova consulta tenta novamente`() = runBlocking {
        val dao = DaoEmMemoria()
        val repository = TrechoRepositoryImpl(dao)

        val primeira = runCatching { repository.observarTrechos().first() }
        assertTrue("Falha na carga nao pode aparecer como lista vazia", primeira.isFailure)

        dao.falharCarga = false
        assertEquals(5, repository.observarTrechos().first().size)
        assertEquals(2, dao.cargas)
        repository.observarTrechos().first()
        assertEquals("Nao deve repetir carga ja salva", 2, dao.cargas)
    }

    @Test fun `consulta de detalhe aguarda carga inicial`() = runBlocking {
        val dao = DaoEmMemoria().apply { falharCarga = false }
        val trecho = TrechoRepositoryImpl(dao).observarTrecho("sp348-20").first()
        assertNotNull(trecho)
        assertEquals(3, trecho!!.medicoes.size)
    }
}
