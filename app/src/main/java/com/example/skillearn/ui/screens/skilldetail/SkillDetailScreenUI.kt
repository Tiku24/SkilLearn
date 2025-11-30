package com.example.skillearn.ui.screens.skilldetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skillearn.data.local.table.Skill
import com.example.skillearn.ui.components.ColorDialog
import com.example.skillearn.ui.navigation.TimerScreen
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SkillDetailScreenUI(id: Int,viewModel: SkillDetailViewModel,navController: NavController,onEditClick:(Skill) -> Unit,onDeleteClick:(Skill) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var showColorDialog by remember { mutableStateOf(false) }
    var colorPicked by remember { mutableStateOf(Color(0xFFEA4F45)) }
    LaunchedEffect(id) {
        viewModel.getSkillById(id)

        viewModel.event.collectLatest {
            when(it){
                is DetailEvent.DeleteSkill -> {
                    navController.popBackStack()
                }
                is DetailEvent.ShowError -> {

                }
                is DetailEvent.UpdateSkill -> {

                }
            }
        }
    }

    when(val state = uiState){
        is DetailUiState.Loading -> {}
        is DetailUiState.Data -> {
            Column() {
                HeaderSection(
                    onShowColorDialog = { showColorDialog = true },
                    onClick = { navController.popBackStack() },
                    onEditClick = {
                        onEditClick.invoke(state.skill)
                    },
                    onDeleteClick = { onDeleteClick(state.skill) },
                    pickedColor = colorPicked)
                Spacer(modifier = Modifier.height(16.dp))
                TrackSection(skill = state.skill, pickedColor = colorPicked)
                TimerSection(onTimerClick = {
                    val route = TimerScreen(
                        skillId = state.skill.id.toString(),
                        name = state.skill.name
                    )
                    navController.navigate(route)
                }, onQuickClick = {},
                    pickedColor = colorPicked)
            }
        }
        is DetailUiState.Empty -> {}
        is DetailUiState.Error -> {}
    }

    if (showColorDialog){
        ColorDialog(onDismissRequest = { showColorDialog = false }, onColorChanged = {
            colorPicked = it
        })
    }
}


@Composable
fun HeaderSection(pickedColor: Color,onShowColorDialog:() -> Unit,onClick:() -> Unit,onEditClick: () -> Unit,onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "back", modifier = Modifier.clickable{onClick.invoke()})
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .clickable{ onShowColorDialog() }
            .background(color = pickedColor))
        OutlinedButton(onClick = onEditClick, shape = RoundedCornerShape(12.dp)) {
            Text("Edit")
        }
        Button(onClick = onDeleteClick,shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(
            containerColor = pickedColor,
            contentColor = Color.White
        )) {
            Text("Delete")
        }
    }
}


@Composable
fun TrackSection(skill: Skill,pickedColor: Color) {
    val goalHours = skill.goalMinutes.toFloat() // Let's be clear about the unit

    val practicedHours = skill.millisPracticed / 3600000f // Convert practiced millis to hours

    val progress = if (goalHours > 0) {
        (practicedHours / goalHours).coerceIn(0f,1f)
    } else {
        0f
    }

    val numberFormatter = NumberFormat.getNumberInstance(Locale.US)
    numberFormatter.maximumFractionDigits = 2
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        Text(skill.name, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(18.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Progress toward mastery")
            Text("Total hours")
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = { progress },
                color = pickedColor,
                modifier = Modifier
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Text("${(progress * 100).toInt()}%", color = pickedColor)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(numberFormatter.format(practicedHours))
                }
                withStyle(
                    style = SpanStyle(
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                ) {
                    append(" / ${numberFormatter.format(goalHours)}")
                }
            })
        }
    }
}

@Composable
fun TimerSection(pickedColor: Color,onTimerClick: () -> Unit,onQuickClick: () -> Unit) {
    Row() {
        Button(onClick = onTimerClick,shape = RoundedCornerShape(12.dp),colors = ButtonDefaults.buttonColors(
            containerColor = pickedColor,
            contentColor = Color.White
        ) ) {
            Text("Start Timer")
        }
        Spacer(modifier = Modifier.width(5.dp))
        OutlinedButton(onClick = onQuickClick,shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(contentColor = Color(
            0xFF009688
        ), containerColor = Color.Black
        )) {
            Text("Quick Log")
        }
    }
}