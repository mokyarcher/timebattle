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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moky.timebattle.data.AppViewModel
import com.moky.timebattle.data.model.TaskItem
import com.moky.timebattle.data.model.TaskStatus
import com.moky.timebattle.data.model.TaskType
import com.moky.timebattle.ui.components.AppButton
import com.moky.timebattle.ui.components.AppChip
import com.moky.timebattle.ui.components.TaskCard
import com.moky.timebattle.ui.components.icons.backIcon
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.StrokeLight
import com.moky.timebattle.ui.theme.TimebattleTheme
import com.moky.timebattle.util.VibrationHelper

private val filters = listOf("全部", "系统", "玩家", "限时")

@Composable
fun TasksScreen(
    viewModel: AppViewModel,
    onShowMessage: (String) -> Unit,
    onBack: () -> Unit,
    onPublishTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val state by viewModel.state.collectAsState()
    TasksContent(
        tasks = state.tasks,
        onBack = onBack,
        onPublishTask = onPublishTask,
        onTaskAction = { taskId ->
            val task = state.tasks.find { it.id == taskId }
            when (task?.status) {
                TaskStatus.AVAILABLE -> {
                    viewModel.claimTask(taskId)
                    VibrationHelper.vibrateSuccess(context)
                    onShowMessage("已接取任务：${task.title}")
                }
                TaskStatus.IN_PROGRESS -> {
                    viewModel.completeTask(taskId)
                    VibrationHelper.vibrateSuccess(context)
                    onShowMessage("任务完成，奖励已到账")
                }
                else -> {}
            }
        },
        modifier = modifier
    )
}

@Composable
private fun TasksContent(
    tasks: List<TaskItem>,
    onBack: () -> Unit,
    onPublishTask: () -> Unit,
    onTaskAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("全部") }

    val filteredTasks = when (selectedFilter) {
        "系统" -> tasks.filter { it.type == TaskType.SYSTEM }
        "玩家" -> tasks.filter { it.type == TaskType.USER }
        "限时" -> tasks.filter { it.type == TaskType.LIMITED }
        else -> tasks
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssBlack)
            .padding(horizontal = 20.dp)
    ) {
        // Top bar
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
                text = "任务中心",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
            )
            AppButton(
                text = "发布",
                onClick = onPublishTask,
                variant = com.moky.timebattle.ui.components.ButtonVariant.Outline
            )
        }

        // Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filters.forEach { filter ->
                AppChip(
                    text = filter,
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter }
                )
            }
        }

        // Task list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "该分类下暂无任务",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MutedWhite)
                    )
                }
            } else {
                filteredTasks.forEach { task ->
                    TaskCard(
                        title = task.title,
                        description = task.description,
                        reward = task.rewardSeconds.formatAsReward(),
                        isSystem = task.type == TaskType.SYSTEM,
                        actionText = when (task.status) {
                            TaskStatus.AVAILABLE -> task.actionText
                            TaskStatus.IN_PROGRESS -> "完成"
                            TaskStatus.CLAIMED -> "已领取"
                            TaskStatus.COMPLETED -> "已完成"
                        },
                        onClick = { onTaskAction(task.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun Long.formatAsReward(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    return String.format("+%dh %02dm", hours, minutes)
}

@Preview(showBackground = true)
@Composable
private fun TasksContentPreview() {
    TimebattleTheme {
        TasksContent(
            tasks = listOf(
                TaskItem(title = "每日签到", description = "连续签到递增奖励", rewardSeconds = 30 * 60, type = TaskType.SYSTEM),
                TaskItem(title = "翻译一篇短文", description = "@夜行者", rewardSeconds = 5 * 3600 + 30 * 60, type = TaskType.USER)
            ),
            onBack = {},
            onPublishTask = {},
            onTaskAction = {}
        )
    }
}
