package com.example.storyefun.ui.screens

import com.example.storyefun.ui.components.CommentSection
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.storyefun.R
import com.example.storyefun.data.models.Book
import com.example.storyefun.viewModel.BookViewModel
import com.example.storyefun.ui.components.*
import com.example.storyefun.ui.theme.AppColors
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.viewModel.ThemeViewModel
import coil.compose.AsyncImage
import com.example.storyefun.data.models.Chapter
import com.example.storyefun.viewModel.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun BookDetailScreen(navController: NavController, bookId : String, themeViewModel: ThemeViewModel, viewModel: BookViewModel = viewModel(), userViewModel: UserViewModel = viewModel()) {
    var theme = LocalAppColors.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val isDarkMode by themeViewModel.isDarkTheme.collectAsState()
    val book by viewModel.book.observeAsState()




    LaunchedEffect(bookId) {
        viewModel.fetchBook(bookId)
        Log.e("info of lauched book: ", book.toString())
    }





    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Image
        if (!isDarkMode) {


        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(theme.backgroundColor)
            )
        }

        if (book != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    var text by remember { mutableStateOf("") }
                    var active by remember { mutableStateOf(false) }
//                    Header(text, active, onQueryChange = { text = it }, onActiveChange = { active = it }, navController)
                }

                item { MangaInfo(theme, book!!, navController, userViewModel) }  // safe to use !! here after null check

                item {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = theme.backgroundContrast2
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Thông tin", color = theme.backgroundColor) }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("Chapter", color = theme.backgroundColor) }
                        )
                    }
                }

                item {
                    when (selectedTabIndex) {
                        0 -> InformationSection(navController, theme, book!!)
                        1 -> ChapterListSection(theme, book!!, navController)
                    }
                    CommentSection(theme, book!!.id, book!!.comments)
                }
            }
        } else {
            // You can show loading or empty state here
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = theme.textPrimary)
            }
        }

    }
}
@Composable
fun MangaInfo(theme: AppColors, book: Book, navController: NavController, viewModel: UserViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLiked by remember { mutableStateOf(false) }
    var isFollowed by remember { mutableStateOf(false) }

    LaunchedEffect(book.id) {
        isLiked = withContext(Dispatchers.IO) { viewModel.isLikedBook(book.id) }
        isFollowed = withContext(Dispatchers.IO) { viewModel.isFollowedBook(book.id) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // Banner with gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        ) {
            AsyncImage(
                model = book.posterUrl,
                contentDescription = "Banner",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.error),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 40.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = theme.backgroundColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Poster
                    AsyncImage(
                        model = book.imageUrl,
                        contentDescription = "Poster",
                        contentScale = ContentScale.FillHeight,
                        placeholder = painterResource(R.drawable.placeholder),
                        error = painterResource(R.drawable.error),
                        modifier = Modifier
                            .width(100.dp)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = book.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = book.author,
                            fontSize = 14.sp,
                            color = theme.textSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Row(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            /*StatItem(
                                icon = R.drawable.ic_views,
                                text = "${book.views}",
                                color = theme.textSecondary,
                                onClick = null
                            )
*/
                            StatItem(
                                icon = R.drawable.ic_follows,
                                text = if (isFollowed) "" + book.follows else "Follow " + book.follows,
                                color = if (isFollowed) Color.Red else theme.textSecondary
                            ) {
                                scope.launch {
                                    if (isFollowed)
                                    {
                                        viewModel.unfollowingBook(book.id)
                                        book.follows--
                                        isFollowed = false
                                    }
                                    else
                                    {
                                        viewModel.followingBook(book.id)
                                        book.follows++
                                        isFollowed = true
                                    }
                                }
                            }

                            StatItem(
                                icon = R.drawable.ic_likes,
                                text = if (isLiked) "" + book.likes else "Like " + book.likes,
                                color = if (isLiked) Color.Red else theme.textSecondary
                            ) {
                                scope.launch {
                                    if (isLiked)
                                    {
                                        viewModel.unlikingBook(book.id)
                                        book.likes--
                                        isLiked = false
                                    }
                                    else
                                    {
                                        viewModel.likingBook(book.id)
                                        book.likes++
                                        isLiked = true
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                navController.navigate("reading/${book.id}/1/1")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Bắt đầu đọc",
                                fontSize = 16.sp,
                                color = theme.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenreTag(text: String, theme: AppColors) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Gray.copy(alpha = 0.2f),
        modifier = Modifier
            .wrapContentSize()
            .padding(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = theme.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InformationSection(navController: NavController, theme: AppColors, book: Book) {
    val genres = book.category

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Genres Section
        Text(
            text = "Thể loại",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            genres.forEach { genre ->
                GenreTag(text = genre.name, theme = theme)
            }
        }


        // Description Section
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Giới thiệu",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        var expanded by remember { mutableStateOf(false) }
        val maxLines = if (expanded) Int.MAX_VALUE else 3

        Text(
            text = book.description,
            fontSize = 14.sp,
            color = theme.textSecondary,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        if (book.description.length > 100) {
            Text(
                text = if (expanded) "Thu gọn" else "Xem thêm",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Red,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(top = 4.dp)
            )
        }
    }

}

@Composable
fun StatItem(
    icon: Int,
    text: String,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    val modifier = Modifier
        .padding(horizontal = 8.dp)
        .then(
            if (onClick != null) Modifier.clickable { onClick() }
            else Modifier
        )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp) // Tăng kích thước icon
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ChapterListSection(theme: AppColors, book: Book, navController: NavController) {
    var showUnlockDialog by remember { mutableStateOf(false) }
    var selectedChapter: Chapter? by remember { mutableStateOf(null) }
    var selectedVolumeOrder by remember { mutableStateOf(0) }
    var coinBalance by remember { mutableStateOf<Int?>(null) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current // Lấy context để sử dụng Toast

    LaunchedEffect(uid) {
        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    coinBalance = document.getLong("coin")?.toInt() ?: 0
                }
                .addOnFailureListener {
                    coinBalance = 0
                }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Volumes (${book.volume.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
        )

        Text(
            text = "Cập nhật mới nhất 29/02/2024",
            fontSize = 14.sp,
            color = theme.textSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        book.volume.forEach { volume ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = theme.backgroundColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tập ${volume.title}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    volume.chapters.forEach { chapter ->
                        val isLocked = chapter.locked == true
                        val chapterModifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (!isLocked) {
                                    // Hiển thị Toast với volumeId và chapterId
                                    Toast.makeText(
                                        context,
                                        "Volume ID: ${volume.id}, Chapter ID: ${chapter.id}",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    // Chuyển đến trang đọc chapter
                                    navController.navigate("reading/${book.id}/${volume.order}/${chapter.order}")
                                } else {
                                    selectedChapter = chapter
                                    selectedVolumeOrder = volume.order.toInt()
                                    showUnlockDialog = true
                                }
                            }
                            .padding(vertical = 6.dp)

                        Row(
                            modifier = chapterModifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isLocked) R.drawable.locked else R.drawable.ic_chapter
                                ),
                                contentDescription = null,
                                tint = if (isLocked) Color.Gray else theme.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Chương ${chapter.title}",
                                fontSize = 14.sp,
                                color = if (isLocked) Color.Gray else theme.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Mở khóa chương
        if (showUnlockDialog && selectedChapter != null && uid != null) {
            AlertDialog(
                onDismissRequest = { showUnlockDialog = false },
                title = { Text("Chương bị khóa") },
                text = {
                    Text("Bạn đang có $coinBalance coin. Mở khóa chương này với 20 coin?")
                },
                confirmButton = {
                    TextButton(onClick = {
                        val chapter = selectedChapter ?: return@TextButton
                        val userDocRef = FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)

                        if (coinBalance != null && coinBalance!! >= 20) {
                            val newCoin = coinBalance!! - 20

                            // Trừ coin người dùng
                            userDocRef.update("coin", newCoin)
                                .addOnSuccessListener {
                                    coinBalance = newCoin

                                    // Truy vấn Firestore để mở khóa chương
                                    FirebaseFirestore.getInstance()
                                        .collection("books")
                                        .document(book.id)
                                        .collection("volumes")
                                        .whereEqualTo("order", selectedVolumeOrder)
                                        .get()
                                        .addOnSuccessListener { volumeQuery ->
                                            if (!volumeQuery.isEmpty) {
                                                val volumeDoc = volumeQuery.documents.first()

                                                volumeDoc.reference
                                                    .collection("chapters")
                                                    .whereEqualTo("order", chapter.order)
                                                    .get()
                                                    .addOnSuccessListener { chapterQuery ->
                                                        if (!chapterQuery.isEmpty) {
                                                            val chapterDoc = chapterQuery.documents.first()

                                                            chapterDoc.reference.update("locked", false)
                                                                .addOnSuccessListener {
                                                                    // Hiển thị Toast khi mở khóa thành công
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Chương đã được mở khóa",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()

                                                                    // Sau khi mở khóa thành công, chuyển đến trang đọc chapter
                                                                    navController.navigate("reading/${book.id}/${selectedVolumeOrder}/${chapter.order}")
                                                                    showUnlockDialog = false
                                                                }
                                                                .addOnFailureListener { e ->
                                                                    // Thông báo lỗi nếu không cập nhật được
                                                                    Log.e("Firestore", "Lỗi khi mở khóa chương: ${e.message}")
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Không thể mở khóa chương",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                }
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Không tìm thấy chương",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Không tìm thấy tập",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                }
                        } else {
                            // Thiếu coin
                            showUnlockDialog = false
                            navController.navigate("desposite")
                        }
                    }) {
                        Text("Mở khóa")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUnlockDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}



