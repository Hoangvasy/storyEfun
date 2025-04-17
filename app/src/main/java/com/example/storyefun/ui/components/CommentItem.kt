import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyefun.R
import com.example.storyefun.ui.theme.AppColors
import androidx.compose.foundation.shape.CircleShape
@Composable
fun CommentItem(username: String, date: String, comment: String, theme: AppColors) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_user),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            colorFilter = ColorFilter.tint(theme.textPrimary)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = username,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = theme.textPrimary
                )
                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = theme.textSecondary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment,
                fontSize = 14.sp,
                color = theme.textPrimary
            )
        }
    }
}
