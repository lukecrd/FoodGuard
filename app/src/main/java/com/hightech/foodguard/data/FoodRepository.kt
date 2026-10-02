package com.hightech.foodguard.data

import kotlinx.coroutines.flow.Flow

class FoodRepository(private val dao: FoodDao) {

    fun observeAll(): Flow<List<FoodItem>> = dao.observeAll()

    suspend fun getForbidden(): List<FoodItem> = dao.getForbidden()

    suspend fun getAllowed(): List<FoodItem> = dao.getAllowed()

    suspend fun add(item: FoodItem) = dao.insert(item)

    suspend fun addAll(items: List<FoodItem>) = dao.insertAll(items)

    suspend fun update(item: FoodItem) = dao.update(item)

    suspend fun delete(item: FoodItem) = dao.delete(item)
}
