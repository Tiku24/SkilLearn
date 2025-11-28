package com.example.skillearn.ui.navigation

import kotlinx.serialization.Serializable

interface Routes {
}

@Serializable
object HomeScreen

@Serializable
data class SkillDetail(
    val id: Int
)