package com.example.skillearn.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.skillearn.data.local.table.Skill
import com.example.skillearn.ui.components.HomeFloatingActionButton
import com.example.skillearn.ui.components.HomeTopAppBar
import com.example.skillearn.ui.screens.home.HomeEvent
import com.example.skillearn.ui.screens.home.HomeScreenUI
import com.example.skillearn.ui.screens.home.HomeScreenViewModel
import com.example.skillearn.ui.screens.home.QuickLogDialog
import com.example.skillearn.ui.screens.skilldetail.SkillDetailScreenUI
import com.example.skillearn.ui.screens.skilldetail.SkillDetailViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavApp() {
    val homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
    val skillDetailViewModel: SkillDetailViewModel = hiltViewModel()
    val nacController = rememberNavController()
    var showQuickLogDialog by remember { mutableStateOf(false) }
    var showFloatingActionBtn by remember { mutableStateOf(false) }
    var showTopAppBar by remember { mutableStateOf(false) }
    val name by homeScreenViewModel.name.collectAsState()
    val hours by homeScreenViewModel.hours.collectAsState()
    val minutes by homeScreenViewModel.minutes.collectAsState()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }


    LaunchedEffect(true) {
        homeScreenViewModel.event.collectLatest {
            when(it){
                is HomeEvent.AddSkill -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(it.message)
                    }
                }
                is HomeEvent.ShowError -> {

                }
            }
        }
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showTopAppBar) {
                HomeTopAppBar()
            }
        },
        floatingActionButton = {
            if (showFloatingActionBtn) {
                HomeFloatingActionButton {
                    homeScreenViewModel.onNameChange("")
                    homeScreenViewModel.onHoursChange("")
                    homeScreenViewModel.onMinutesChange("")
                    showQuickLogDialog = true
                }
            }
        }) { innerPadding ->
        NavHost(
            navController = nacController,
            startDestination = HomeScreen,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<HomeScreen> {
                HomeScreenUI(viewModel = homeScreenViewModel, navController = nacController)
                showFloatingActionBtn = true
                showTopAppBar = true
            }
            composable<SkillDetail> {
                val id = it.toRoute<SkillDetail>().id
                SkillDetailScreenUI(
                    id = id,
                    viewModel = skillDetailViewModel,
                    navController = nacController,
                    onEditClick = { skill ->
                        homeScreenViewModel.storeId(skill.id)
                        homeScreenViewModel.onNameChange(skill.name)
                        homeScreenViewModel.onHoursChange(skill.goalMinutes.toString())
                        homeScreenViewModel.onMinutesChange(skill.millisPracticed.toString())
                        showQuickLogDialog = true
                    }, onDeleteClick = { skill ->
                        skillDetailViewModel.deleteSkill(
                            Skill(
                                id = skill.id,
                                name = skill.name,
                                millisPracticed = skill.millisPracticed,
                                goalMinutes = skill.goalMinutes
                            )
                        )
                    })
                showFloatingActionBtn = false
                showTopAppBar = false
            }
        }
    }

    if (showQuickLogDialog) {
        QuickLogDialog(
            onDismissRequest = { showQuickLogDialog = false },
            onClick = {
                if (showFloatingActionBtn) {
                    homeScreenViewModel.addSkill()
                } else {
                    skillDetailViewModel.updateSkill(
                        Skill(
                            id = homeScreenViewModel.id.value,
                            name = name,
                            millisPracticed = minutes.toLong(),
                            goalMinutes = hours.toInt()
                        )
                    )
                }
            },
            viewModel = homeScreenViewModel,
            text = if (showFloatingActionBtn) "Add New Skill" else "Update Skill"
        )
    }
}