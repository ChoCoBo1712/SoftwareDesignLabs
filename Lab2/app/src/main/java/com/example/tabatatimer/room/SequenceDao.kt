package com.example.tabatatimer.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SequenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sequence: Sequence)

    @Delete
    fun delete(sequence: Sequence)

    @Query("SELECT * FROM sequence ORDER BY title ASC")
    fun getAll(): LiveData<List<Sequence>>

    @Query("SELECT * FROM sequence WHERE sequence_id = :id")
    fun get(id: Int) : Sequence?
}