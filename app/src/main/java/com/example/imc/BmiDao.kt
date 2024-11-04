package com.example.imc

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

@Dao
interface BmiDao {
    @Insert
    suspend fun insert(bmi: Bmi)

    @Query("SELECT * FROM bmi_table")
    suspend fun getAllBmiEntries(): List<Bmi>

    @Update
    suspend fun updateBmi(bmi: Bmi)

    @Delete
    suspend fun deleteBmi(bmi: Bmi)
}
