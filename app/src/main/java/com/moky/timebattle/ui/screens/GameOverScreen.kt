package com.moky.timebattle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moky.timebattle.data.AppViewModel
import com.moky.timebattle.data.model.AppState
import com.moky.timebattle.data.model.formatCompact
import com.moky.timebattle.ui.components.AppButton
import com.moky.timebattle.ui.components.ButtonVariant
import com.moky.timebattle.ui.components.icons.logoIcon
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.TimebattleTheme

@Composable
fun GameOverScreen(
    viewModel: AppViewModel,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    GameOverContent(
        state = state,
        onRestart = {
            viewModel.restart()
            onRestart()
        },
        modifier = modifier
    )
}

@Composable
private fun GameOverContent(
    state: AppState,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssBlack)
            .padding(horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = logoIcon(size = 64f),
            contentDescription = "Game Over",
            tint = LifeRed,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "时间归零",
            style = MaterialTheme.typography.displayMedium.copy(
                color = LifeRed,
                fontSize = 32.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "你的时间已经耗尽\n管理局已将你标记为「离线」",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = DimWhite,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        )
        Spacer(modifier = Modifier.height(30.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            StatRow(label = "存活", value = state.user.createdAt.formatSurvivalTime())
            StatRow(label = "任务", value = state.totalTaskCount.toString())
            StatRow(label = "交易", value = state.totalTradeCount.toString())
            StatRow(label = "累计", value = state.totalEarnedSeconds.formatCompact())
        }
        Spacer(modifier = Modifier.height(34.dp))
        AppButton(
            text = "重新开始",
            onClick = onRestart,
            variant = ButtonVariant.Outline
        )
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = DimWhite)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MutedWhite,
                fontSize = 12.sp
            )
        )
    }
}

private fun Long.formatSurvivalTime(): String {
    val diff = System.currentTimeMillis() - this
    val seconds = diff / 1000
    return seconds.formatCompact()
}

@Preview(showBackground = true)
@Composable
private fun GameOverContentPreview() {
    TimebattleTheme {
        GameOverContent(
            state = AppState(),
            onRestart = {}
        )
    }
}
