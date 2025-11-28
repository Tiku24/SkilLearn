package com.example.skillearn.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.skillearn.data.local.table.Skill
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {

    @Insert
    suspend fun addSkill(skill: Skill)

    @Query("SELECT * FROM skill")
    fun getAllSkills(): Flow<List<Skill>>

    @Query("SELECT * FROM skill WHERE id = :id")
    fun getSkillById(id: Int): Flow<Skill?>

    @androidx.room.Update
    suspend fun updateSkill(skill: Skill)

    @androidx.room.Delete
    suspend fun deleteSkill(skill: Skill)
}