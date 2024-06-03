package com.example.ratemate.home

import android.graphics.Paint
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratemate.R
import com.example.ratemate.data.Comment
import com.example.ratemate.data.Dislike
import com.example.ratemate.data.Like
import com.example.ratemate.data.User
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import java.util.Date
import java.util.UUID

@Composable
fun SurveyResultScreen() {

    val surveyResult = getExampleSurveyResult()
    val user = getExampleUser()
    val content = getExampleResultContent()
    val userChoice = getExampleUserChoice()

    var commentList by remember { mutableStateOf(surveyResult.comments.values.toList()) }
    var sortComment by remember { mutableStateOf("인기순") }
    var likes by remember { mutableIntStateOf(surveyResult.likes.count) }
    var numberOfComment by remember { mutableIntStateOf(surveyResult.comments.size) }
    val context = LocalContext.current

    //해당 유저가 좋아요를 눌렀는지 확인
    var isLiked by rememberSaveable {
        mutableStateOf(
            surveyResult.likes.usersWhoLiked.find { it == user.userId } != null
        )
    }

    //좋아요를 눌렀을때 실행될 함수
    val clickLikes = {
        if (isLiked) {
            isLiked = false
            likes -= 1
        } else {
            isLiked = true
            likes += 1
        }
    }

    //인기순 버튼 클릭시 실행될 함수
    val clickSortByLikes = {
        sortComment = "인기순"
        commentList = commentList.sortedByDescending { it.like.count - it.dislike.count }
    }

    //최신순 버튼 클릭시 실행될 함수
    val clickSortByDate = {
        sortComment = "최신순"
        commentList = commentList.sortedBy { it.createdDate }
    }

    //댓글 추가 함수
    @Composable
    fun addComment(comment: String) {
        val calendar = Calendar.getInstance()
        val newComment = Comment(
            commentId = UUID.randomUUID().toString(),
            userId = user.email,
            text = comment,
            createdDate = calendar.time.toString(),
            profileImage = painterResource(id = user.profileImage.toInt()),
            like = Like(),
            dislike = Dislike()
        )

        commentList = commentList + newComment

        if (sortComment == "인기순") {
            clickSortByLikes()
        } else {
            clickSortByDate()
        }

    }


    //댓글 입력 버튼 클릭시 실행될 함수
    val onClickSend = @Composable { comment: String ->
        if (comment == "") {
            Toast.makeText(context, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show()
        } else {
            addComment(comment)
            Toast.makeText(context, "댓글이 등록되었습니다", Toast.LENGTH_SHORT).show()
        }
    }


    //좋아요 눌렀을 때 동기화
    LaunchedEffect(key1 = likes, key2 = isLiked) {
        if (isLiked) {
            surveyResult.likes.usersWhoLiked.add(user.userId)
            surveyResult.likes.count = likes
        } else {
            surveyResult.likes.usersWhoLiked.remove(user.userId)
            surveyResult.likes.count = likes
        }
    }


    //댓글 수 동기화
    LaunchedEffect(key1 = commentList) {
        numberOfComment = commentList.size

    }


    //전체화면 Column
    Column (
        modifier = Modifier.fillMaxSize()
    ){


        //제목, 작성자
        val modifier1 = Modifier.weight(1f)
        ShowTitle(title = surveyResult.title, writer = surveyResult.creatorId, modifier = modifier1)

        //내용
        val modifier2 = Modifier.weight(7f)
        ShowMainContent(content = content, userChoice = userChoice, modifier = modifier2)

        //좋아요, 댓글 수, 댓글 정렬 방법 버튼
        val modifier3 = Modifier.height(40.dp)
        ShowCounts(isLiked = isLiked, like = likes,
            numberOfComment = numberOfComment, modifier = modifier3,
            clickLikes = clickLikes, clickSortByLikes = clickSortByLikes,
            clickSortByDate = clickSortByDate)

        //댓글 리스트
        Divider()
        val modifier5 = Modifier.weight(5f)
        ShowComments(user, comments = commentList,
            modifier = modifier5)

        //댓글 입력창
        val modifier6 = Modifier.height(70.dp)
        ShowCommentInput(modifier = modifier6, onClickSend = onClickSend)


    }

}

@Composable
fun getExampleSurveyResult(): SurveyResultEX {
    val calendar1 = Calendar.getInstance()
    calendar1.set(2022, 10, 1)
    val commentEX1 = Comment(
        commentId = UUID.randomUUID().toString(),
        userId = "user1@gmail.com",
        text = "내용1",
        createdDate = calendar1.time.toString(),
        profileImage = painterResource(id = R.drawable.image1),
        like = Like(),
        dislike = Dislike()
    )
    commentEX1.like.usersWhoLiked = mutableListOf("1", "2", "3", "4", "5")
    commentEX1.dislike.usersWhoDisliked = mutableListOf("1", "2", "3")
    commentEX1.like.count = 5
    commentEX1.dislike.count = 3

    val calendar2 = Calendar.getInstance()
    calendar2.set(2023, 10, 2)
    val commentEX2 = Comment(
        commentId = UUID.randomUUID().toString(),
        userId = "user2@gmail.com",
        text = "내용2",
        createdDate = calendar2.time.toString(),
        profileImage = painterResource(id = R.drawable.image2),
        like = Like(),
        dislike = Dislike()
    )

    commentEX2.like.usersWhoLiked = mutableListOf("1", "2", "3", "4", "5")
    commentEX2.dislike.usersWhoDisliked = mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    commentEX2.like.count = 5
    commentEX2.dislike.count = 10

    val surveyResultEx = SurveyResultEX(
        surveyId = UUID.randomUUID().toString(),
        creatorId = "user3@gmail.com",
        title = "제목",
        content = "내용",
        likes = Like(),
        createdDate = Date().toString(),
        modifiedDate = Date().toString(),
        status = "active",
        questions = emptyMap(),
        responses = emptyMap(),
        comments = mapOf(
            commentEX1.commentId to commentEX1,
            commentEX2.commentId to commentEX2
        )
    )

    surveyResultEx.likes.usersWhoLiked = mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    surveyResultEx.likes.count = 10


    return surveyResultEx
}

@Composable
fun getExampleUser(): User {
    val user = User()
    val me = FirebaseAuth.getInstance().currentUser

    user.userId = me?.uid ?: "null"
    user.email = me?.email ?: "null"
    user.points = 1000
    user.createdDate = Date().toString()
    user.modifiedDate = Date().toString()
    user.status = "active"
    user.profileImage = R.drawable.profile.toString()
    user.surveysCreated = mutableListOf("1", "2", "3")
    user.surveysParticipated = mutableListOf("4", "5", "6")
    user.PurchaseList = mutableListOf()

    return user
}

@Composable
fun getExampleResultContent() : List<ResultContent>{
    val resultContent = mutableListOf<ResultContent>()

    resultContent.add(ResultContent("question1", listOf("answer1", "answer2", "answer3", "answer4"), listOf(10, 20, 30, 40)))
    resultContent.add(ResultContent("question2", listOf("answer2_1", "answer2_2", "answer2_3"), listOf(30, 10, 40)))
    resultContent.add(ResultContent("question3", listOf("answer3_1", "answer3_2", "answer3_3", "answer3_4" , "answer3_5"), listOf(100, 20, 30, 0, 50)))
    resultContent.add(ResultContent("question4", listOf("answer4_1", "answer4_2", "answer4_3", "answer4_4"), listOf(1, 2, 300, 2)))

    return resultContent
}

fun getExampleUserChoice() : List<List<Int>>{
    val userChoice = mutableListOf<List<Int>>()

    userChoice.add(listOf(2))
    userChoice.add(listOf(1,2))
    userChoice.add(listOf(0,1))
    userChoice.add(listOf(3))

    return userChoice
}

@Composable
fun ShowTitle(title: String, writer: String, modifier: Modifier) {
    Row(modifier = modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        //제목
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 5.dp, top = 5.dp, bottom = 5.dp, end = 5.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        //작성자
        Text(
            text = writer,
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(start = 5.dp, top = 5.dp, bottom = 5.dp, end = 5.dp)
        )

    }
}

@Composable
fun ShowMainContent(content : List<ResultContent>, userChoice : List<List<Int>>, modifier : Modifier){

    val contentSize = content.size
    var currentContent by rememberSaveable { mutableIntStateOf(0) }

    Box(modifier = modifier){
        //회색 배경
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray.copy(alpha = 0.5f))
            .padding(start = 5.dp, top = 5.dp, bottom = 5.dp, end = 5.dp)
        )

        //내용
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ){

            //질문 + 이전, 다음 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //질문
                Text(
                    text = content[currentContent].question,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(8f)
                )

                //이전 버튼
                Button(
                    onClick = {
                        if (currentContent > 0) {
                            currentContent -= 1
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentContent == 0) Color.Gray else Color.Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(8.dp))
                //다음 버튼
                Button(
                    onClick = {
                        if (currentContent < contentSize - 1) {
                            currentContent += 1
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentContent == contentSize - 1) Color.Gray else Color.Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "next",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

            }

            //설문 결과
            ShowResult(result = content[currentContent], userChoice = userChoice[currentContent], modifier = Modifier.weight(8f))

        }
    }
}

@Composable
fun ShowResult(result : ResultContent, userChoice : List<Int>, modifier: Modifier){
    Column(
        modifier = modifier
            .fillMaxWidth()
    ){

        //설문 결과 차트
        SelectionPercentageChart(answers = result.answer,choices = result.answerCount, userChoices = userChoice )

    }
}


@Composable
fun SelectionPercentageChart(answers: List<String>, choices: List<Int>, userChoices: List<Int>) {
    val total = choices.sum()
    val percentages = choices.map { it.toFloat() / total * 100 }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 차트
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .weight(3f)) {
            val barWidth = size.width / (choices.size * 2)
            percentages.forEachIndexed { index, percentage ->
                val barHeight = size.height * (percentage / 100)
                drawRect(
                    color = if (index in userChoices) Color.Green else Color.Gray,
                    topLeft = Offset(index * 2 * barWidth + barWidth / 2, size.height - barHeight),
                    size = Size(barWidth, barHeight)
                )
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        "${percentage.toInt()}%",
                        index * 2 * barWidth + barWidth,
                        size.height - barHeight - 10,
                        Paint().apply {
                            color = android.graphics.Color.BLACK
                            textAlign = Paint.Align.CENTER
                            textSize = 40f
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))
        // 답변 리스트
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            answers.forEach { answer ->
                Text(
                    text = answer,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }


    }
}

@Composable
fun ShowCounts(
    isLiked: Boolean,
    like: Int,
    numberOfComment: Int,
    modifier: Modifier,
    clickLikes: () -> Unit,
    clickSortByLikes: () -> Unit,
    clickSortByDate: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    val selectedText by remember { mutableStateOf("댓글 정렬") }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        //좋아요 버튼
        Icon(
            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Likes",
            modifier = Modifier.clickable {
                clickLikes()
            }
        )

        //좋아요 수
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$like likes")


        //댓글 수
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Comment,
            contentDescription = "Comments"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$numberOfComment comments")



        Spacer(modifier = Modifier.weight(1f))
        //인기순, 최신순 버튼
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier.fillMaxSize()
        ){
            Button(
                onClick = {expanded = true},
                modifier = Modifier
                    .background(Color.Gray.copy(alpha = 0.5f))
                    .clip(RoundedCornerShape(6.dp))

                ,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = selectedText,
                    modifier = Modifier.safeContentPadding()
                    )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ){

                DropdownMenuItem(text = { Text(text = "인기순") },
                    onClick = { clickSortByLikes(); expanded = false})

                DropdownMenuItem(text = { Text(text = "최신순") },
                    onClick = { clickSortByDate(); expanded = false})


            }

        }


    }
}


@Composable
fun ShowComments(user : User, comments: List<Comment>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier
    ) {
        items(comments) { comment ->

            var like by rememberSaveable { mutableIntStateOf(comment.like.count) }
            var dislike by rememberSaveable { mutableIntStateOf(comment.dislike.count) }
            var isLiked by rememberSaveable {
                mutableStateOf(
                    comment.like.usersWhoLiked.find { it == user.userId } != null
                )
            }
            var isDisliked by rememberSaveable {
                mutableStateOf(
                    comment.dislike.usersWhoDisliked.find { it == user.userId } != null
                )
            }

            LaunchedEffect(comments) {
                like = comment.like.count
                dislike = comment.dislike.count
                isLiked = comment.like.usersWhoLiked.find { it == user.userId } != null
                isDisliked = comment.dislike.usersWhoDisliked.find { it == user.userId } != null
            }

            LaunchedEffect(key1 = isLiked, key2 = isDisliked) {
                comment.like.count = like
                comment.dislike.count = dislike
                if (isLiked) {
                    comment.like.usersWhoLiked.add(user.userId)
                } else {
                    comment.like.usersWhoLiked.remove(user.userId)
                }

                if (isDisliked) {
                    comment.dislike.usersWhoDisliked.add(user.userId)
                } else {
                    comment.dislike.usersWhoDisliked.remove(user.userId)
                }
            }

            //좋아요, 싫어요 버튼 클릭시 실행될 함수
            val clickLike = {
                if (!isLiked && !isDisliked) {
                    isLiked = true
                    like += 1

                } else if (isLiked) {
                    isLiked = false
                    like -= 1
                } else {
                    isDisliked = false
                    dislike -= 1
                    isLiked = true
                    like += 1
                }
            }

            val clickDislike = {
                if (!isLiked && !isDisliked) {
                    isDisliked = true
                    dislike += 1
                } else if (isDisliked) {
                    isDisliked = false
                    dislike -= 1
                } else {
                    isLiked = false
                    like -= 1
                    isDisliked = true
                    dislike += 1
                }
            }

            //댓글
            ShowComment(
                comment = comment,
                like = like,
                dislike = dislike,
                isLiked = isLiked,
                isDisliked = isDisliked,
                clickLike = clickLike,
                clickDislike = clickDislike
            )

        }
    }
}

@Composable
fun ShowComment(
    comment: Comment,
    like: Int = comment.like.count,
    dislike: Int = comment.dislike.count,
    isLiked: Boolean,
    isDisliked: Boolean,
    clickLike: () -> Unit,
    clickDislike: () -> Unit
) {

    Column {
        // 댓글 내용
        Row(
            verticalAlignment = Alignment.Top
        ) {
            // 프로필 이미지
            Image(
                painter = comment.profileImage!!,
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(30.dp))
            )


            // 댓글 내용
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = comment.userId, fontWeight = FontWeight.Bold)
                Text(text = comment.text)
                Text(text = comment.createdDate, color = Color.Gray, fontSize = 15.sp)
            }

        }


        //댓글 -> 좋아요, 싫어요
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 좋아요
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Filled.ThumbUpOffAlt,
                contentDescription = "Likes",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        clickLike()
                    }
            )

            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "$like likes")


            // 싫어요
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = if (isDisliked) Icons.Filled.ThumbDown else Icons.Default.ThumbDownOffAlt,
                contentDescription = "Dislikes",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        clickDislike()
                    })

            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$dislike dislikes"
            )

        }
    }
}

@Composable
fun ShowCommentInput(modifier: Modifier, onClickSend: @Composable (String) -> Unit) {

    var userComment by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var sendComment by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = modifier.padding(start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {

        //댓글 입력창
        TextField(
            value = userComment,
            onValueChange = { userComment = it },
            label = { Text("Comment") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (userComment != "") {
                        sendComment = true
                    }

                    else {
                        focusManager.clearFocus()

                    }
                }
            ),
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        )

        //전송 버튼
        Button(
            onClick = {
                sendComment = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        if (sendComment) {
            onClickSend(userComment)
            userComment = ""
            sendComment = false
        }

    }
}