package com.hightech.foodguard.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealGuidelineDao {

    @Query("SELECT * FROM meal_guidelines ORDER BY mealType, id")
    fun observeAll(): Flow<List<MealGuideline>>

    @Query("SELECT * FROM meal_guidelines WHERE mealType = :type ORDER BY id")
    fun observeByType(type: MealType): Flow<List<MealGuideline>>

    @Query("SELECT * FROM meal_guidelines ORDER BY mealType, id")
    suspend fun getAll(): List<MealGuideline>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MealGuideline): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MealGuideline>): List<Long>

    @Update
    suspend fun update(item: MealGuideline)

    @Delete
    suspend fun delete(item: MealGuideline)

    @Query("DELETE FROM meal_guidelines WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM meal_guidelines")
    suspend fun clearAll()
}
