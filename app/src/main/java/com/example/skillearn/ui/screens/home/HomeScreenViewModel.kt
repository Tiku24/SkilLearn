package com.example.skillearn.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillearn.data.local.table.Skill
import com.example.skillearn.domain.usecase.AddSkillUseCase
import com.example.skillearn.domain.usecase.DeleteSkillUseCase
import com.example.skillearn.domain.usecase.GetSkillByIdUseCase
import com.example.skillearn.domain.usecase.GetSkillsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val addSkillUseCase: AddSkillUseCase,
    private val getSkillsUseCase: GetSkillsUseCase
): ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()


    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()


    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _millisPracticed = MutableStateFlow(0L)
    val millisPracticed = _millisPracticed.asStateFlow()

    private val _goalMinutes = MutableStateFlow(0)
    val goalMinutes= _goalMinutes.asStateFlow()

    private val _hours = MutableStateFlow("")
    val hours = _hours.asStateFlow()

    private val _minutes = MutableStateFlow("")
    val minutes = _minutes.asStateFlow()
    val id = MutableStateFlow(0)


    init {
        getSkills()
    }

    fun storeId(newId:Int){
        id.update { newId }
    }

    fun onHoursChange(hours: String){
        if (hours.all { it.isDigit() }) {
            _hours.value = hours
        }
    }
    fun onMinutesChange(minutes: String){
        if (minutes.all { it.isDigit() }) {
            _minutes.value = minutes
        }
    }

    fun onNameChange(name:String){
        _name.value  = name
    }
    fun addSkill(){
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
//                val totalGoalMinutes = (_hours.value.toInt() * 60) + _minutes.value.toInt()
                addSkillUseCase(
                    Skill(
                        name = _name.value,
                        millisPracticed = _millisPracticed.value,
                        goalMinutes = _hours.value.toInt()
                    )
                )
            }.onSuccess{
                _event.emit(HomeEvent.AddSkill("Skill added"))
            }.onFailure { throwable ->
                val msg = throwable.message ?: "Unknown error"
                _uiState.update { HomeUiState.Error(throwable.message ?: msg) }
                _event.emit(HomeEvent.ShowError(msg))
            }
        }
    }

    fun getSkills(){
        viewModelScope.launch {
            getSkillsUseCase()
                .map { skills ->
                    if (skills.isEmpty())
                        HomeUiState.Empty
                    else
                        HomeUiState.Data(skills.sortedBy { it.name.lowercase() })
                }
                .onStart { emit(HomeUiState.Loading) }
                .catch { throwable ->
                    emit(HomeUiState.Error(throwable.message ?: "Failed to load skills"))
                    _event.emit(
                        HomeEvent.ShowError(
                            throwable.message ?: "Failed to load skills"
                        )
                    )
                }
                .collect { newState -> _uiState.value = newState }
        }
    }
}

