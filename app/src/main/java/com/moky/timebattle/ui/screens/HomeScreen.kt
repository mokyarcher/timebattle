package com.moky.timebattle.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moky.timebattle.data.AppViewModel
import com.moky.timebattle.data.model.AppState
import com.moky.timebattle.data.model.TaskStatus
import com.moky.timebattle.data.model.User
import com.moky.timebattle.data.model.formatAsLifeTime
import com.moky.timebattle.data.model.formatCompact
import com.moky.timebattle.data.model.formatReward
import com.moky.timebattle.ui.components.InProgressItem
import com.moky.timebattle.ui.components.SectionLabel
import com.moky.timebattle.ui.components.TimerBar
import com.moky.timebattle.ui.components.icons.bellIcon
import com.moky.timebattle.ui.components.icons.groupIcon
import com.moky.timebattle.ui.components.icons.syncIcon
import com.moky.timebattle.ui.components.icons.userIcon
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.DeepGrey
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.StrokeRed
import com.moky.timebattle.ui.theme.TimebattleTheme
import com.moky.timebattle.ui.theme.TimerDigitsStyle
import com.moky.timebattle.util.VibrationHelper

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onShowMessage: (String) -> Unit,
    onTaskClick: (String) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAlliance: () -> Unit,
    onNavigateToSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val state by viewModel.state.collectAsState()
    HomeContent(
        state = state,
        onCheckIn = {
            if (viewModel.checkIn()) {
                VibrationHelper.vibrateSuccess(context)
                onShowMessage("签到成功，生命时间已到账")
            } else {
                VibrationHelper.vibrateWarning(context)
                onShowMessage("今日已签到")
            }
        },
        onTaskClick = { taskId ->
            viewModel.completeTask(taskId)
            VibrationHelper.vibrateSuccess(context)
            onShowMessage("任务完成，奖励已到账")
        },
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToAlliance = onNavigateToAlliance,
        onNavigateToSync = onNavigateToSync,
        modifier = modifier
    )
}

@Composable
private fun HomeContent(
    state: AppState,
    onCheckIn: () -> Unit,
    onTaskClick: (String) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAlliance: () -> Unit,
    onNavigateToSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user = state.user
    val secondsInMinute = (user.remainingSeconds % 60).toInt()
    val targetProgress = 1f - (secondsInMinute / 60f)
    val progress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "timerProgress"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(1.dp, StrokeRed, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = userIcon(),
                    contentDescription = "Avatar",
                    tint = LifeRed,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = "管理局",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                    fontSize = 16.sp
                )
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = onNavigateToNotifications
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = bellIcon(),
                    contentDescription = "Notifications",
                    tint = MutedWhite,
                    modifier = Modifier.size(18.dp)
                )
                if (state.notifications.any { !it.isRead }) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(LifeRed, RoundedCornerShape(2.5.dp))
                            .align(Alignment.TopEnd)
                            .padding(end = 4.dp, top = 4.dp)
                    )
                }
            }
        }

        // Timer card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .border(1.dp, StrokeRed, RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(LifeRed.copy(alpha = 0.03f), androidx.compose.ui.graphics.Color.Transparent),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    ),
                    RoundedCornerShape(18.dp)
                )
                .padding(vertical = 26.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionLabel(text = "remaining life")
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = user.remainingSeconds.formatAsLifeTime(),
                style = TimerDigitsStyle,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        animationMode = androidx.compose.foundation.MarqueeAnimationMode.Immediately,
                        velocity = 38.dp,
                        spacing = androidx.compose.foundation.MarqueeSpacing(24.dp)
                    )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "−1/min · today consumed ${(24 * 3600 - user.remainingSeconds.coerceAtMost(24 * 3600)).formatCompact()}",
                style = MaterialTheme.typography.labelSmall.copy(color = DimWhite, fontSize = 8.sp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            TimerBar(progress = progress)
        }

        // Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .background(DeepGrey, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionItem(
                modifier = Modifier.weight(1f),
                icon = { Icon(userIcon(), null, tint = MutedWhite, modifier = Modifier.size(20.dp)) },
                label = "签到",
                onClick = onCheckIn
            )
            ActionItem(
                modifier = Modifier.weight(1f),
                icon = { Icon(syncIcon(), null, tint = MutedWhite, modifier = Modifier.size(20.dp)) },
                label = "同步",
                onClick = onNavigateToSync
            )
            ActionItem(
                modifier = Modifier.weight(1f),
                icon = { Icon(groupIcon(), null, tint = MutedWhite, modifier = Modifier.size(20.dp)) },
                label = "联盟",
                onClick = onNavigateToAlliance
            )
        }

        // In progress
        SectionLabel(
            text = "in progress",
            modifier = Modifier.padding(top = 22.dp, bottom = 8.dp)
        )
        val inProgressTasks = state.tasks.filter { it.status == TaskStatus.IN_PROGRESS }
        if (inProgressTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无进行中的任务",
                    style = MaterialTheme.typography.bodySmall.copy(color = DimWhite)
                )
            }
        } else {
            Column {
                inProgressTasks.forEach { task ->
                    InProgressItem(
                        title = task.title,
                        reward = task.rewardSeconds.formatReward(),
                        isCompleted = false,
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionItem(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = DimWhite,
                fontSize = 9.sp,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    TimebattleTheme {
        HomeContent(
            state = AppState(user = User(isLoggedIn = true)),
            onCheckIn = {},
            onTaskClick = {},
            onNavigateToNotifications = {},
            onNavigateToAlliance = {},
            onNavigateToSync = {}
        )
    }
}
