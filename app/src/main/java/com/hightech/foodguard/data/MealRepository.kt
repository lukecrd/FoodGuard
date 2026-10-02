package com.hightech.foodguard.data

import kotlinx.coroutines.flow.Flow

class MealRepository(private val dao: MealGuidelineDao) {

    fun observeAll(): Flow<List<MealGuideline>> = dao.observeAll()

    fun observeByType(type: MealType): Flow<List<MealGuideline>> = dao.observeByType(type)

    suspend fun getAll(): List<MealGuideline> = dao.getAll()

    suspend fun add(item: MealGuideline) = dao.insert(item)

    suspend fun addAll(items: List<MealGuideline>) = dao.insertAll(items)

    suspend fun update(item: MealGuideline) = dao.update(item)

    suspend fun delete(item: MealGuideline) = dao.delete(item)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun clearAll() = dao.clearAll()
}
