package com.example.skillearn.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.skillearn.data.local.dao.SkillDao
import com.example.skillearn.data.local.table.Skill

@Database(entities = [Skill::class], version = 4, exportSchema = false)
abstract class SkillDatabase : RoomDatabase() {
    abstract fun skillDao(): SkillDao
}