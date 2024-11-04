package com.example.imc

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bmi_table")
data class Bmi(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bmi: Double,
    val height: Double,
    val weight: Double
)
