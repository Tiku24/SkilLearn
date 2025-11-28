package com.example.skillearn.ui.screens.skilldetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skillearn.data.local.table.Skill
import com.example.skillearn.ui.screens.home.HomeEvent
import com.example.skillearn.ui.screens.home.HomeScreenViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.Locale
import kotlin.text.format

@Composable
fun SkillDetailScreenUI(id: Int,viewModel: SkillDetailViewModel,navController: NavController,onEditClick:(Skill) -> Unit,onDeleteClick:(Skill) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

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
                    onClick = { navController.popBackStack() },
                    onEditClick = {
                        onEditClick.invoke(state.skill)
                    },
                    onDeleteClick = { onDeleteClick(state.skill) })
                Spacer(modifier = Modifier.height(16.dp))
                TrackSection(skill = state.skill)
            }
        }
        is DetailUiState.Empty -> {}
        is DetailUiState.Error -> {}
    }
}


@Composable
fun HeaderSection(onClick:() -> Unit,onEditClick: () -> Unit,onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "back", modifier = Modifier.clickable{onClick.invoke()})
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(onClick = onEditClick, shape = RoundedCornerShape(12.dp)) {
            Text("Edit")
        }
        Button(onClick = onDeleteClick,shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE76A5D),
            contentColor = Color.White
        )) {
            Text("Delete")
        }
    }
}


@Composable
fun TrackSection(skill: Skill) {
    val progress = if (skill.goalMinutes > 0) {
        (skill.millisPracticed.toFloat() / (skill.goalMinutes * 3600000f)).coerceIn(0f, 1f)
    } else {
        0f
    }
    val numberFormatter = NumberFormat.getNumberInstance(Locale.US)
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
                color = Color(0xFF7C3AED),
                modifier = Modifier
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Text("${skill.millisPracticed}%", color = Color(0xFF7C3AED))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(numberFormatter.format(skill.millisPracticed))
                }
                withStyle(
                    style = SpanStyle(
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                ) {
                    append(" / ${numberFormatter.format(skill.goalMinutes)}")
                }
            })
        }
    }
}