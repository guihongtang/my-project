package com.example.database

import androidx.room.*
import com.example.model.SaveSlotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM save_slots ORDER BY slotId ASC")
    fun getAllSaveSlots(): Flow<List<SaveSlotEntity>>

    @Query("SELECT * FROM save_slots WHERE slotId = :slotId LIMIT 1")
    suspend fun getSaveSlotById(slotId: Int): SaveSlotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaveSlot(saveSlot: SaveSlotEntity)

    @Delete
    suspend fun deleteSaveSlot(saveSlot: SaveSlotEntity)

    @Query("DELETE FROM save_slots WHERE slotId = :slotId")
    suspend fun deleteSaveSlotById(slotId: Int)
}
