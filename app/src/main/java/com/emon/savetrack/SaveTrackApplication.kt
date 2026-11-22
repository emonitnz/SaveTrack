package com.emon.savetrack

import android.app.Application
import com.emon.savetrack.data.local.SaveTrackDatabase
import com.emon.savetrack.data.repository.TransactionRepository

/**
 * Custom Application class serving as manual dependency injection container.
 */
class SaveTrackApplication : Application() {

    val database by lazy { SaveTrackDatabase.getDatabase(this) }
    val repository by lazy { TransactionRepository(database.transactionDao()) }
}