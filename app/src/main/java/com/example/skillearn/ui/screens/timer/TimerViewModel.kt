package com.example.skillearn.ui.screens.timer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillearn.domain.repository.SkillsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val skillsRepository: SkillsRepository
): ViewModel() {

    private val skillId: String = checkNotNull(savedStateHandle["skillId"])
    private val _time = MutableStateFlow("00:00:00")
    val time: StateFlow<String> = _time

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private var startTimeMillis = 0L
    private var accumulatedTimeMillis = 0L

    private var timerJob: Job? = null

    fun toggleTimer() {
        if (_isRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isRunning.value = true
        startTimeMillis = System.currentTimeMillis()
        println("first $startTimeMillis")

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isRunning.value) {
                val currentRunTime = System.currentTimeMillis() - startTimeMillis
                println("second $currentRunTime")
                updateFormattedTime(accumulatedTimeMillis + currentRunTime)
                delay(50) // Update more frequently for smoothness
            }
        }
    }

    private fun pauseTimer() {
        if (_isRunning.value) {
            _isRunning.value = false
            timerJob?.cancel()
            accumulatedTimeMillis += System.currentTimeMillis() - startTimeMillis
            updateFormattedTime(accumulatedTimeMillis)
        }
    }

    fun resetTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        accumulatedTimeMillis = 0L
        updateFormattedTime(0L)
    }

    fun saveSession(onSaveSuccess: () -> Unit) {
        pauseTimer()
        println("DEBUG: saveSession - accumulated: $accumulatedTimeMillis, skillId: $skillId")

        if (accumulatedTimeMillis > 0) {
            viewModelScope.launch {
                val id = skillId.toIntOrNull()
                if (id == null) {
                    println("DEBUG: Invalid skillId: $skillId")
                    return@launch
                }

                val skill = skillsRepository.getSkillById(id).first()
                println("DEBUG: Fetched skill: $skill")

                if (skill != null) {
                    val updatedSkill = skill.copy(
                        millisPracticed = skill.millisPracticed + accumulatedTimeMillis
                    )
                    println("DEBUG: Updating skill to: $updatedSkill")
                    skillsRepository.updateSkill(updatedSkill)
                    onSaveSuccess()
                } else {
                    println("DEBUG: Skill is null")
                    onSaveSuccess()
                }
            }
        } else {
            println("DEBUG: No time accumulated")
            onSaveSuccess() // Nothing to save
        }
    }

    private fun updateFormattedTime(millis: Long) {
        val totalSec = millis / 1000
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = totalSec % 60
        _time.value = String.format("%02d:%02d:%02d", h, m, s)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}