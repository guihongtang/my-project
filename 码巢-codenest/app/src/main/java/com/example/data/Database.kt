package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val nickname: String = "全栈拓荒者",
    val level: Int = 2,
    val xp: Int = 350,
    val streak: Int = 5,
    val totalCodeLines: Int = 420,
    val completedProjectsCount: Int = 1
)

@Entity(tableName = "completed_nodes")
data class CompletedNode(
    @PrimaryKey val nodeId: String,
    val category: String,
    val title: String,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_projects")
data class UserProject(
    @PrimaryKey val projectCode: String,
    val title: String,
    val currentMilestone: Int = 1,
    val currentStep: Int = 1,
    val status: String = "IN_PROGRESS" // IN_PROGRESS, COMPLETED
)

@Dao
interface CodeNestDao {
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getUserStatsFlow(): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    suspend fun getUserStats(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserStats(stats: UserStats)

    @Query("SELECT * FROM completed_nodes")
    fun getCompletedNodesFlow(): Flow<List<CompletedNode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCompletedNode(node: CompletedNode)

    @Query("SELECT * FROM user_projects")
    fun getUserProjectsFlow(): Flow<List<UserProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProject(project: UserProject)
}

@Database(entities = [UserStats::class, CompletedNode::class, UserProject::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): CodeNestDao
}
