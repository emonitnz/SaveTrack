package com.emon.savetrack.data.repository

import com.emon.savetrack.data.local.Transaction
import com.emon.savetrack.data.local.TransactionDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository to manage transaction data operations.
 */
class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    fun getTransactionsBetween(startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsBetween(startDate, endDate)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }
}