package com.moky.timebattle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moky.timebattle.data.AppViewModel
import com.moky.timebattle.data.model.NotificationItem
import com.moky.timebattle.ui.components.icons.backIcon
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.StrokeLight
import com.moky.timebattle.ui.theme.TimebattleTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    NotificationsContent(
        notifications = state.notifications,
        onBack = onBack,
        onMarkRead = { viewModel.markNotificationRead(it) },
        modifier = modifier
    )
}

@Composable
private fun NotificationsContent(
    notifications: List<NotificationItem>,
    onBack: () -> Unit,
    onMarkRead: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssBlack)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .border(1.dp, StrokeLight, RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = onBack
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = backIcon(),
                    contentDescription = "Back",
                    tint = MutedWhite,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = "通知",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无通知",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DimWhite)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                notifications.forEach { notification ->
                    NotificationRow(
                        notification = notification,
                        onClick = { onMarkRead(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    val borderColor = if (notification.isRead) StrokeLight else LifeRed.copy(alpha = 0.5f)
    val titleColor = if (notification.isRead) MutedWhite else androidx.compose.ui.graphics.Color.White

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = titleColor,
                    fontSize = 14.sp
                )
            )
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(LifeRed, RoundedCornerShape(3.dp))
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = notification.message,
            style = MaterialTheme.typography.bodySmall.copy(color = DimWhite)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = formatTimestamp(notification.timestamp),
            style = MaterialTheme.typography.labelSmall.copy(color = DimWhite, fontSize = 8.sp)
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
private fun NotificationsContentPreview() {
    TimebattleTheme {
        NotificationsContent(
            notifications = listOf(
                NotificationItem(title = "签到成功", message = "+30m 已到账"),
                NotificationItem(title = "任务被接取", message = "你发布的任务已被接取", isRead = true)
            ),
            onBack = {},
            onMarkRead = {}
        )
    }
}
