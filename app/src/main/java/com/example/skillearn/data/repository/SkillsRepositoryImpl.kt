package com.example.skillearn.data.repository

import com.example.skillearn.data.local.dao.SkillDao
import com.example.skillearn.data.local.table.Skill
import com.example.skillearn.domain.repository.SkillsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SkillsRepositoryImpl @Inject constructor(private val dao: SkillDao): SkillsRepository {
    override suspend fun addSkill(skill: Skill) {
        dao.addSkill(skill = skill)
    }

    override fun getSkills(): Flow<List<Skill>> {
        return dao.getAllSkills()
    }

    override suspend fun deleteSkill(skill: Skill) {
        dao.deleteSkill(skill)
    }

    override fun getSkillById(id: Int): Flow<Skill?> {
        return dao.getSkillById(id)
    }

    override suspend fun updateSkill(skill: Skill) {
        dao.updateSkill(skill)
    }
}