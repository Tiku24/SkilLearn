package com.example.skillearn.domain.usecase

import com.example.skillearn.data.local.table.Skill
import com.example.skillearn.domain.repository.SkillsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSkillsUseCase @Inject constructor(
    private val skillsRepository: SkillsRepository
) {
    operator fun invoke(): Flow<List<Skill>> = skillsRepository.getSkills()
}