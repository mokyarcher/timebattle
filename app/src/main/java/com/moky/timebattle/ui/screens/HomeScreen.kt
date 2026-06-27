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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moky.timebattle.data.AppViewModel
import com.moky.timebattle.data.model.AppState
import com.moky.timebattle.data.model.User
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.moky.timebattle.data.model.formatAsLifeTime
import com.moky.timebattle.data.model.formatCompact
import com.moky.timebattle.ui.components.SectionLabel
import com.moky.timebattle.ui.components.TimerBar
import com.moky.timebattle.ui.components.icons.bellIcon
import com.moky.timebattle.ui.components.icons.groupIcon
import com.moky.timebattle.ui.components.icons.logoIcon
import com.moky.timebattle.ui.components.icons.syncIcon
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.CarbonGrey
import com.moky.timebattle.ui.theme.DeepGrey
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.StrokeLight
import com.moky.timebattle.ui.theme.StrokeRed
import com.moky.timebattle.ui.theme.TimebattleTheme
import com.moky.timebattle.ui.theme.TimerDigitsStyle
import com.moky.timebattle.util.VibrationHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val tabs = listOf("签到", "同步", "联盟")

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onShowMessage: (String) -> Unit,
    onNavigateToNotifications: () -> Unit,
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
        onNavigateToNotifications = onNavigateToNotifications,
        modifier = modifier
    )
}

@Composable
private fun HomeContent(
    state: AppState,
    onCheckIn: () -> Unit,
    onNavigateToNotifications: () -> Unit,
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

    var selectedTab by remember { mutableStateOf(0) }

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
                        colors = listOf(LifeRed.copy(alpha = 0.03f), Color.Transparent),
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

        // Tab bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .background(DeepGrey, RoundedCornerShape(14.dp))
                .border(1.dp, StrokeLight, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, title ->
                val selected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(if (selected) AbyssBlack else Color.Transparent)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = { selectedTab = index }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (selected) LifeRed else MutedWhite,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }

        // Tab content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            when (selectedTab) {
                0 -> SignInTab(
                    lastCheckInDate = state.lastCheckInDate,
                    onCheckIn = onCheckIn
                )
                1 -> SyncTab()
                2 -> AllianceTab()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SignInTab(
    lastCheckInDate: String?,
    onCheckIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) }
    val checkedInToday = lastCheckInDate == today

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CarbonGrey, RoundedCornerShape(14.dp))
            .border(1.dp, StrokeLight, RoundedCornerShape(14.dp))
            .clickable(
                enabled = !checkedInToday,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onCheckIn
            )
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = logoIcon(size = 96f),
            contentDescription = "Check In",
            tint = if (checkedInToday) MutedWhite else LifeRed,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (checkedInToday) "今日已签到" else "点击签到",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (checkedInToday) DimWhite else LifeRed,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
private fun SyncTab(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CarbonGrey, RoundedCornerShape(14.dp))
            .border(1.dp, StrokeLight, RoundedCornerShape(14.dp))
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = syncIcon(),
            contentDescription = "Sync",
            tint = LifeRed,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "同步功能即将开放",
            style = MaterialTheme.typography.bodyMedium.copy(color = MutedWhite)
        )
    }
}

@Composable
private fun AllianceTab(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CarbonGrey, RoundedCornerShape(14.dp))
            .border(1.dp, StrokeLight, RoundedCornerShape(14.dp))
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = groupIcon(),
            contentDescription = "Alliance",
            tint = LifeRed,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "联盟功能即将开放",
            style = MaterialTheme.typography.bodyMedium.copy(color = MutedWhite)
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
            onNavigateToNotifications = {}
        )
    }
}
