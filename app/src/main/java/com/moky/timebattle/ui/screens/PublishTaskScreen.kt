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
import com.moky.timebattle.ui.components.AppButton
import com.moky.timebattle.ui.components.AppInput
import com.moky.timebattle.ui.components.ButtonVariant
import com.moky.timebattle.ui.components.icons.backIcon
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.StrokeLight
import com.moky.timebattle.ui.theme.TimebattleTheme
import com.moky.timebattle.util.VibrationHelper

@Composable
fun PublishTaskScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rewardHours by remember { mutableStateOf("1") }

    PublishTaskContent(
        title = title,
        onTitleChange = { title = it },
        description = description,
        onDescriptionChange = { description = it },
        rewardHours = rewardHours,
        onRewardHoursChange = { rewardHours = it.filter { c -> c.isDigit() } },
        onBack = onBack,
        onPublish = {
            val hours = rewardHours.toIntOrNull() ?: 1
            if (title.isBlank()) {
                VibrationHelper.vibrateWarning(context)
                onShowMessage("请输入任务标题")
                return@PublishTaskContent
            }
            viewModel.publishTask(
                title = title,
                description = description.ifBlank { "玩家悬赏任务" },
                rewardSeconds = hours * 3600L
            )
            VibrationHelper.vibrateSuccess(context)
            onShowMessage("任务发布成功")
            onBack()
        },
        modifier = modifier
    )
}

@Composable
private fun PublishTaskContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    rewardHours: String,
    onRewardHoursChange: (String) -> Unit,
    onBack: () -> Unit,
    onPublish: () -> Unit,
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
                text = "发布悬赏",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "任务标题",
            style = MaterialTheme.typography.bodyMedium.copy(color = DimWhite),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        AppInput(
            value = title,
            onValueChange = onTitleChange,
            placeholder = "例如：翻译一篇短文"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "任务描述",
            style = MaterialTheme.typography.bodyMedium.copy(color = DimWhite),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        AppInput(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = "描述任务内容、要求..."
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "悬赏时间（小时）",
            style = MaterialTheme.typography.bodyMedium.copy(color = DimWhite),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        AppInput(
            value = rewardHours,
            onValueChange = onRewardHoursChange,
            placeholder = "1"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "提示：发布悬赏会立即扣除对应的生命时间",
            style = MaterialTheme.typography.bodySmall.copy(color = MutedWhite)
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppButton(
            text = "确认发布",
            onClick = onPublish,
            modifier = Modifier.fillMaxWidth(),
            variant = ButtonVariant.Primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PublishTaskContentPreview() {
    TimebattleTheme {
        PublishTaskContent(
            title = "",
            onTitleChange = {},
            description = "",
            onDescriptionChange = {},
            rewardHours = "1",
            onRewardHoursChange = {},
            onBack = {},
            onPublish = {}
        )
    }
}
