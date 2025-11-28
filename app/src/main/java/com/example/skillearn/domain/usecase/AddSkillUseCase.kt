package com.example.skillearn.domain.usecase

import com.example.skillearn.data.local.table.Skill
import com.example.skillearn.domain.repository.SkillsRepository
import javax.inject.Inject

class AddSkillUseCase @Inject constructor(
    private val skillsRepository: SkillsRepository
) {
    suspend operator fun invoke(skill: Skill) = skillsRepository.addSkill(skill)
}