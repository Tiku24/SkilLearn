package com.example.skillearn.ui.screens.home

import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skillearn.data.local.table.Skill
import com.example.skillearn.ui.navigation.SkillDetail


@Composable
fun HomeScreenUI(modifier: Modifier = Modifier,viewModel: HomeScreenViewModel,navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()

    when(uiState){
        is HomeUiState.Empty -> {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No skills yet", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
        is HomeUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Data -> {
            val data = (uiState as HomeUiState.Data).skills
            LazyColumn(
            ) {
                items(data){
                    SkillCart(it, onClick = {
                        navController.navigate(SkillDetail(id = it.id))
                    })
                }
            }
        }
        is HomeUiState.Error -> {
            val data = (uiState as HomeUiState.Error)
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(data.message)
                    Button(onClick = {

                    }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
fun SkillCart(skill: Skill,onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable{ onClick() }
            .padding(8.dp)
            .clip(RoundedCornerShape(15.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF222222
        ))
    ) {
        val progress = if (skill.goalMinutes > 0) {
            (skill.millisPracticed.toFloat() / (skill.goalMinutes * 3600000f)).coerceIn(0f, 1f)
        } else {
            0f
        }

        val numberFormatter = NumberFormat.getNumberInstance(Locale.US)
        Column(modifier = Modifier.padding(15.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(skill.name, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowForward, contentDescription = null, tint = Color(0xFF7C3AED))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(imageVector = Icons.Outlined.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Text("${numberFormatter.format(skill.millisPracticed)} / ${numberFormatter.format(skill.goalMinutes)} hours",color = MaterialTheme.colorScheme.onBackground,fontSize = 14.sp,)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Progress",color = MaterialTheme.colorScheme.onBackground,fontSize = 14.sp,)
                Text("${skill.millisPracticed}%",color = MaterialTheme.colorScheme.onBackground,fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = { progress },
                color = Color(0xFF7C3AED),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }

    }
}