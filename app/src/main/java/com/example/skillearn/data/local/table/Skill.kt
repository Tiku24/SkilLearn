package com.example.skillearn.data.local.table


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skill")
data class Skill(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val millisPracticed: Long,
    val goalMinutes: Int
)