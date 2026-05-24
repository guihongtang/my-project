package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "save_slots")
data class SaveSlotEntity(
    @PrimaryKey val slotId: Int, // 1, 2, or 3
    val characterName: String,
    val level: Int,
    val exp: Int,
    val gold: Int,
    val raceName: String, // String mapping to Race
    val pathName: String, // String mapping to StylePath
    val portraitIndex: Int,
    
    // Attributes
    val strength: Int,
    val agility: Int,
    val constitution: Int,
    val intelligence: Int,
    val will: Int,
    val spirit: Int,
    val perception: Int,
    val charisma: Int,
    val attributePointsLeft: Int,
    val skillPointsLeft: Int,
    
    // Status
    val currentHp: Int,
    val maxHp: Int,
    val currentMp: Int,
    val maxMp: Int,
    
    // World cycle state
    val currentRegionId: String,
    val timeOfDayOrdinal: Int,
    val weatherOrdinal: Int,
    val seasonOrdinal: Int,
    val daysElapsed: Int,
    val lastSaveTime: Long,
    
    // Serialized Lists (Equipments, Inventory, Complex States)
    val serializedGear: String, // JSON representing active gears
    val serializedInventory: String, // JSON representing bag consumables
    val serializedSkills: String, // JSON representing learned skills list
    val serializedQuests: String, // JSON representing quest log progress map
    val serializedNpcRelations: String // JSON map of NPC relationships
)

// Converters for complex lists in Room
class GameTypeConverters {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun fromStringMap(value: String?): Map<String, String>? {
        if (value.isNullOrEmpty()) return emptyMap()
        val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        val adapter = moshi.adapter<Map<String, String>>(type)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun toStringMap(map: Map<String, String>?): String {
        val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        val adapter = moshi.adapter<Map<String, String>>(type)
        return adapter.toJson(map ?: emptyMap())
    }
}
