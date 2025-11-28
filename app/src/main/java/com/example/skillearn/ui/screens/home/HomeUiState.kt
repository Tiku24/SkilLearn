package com.example.skillearn.ui.screens.home

import com.example.skillearn.data.local.table.Skill

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Data(val skills: List<Skill>) : HomeUiState()
    data object Empty : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

sealed class HomeEvent{
    data class AddSkill(val message: String) : HomeEvent()
    data class ShowError(val message: String) : HomeEvent()
}