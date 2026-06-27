package com.moky.timebattle.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.moky.timebattle.data.AppViewModel
import com.moky.timebattle.data.model.AppState
import com.moky.timebattle.data.model.User
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.moky.timebattle.data.model.formatAsLifeTime
import com.moky.timebattle.data.model.formatCompact
import com.moky.timebattle.ui.components.DynamicClock
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
        var showClockMode by remember { mutableStateOf(false) }
        val context = androidx.compose.ui.platform.LocalContext.current
        val timerTransition = updateTransition(targetState = showClockMode, label = "timerMode")
        val titleAlpha by timerTransition.animateFloat(
            transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
            label = "titleAlpha"
        ) { if (it) 0f else 1f }
        val titleOffsetY by timerTransition.animateDp(
            transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
            label = "titleOffsetY"
        ) { if (it) (-12).dp else 0.dp }
        val timeFontSize by timerTransition.animateInt(
            transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
            label = "timeFontSize"
        ) { if (it) 24 else 48 }
        val timeScale by timerTransition.animateFloat(
            transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
            label = "timeScale"
        ) { if (it) 1f else 1f }
        val timeOffsetY by timerTransition.animateDp(
            transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
            label = "timeOffsetY"
        ) { if (it) 8.dp else 0.dp }
        val clockAlpha by timerTransition.animateFloat(
            transitionSpec = { tween(600, easing = FastOutSlowInEasing) },
            label = "clockAlpha"
        ) { if (it) 1f else 0f }
        val clockScale by timerTransition.animateFloat(
            transitionSpec = { tween(600, easing = FastOutSlowInEasing) },
            label = "clockScale"
        ) { if (it) 1f else 0.75f }
        val clockHeight by timerTransition.animateDp(
            transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
            label = "clockHeight"
        ) { if (it) 180.dp else 0.dp }
        val topSpacerHeight by timerTransition.animateDp(
            transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
            label = "topSpacerHeight"
        ) { if (it) 0.dp else 6.dp }
        val labelHeight by timerTransition.animateDp(
            transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
            label = "labelHeight"
        ) { if (it) 0.dp else 24.dp }
        val subtitleOffsetY by timerTransition.animateDp(
            transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
            label = "subtitleOffsetY"
        ) { if (it) 12.dp else 0.dp }

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
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = {
                        VibrationHelper.vibrate(context, 30)
                        showClockMode = !showClockMode
                    }
                )
                .animateContentSize()
                .padding(vertical = 26.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(labelHeight)
                    .alpha(titleAlpha)
                    .offset { IntOffset(0, titleOffsetY.roundToPx()) },
                contentAlignment = Alignment.Center
            ) {
                SectionLabel(text = "remaining life")
            }
            Spacer(modifier = Modifier.height(topSpacerHeight))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(clockHeight)
                    .alpha(clockAlpha)
                    .scale(clockScale),
                contentAlignment = Alignment.Center
            ) {
                if (clockAlpha > 0f) {
                    DynamicClock(modifier = Modifier.size(180.dp))
                }
            }

            Text(
                text = user.remainingSeconds.formatAsLifeTime(),
                style = TimerDigitsStyle.copy(
                    fontSize = timeFontSize.sp,
                    color = LifeRed
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, timeOffsetY.roundToPx()) }
                    .scale(timeScale)
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
                style = MaterialTheme.typography.labelSmall.copy(color = DimWhite, fontSize = 8.sp),
                modifier = Modifier.offset { IntOffset(0, subtitleOffsetY.roundToPx()) }
            )
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
private fun CheckInLogo(
    progress: Float,
    modifier: Modifier = Modifier,
    baseColor: Color = MutedWhite,
    activeColor: Color = LifeRed
) {
    Canvas(modifier = modifier.size(96.dp)) {
        val sizePx = size.width
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val r1 = sizePx * 0.4375f
        val r2 = sizePx * 0.3125f
        val strokeMain = sizePx * 0.0156f
        val strokeThin = sizePx * 0.0078f
        val dotR = sizePx * 0.026f

        // Tick mark at top of outer circle
        val tickStart = Offset(cx, cy - r1 + strokeMain)
        val tickEnd = Offset(cx, cy - r1 + strokeMain + sizePx * 0.125f)
        val currentTickEnd = Offset(
            tickStart.x + (tickEnd.x - tickStart.x) * progress,
            tickStart.y + (tickEnd.y - tickStart.y) * progress
        )

        // Minute hand (long, pointing up)
        val minuteStart = Offset(cx, cy)
        val minuteEnd = Offset(cx, cy - sizePx * 0.125f)
        val currentMinuteEnd = Offset(
            minuteStart.x + (minuteEnd.x - minuteStart.x) * progress,
            minuteStart.y + (minuteEnd.y - minuteStart.y) * progress
        )

        // Hour hand (short, pointing down-right)
        val hourStart = Offset(cx, cy)
        val hourEnd = Offset(cx + sizePx * 0.125f, cy + sizePx * 0.0625f)
        val currentHourEnd = Offset(
            hourStart.x + (hourEnd.x - hourStart.x) * progress,
            hourStart.y + (hourEnd.y - hourStart.y) * progress
        )

        // Draw base (white) full shapes
        drawCircle(
            color = baseColor,
            radius = r1,
            center = Offset(cx, cy),
            style = Stroke(width = strokeMain)
        )
        drawCircle(
            color = baseColor,
            radius = r2,
            center = Offset(cx, cy),
            style = Stroke(width = strokeThin)
        )
        drawLine(
            color = baseColor,
            start = tickStart,
            end = tickEnd,
            strokeWidth = strokeMain * 0.8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = baseColor,
            start = minuteStart,
            end = minuteEnd,
            strokeWidth = strokeMain * 0.8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = baseColor,
            start = hourStart,
            end = hourEnd,
            strokeWidth = strokeMain * 0.8f,
            cap = StrokeCap.Round
        )
        if (progress < 1f) {
            drawCircle(
                color = baseColor,
                radius = dotR,
                center = Offset(cx, cy)
            )
        }

        // Draw active (red) progress shapes
        val sweep = 360f * progress
        drawArc(
            color = activeColor,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(cx - r1, cy - r1),
            size = androidx.compose.ui.geometry.Size(r1 * 2, r1 * 2),
            style = Stroke(width = strokeMain)
        )
        drawArc(
            color = activeColor,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(cx - r2, cy - r2),
            size = androidx.compose.ui.geometry.Size(r2 * 2, r2 * 2),
            style = Stroke(width = strokeThin)
        )
        if (progress > 0f) {
            drawLine(
                color = activeColor,
                start = tickStart,
                end = currentTickEnd,
                strokeWidth = strokeMain * 0.8f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = activeColor,
                start = minuteStart,
                end = currentMinuteEnd,
                strokeWidth = strokeMain * 0.8f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = activeColor,
                start = hourStart,
                end = currentHourEnd,
                strokeWidth = strokeMain * 0.8f,
                cap = StrokeCap.Round
            )
        }
        if (progress >= 1f) {
            drawCircle(
                color = activeColor,
                radius = dotR,
                center = Offset(cx, cy)
            )
        }
    }
}

@Composable
private fun SignInTab(
    lastCheckInDate: String?,
    onCheckIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val today = remember { LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) }
    val checkedInToday = lastCheckInDate == today
    val pressProgress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isCompleted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CarbonGrey, RoundedCornerShape(14.dp))
            .border(1.dp, StrokeLight, RoundedCornerShape(14.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        if (checkedInToday || isCompleted) return@detectTapGestures

                        // Continuous vibration while pressed
                        val vibrationJob = scope.launch {
                            while (isActive) {
                                VibrationHelper.vibrate(context, 50)
                                kotlinx.coroutines.delay(60)
                            }
                        }

                        // Start progress animation; auto-complete when filled
                        val progressJob = scope.launch {
                            pressProgress.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(durationMillis = 1200)
                            )
                            vibrationJob.cancel()
                            if (!isCompleted) {
                                isCompleted = true
                                onCheckIn()
                            }
                        }

                        tryAwaitRelease()

                        vibrationJob.cancel()
                        progressJob.cancel()

                        if (!isCompleted) {
                            scope.launch {
                                pressProgress.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 200)
                                )
                            }
                        }
                    }
                )
            }
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CheckInLogo(
            progress = if (checkedInToday) 1f else pressProgress.value,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = when {
                checkedInToday -> "今日已签到"
                isCompleted -> "签到成功"
                else -> "长按签到"
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (checkedInToday || isCompleted) LifeRed else MutedWhite,
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
