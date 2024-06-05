package com.example.ratemate.myPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ratemate.R
import com.example.ratemate.data.Survey
import com.example.ratemate.repository.SurveyRepository
import com.example.ratemate.ui.theme.NotoSansKr
import com.example.ratemate.viewModel.SortType
import com.example.ratemate.viewModel.SurveyModelFactory
import com.example.ratemate.viewModel.SurveyViewModel

@Composable
fun Answer(navController: NavHostController) {
    val repository = SurveyRepository()
    val viewModel: SurveyViewModel = viewModel(factory = SurveyModelFactory(repository))
    val surveys by viewModel.surveys.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var sortText by remember { mutableStateOf("최신순") }
    Scaffold(
        content = { paddingValues ->
            Column(modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        sortText,
                        fontSize = 15.sp,
                        fontFamily = NotoSansKr,
                        fontWeight = FontWeight.Bold
                    )
                    Box {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "정렬", tint = Color.Black)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("최신순",fontFamily = NotoSansKr,
                                    fontWeight = FontWeight.Bold) },
                                onClick = {
                                    viewModel.sortSurveys(SortType.LATEST)
                                    sortText = "최신순"
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("좋아요 많은 순", fontFamily = NotoSansKr,
                                    fontWeight = FontWeight.Bold) },
                                onClick = {
                                    viewModel.sortSurveys(SortType.MOST_LIKED)
                                    sortText = "좋아요 많은 순"
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("답변 많은 순", fontFamily = NotoSansKr,
                                    fontWeight = FontWeight.Bold) },
                                onClick = {
                                    viewModel.sortSurveys(SortType.MOST_RESPONDED)
                                    sortText = "답변 많은 순"
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (surveys == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally)) // 로딩 표시
                } else {
                    LazyColumn {
                        items(surveys ?: emptyList()) { survey ->
                            SurveyItem(survey, navController)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun Point() {
    var checked by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.small_logo2), // 실제 drawable로 교체
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(text = "test@test.com님", fontSize = 30.sp, color = Color.Black, fontWeight = FontWeight.Bold )
                Text(text = "잔여 포인트: 1000", fontSize = 28.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }

        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "구매한 상품 포함", fontSize = 16.sp)
            }
        }
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            ItemRow("item 1", "200 포인트")
            ItemRow("item 4", "400 포인트")
            ItemRow("item 5", "300 포인트")
        }
    }


}

@Composable
fun ItemRow(itemName: String, itemPoints: String) {
    Box(modifier = Modifier.border(1.dp, Color.Gray, RoundedCornerShape(4.dp))){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = itemName, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(150.dp))
            Text(text = itemPoints, fontSize = 16.sp)
        }
    }

}

@Composable
fun Quest(navController: NavHostController) {
    val repository = SurveyRepository()
    val viewModel: SurveyViewModel = viewModel(factory = SurveyModelFactory(repository))
    val surveys by viewModel.surveys.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var sortText by remember { mutableStateOf("최신순") }
    Scaffold(
        content = { paddingValues ->
            Column(modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        sortText,
                        fontSize = 15.sp,
                        fontFamily = NotoSansKr,
                        fontWeight = FontWeight.Bold
                    )
                    Box {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "정렬", tint = Color.Black)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("최신순",fontFamily = NotoSansKr,
                                    fontWeight = FontWeight.Bold) },
                                onClick = {
                                    viewModel.sortSurveys(SortType.LATEST)
                                    sortText = "최신순"
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("좋아요 많은 순", fontFamily = NotoSansKr,
                                    fontWeight = FontWeight.Bold) },
                                onClick = {
                                    viewModel.sortSurveys(SortType.MOST_LIKED)
                                    sortText = "좋아요 많은 순"
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("답변 많은 순", fontFamily = NotoSansKr,
                                    fontWeight = FontWeight.Bold) },
                                onClick = {
                                    viewModel.sortSurveys(SortType.MOST_RESPONDED)
                                    sortText = "답변 많은 순"
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (surveys == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally)) // 로딩 표시
                } else {
                    LazyColumn {
                        items(surveys ?: emptyList()) { survey ->
                            SurveyItem(survey, navController)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SurveyItem(survey: Survey, navController: NavHostController) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Column(modifier = Modifier
            .padding(16.dp)
            .clickable { navController.navigate("SurveyResult") }) {
            Text(text = survey.title, style = MaterialTheme.typography.headlineSmall)
            Text(text = "작성자: ${survey.creatorId}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "좋아요: ${survey.likes}, 답변 수: ${survey.responses}", style = MaterialTheme.typography.bodySmall)
        }
    }
}