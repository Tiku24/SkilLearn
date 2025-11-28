package com.example.skillearn.ui.screens.skilldetail

import com.example.skillearn.data.local.table.Skill
import com.example.skillearn.ui.screens.home.HomeEvent

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Data(val skill: Skill) : DetailUiState()
    data object Empty : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

sealed class DetailEvent{
    data class ShowError(val message: String) : DetailEvent()
    data class UpdateSkill(val message: String) : DetailEvent()
    data class DeleteSkill(val message: String) : DetailEvent()
}