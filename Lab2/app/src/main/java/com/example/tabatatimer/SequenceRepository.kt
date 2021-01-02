package com.example.tabatatimer

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.tabatatimer.room.AppDatabase
import com.example.tabatatimer.room.Sequence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SequenceRepository private constructor(context: Context) {

    private val db = AppDatabase(context)
    private var sequences: LiveData<List<Sequence>> = db.sequenceDao().getAll()

    fun getAll(): LiveData<List<Sequence>> {
        return sequences
    }

    suspend fun get(id: Int): Sequence? {
        return withContext(Dispatchers.IO) {
            db.sequenceDao().get(id)
        }
    }

    suspend fun insert(sequence: Sequence) {
        return withContext(Dispatchers.IO) {
            db.sequenceDao().insert(sequence)
        }
    }

    suspend fun delete(sequence: Sequence) {
        return withContext(Dispatchers.IO) {
            db.sequenceDao().delete(sequence)
        }
    }

    companion object : SingletonHolder<SequenceRepository, Context>(::SequenceRepository)
}