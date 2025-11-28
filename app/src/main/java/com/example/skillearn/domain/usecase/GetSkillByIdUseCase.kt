package com.example.skillearn.domain.usecase

import com.example.skillearn.domain.repository.SkillsRepository
import javax.inject.Inject

class GetSkillByIdUseCase @Inject constructor(private val skillsRepository: SkillsRepository) {
    operator fun invoke(id: Int) = skillsRepository.getSkillById(id)
}