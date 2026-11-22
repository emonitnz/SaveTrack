package com.emon.savetrack.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class TransactionType {
    INCOME,
    EXPENSE
}

class TransactionTypeConverter {
    @TypeConverter
    fun fromType(type: TransactionType): String = type.name

    @TypeConverter
    fun toType(value: String): TransactionType = TransactionType.valueOf(value)
}

/**
 * Room entity representing an income or expense transaction.
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: TransactionType,
    val amount: Double,
    val itemName: String,
    val date: Long = System.currentTimeMillis()
)