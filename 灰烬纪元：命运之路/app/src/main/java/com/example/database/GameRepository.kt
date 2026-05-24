package com.example.database

import com.example.model.SaveSlotEntity
import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val allSaveSlots: Flow<List<SaveSlotEntity>> = gameDao.getAllSaveSlots()

    suspend fun getSaveSlotById(slotId: Int): SaveSlotEntity? {
        return gameDao.getSaveSlotById(slotId)
    }

    suspend fun saveSlot(saveSlot: SaveSlotEntity) {
        gameDao.insertSaveSlot(saveSlot)
    }

    suspend fun deleteSlotById(slotId: Int) {
        gameDao.deleteSaveSlotById(slotId)
    }
}
