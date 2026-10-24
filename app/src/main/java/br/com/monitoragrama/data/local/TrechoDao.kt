package br.com.monitoragrama.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TrechoDao {

    @Transaction
    @Query("SELECT * FROM trechos")
    fun observarTodos(): Flow<List<TrechoComMedicoes>>

    @Transaction
    @Query("SELECT * FROM trechos WHERE id = :id")
    fun observarPorId(id: String): Flow<TrechoComMedicoes?>

    @Query("SELECT COUNT(*) FROM trechos")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarMedicao(medicao: MedicaoEntity)

    /** Carga inicial: trechos e histórico entram juntos ou nada entra. */
    @Transaction
    suspend fun salvarCarga(trechos: List<TrechoEntity>, medicoes: List<MedicaoEntity>) {
        inserirTrechos(trechos)
        inserirMedicoes(medicoes)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTrechos(trechos: List<TrechoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirMedicoes(medicoes: List<MedicaoEntity>)
}
