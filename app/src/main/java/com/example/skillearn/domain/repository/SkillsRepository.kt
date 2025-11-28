package com.example.skillearn.domain.repository

import com.example.skillearn.data.local.table.Skill
import kotlinx.coroutines.flow.Flow

interface SkillsRepository {

    suspend fun addSkill(skill: Skill)
    fun getSkills(): Flow<List<Skill>>

    suspend fun deleteSkill(skill: Skill)

    fun getSkillById(id: Int): Flow<Skill?>

    suspend fun updateSkill(skill: Skill)
}