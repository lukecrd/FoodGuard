package com.hightech.foodguard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromStatus(status: FoodStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): FoodStatus = FoodStatus.valueOf(value)

    @TypeConverter
    fun fromMealType(type: MealType): String = type.name

    @TypeConverter
    fun toMealType(value: String): MealType = try {
        MealType.valueOf(value)
    } catch (e: Exception) {
        MealType.ALTRO
    }
}

@Database(entities = [FoodItem::class, MealGuideline::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun mealGuidelineDao(): MealGuidelineDao

    companion object {
        @Volatile private var INSTANCE: FoodDatabase? = null

        fun getInstance(context: Context): FoodDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "foodguard.db"
                )
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
    }
}
