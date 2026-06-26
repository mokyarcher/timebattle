package com.moky.timebattle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moky.timebattle.data.model.LeaderboardEntry
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.CarbonGrey
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.StrokeLight
import com.moky.timebattle.ui.theme.TimebattleTheme
import com.moky.timebattle.ui.theme.WarmWhite

@Composable
fun LeaderboardScreen(
    entries: List<LeaderboardEntry>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssBlack)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "排行榜",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
            modifier = Modifier.padding(top = 14.dp)
        )
        Text(
            text = "最富有时长的管理局成员",
            style = MaterialTheme.typography.bodySmall.copy(color = MutedWhite),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无数据",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DimWhite)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                entries.forEach { entry ->
                    LeaderboardRow(entry = entry)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    val bg = if (entry.isCurrentUser) CarbonGrey else AbyssBlack
    val borderColor = if (entry.isCurrentUser) StrokeLight else StrokeLight.copy(alpha = 0.5f)
    val rankColor = when (entry.rank) {
        1 -> LifeRed
        2 -> MutedWhite
        3 -> DimWhite
        else -> DimWhite
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.rank.toString(),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = rankColor,
                    fontSize = 14.sp
                )
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.nickname,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (entry.isCurrentUser) WarmWhite else MutedWhite,
                    fontWeight = if (entry.isCurrentUser) FontWeight.Medium else FontWeight.Normal
                )
            )
        }
        Text(
            text = formatDuration(entry.value),
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp)
        )
    }
}

private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return String.format("%dh %02dm", hours, minutes)
}

@Preview(showBackground = true)
@Composable
private fun LeaderboardScreenPreview() {
    TimebattleTheme {
        LeaderboardScreen(
            entries = listOf(
                LeaderboardEntry(rank = 1, nickname = "星河", value = 120 * 3600L),
                LeaderboardEntry(rank = 2, nickname = "夜行者", value = 96 * 3600L),
                LeaderboardEntry(rank = 3, nickname = "你", value = 72 * 3600L, isCurrentUser = true),
                LeaderboardEntry(rank = 4, nickname = "北风", value = 48 * 3600L)
            )
        )
    }
}
