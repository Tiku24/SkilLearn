package com.example.skillearn.ui.screens.skilldetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillearn.data.local.table.Skill
import com.example.skillearn.domain.usecase.DeleteSkillUseCase
import com.example.skillearn.domain.usecase.GetSkillByIdUseCase
import com.example.skillearn.domain.usecase.UpdateSkillUseCase
import com.example.skillearn.ui.screens.home.HomeEvent
import com.example.skillearn.ui.screens.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkillDetailViewModel @Inject constructor(
    private val getSkillByIdUseCase: GetSkillByIdUseCase,
    private val updateSkillUseCase: UpdateSkillUseCase,
    private val deleteSkillUseCase: DeleteSkillUseCase
): ViewModel() {

    private val _uiState: MutableStateFlow<DetailUiState> = MutableStateFlow(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<DetailEvent>()
    val event = _event.asSharedFlow()


    fun getSkillById(id: Int){
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            runCatching {
                getSkillByIdUseCase(id).collect { skill ->
                    if (skill != null){
                        _uiState.value = DetailUiState.Data(skill)
                    }else{
                        _uiState.value = DetailUiState.Error("Skill with ID $id not found.")
                    }
                }
            }.onFailure { throwable ->
                val msg = throwable.message ?: "Unknown error"
                _uiState.update { DetailUiState.Error(throwable.message ?: msg) }
            }
        }
    }

    fun updateSkill(skill: Skill){
        viewModelScope.launch {
            runCatching {
                updateSkillUseCase(skill)
            }.onSuccess {
                _event.emit(DetailEvent.UpdateSkill("Skill updated"))
            }.onFailure { throwable ->
                val msg = throwable.message ?: "Unknown error"
                _uiState.update { DetailUiState.Error(throwable.message ?: msg) }
                _event.emit(DetailEvent.ShowError(msg))
            }
        }
    }
    fun deleteSkill(skill: Skill) {
        viewModelScope.launch {
            runCatching {
                deleteSkillUseCase(skill)
            }.onSuccess {
                _event.emit(DetailEvent.DeleteSkill("Skill deleted"))
            }.onFailure { throwable ->
                val msg = throwable.message ?: "Unknown error"
                _uiState.update { DetailUiState.Error(throwable.message ?: msg) }
                _event.emit(DetailEvent.DeleteSkill(msg))
            }
        }
    }
}