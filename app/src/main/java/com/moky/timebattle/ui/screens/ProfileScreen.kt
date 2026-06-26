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
import com.moky.timebattle.data.model.AppState
import com.moky.timebattle.data.model.User
import com.moky.timebattle.data.model.formatCompact
import com.moky.timebattle.ui.components.icons.chartIcon
import com.moky.timebattle.ui.components.icons.userIcon
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.CarbonGrey
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.StrokeLight
import com.moky.timebattle.ui.theme.StrokeRed
import com.moky.timebattle.ui.theme.TimebattleTheme

@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    ProfileContent(
        state = state,
        onNavigateToLeaderboard = onNavigateToLeaderboard,
        onNavigateToSettings = onNavigateToSettings,
        modifier = modifier
    )
}

@Composable
private fun ProfileContent(
    state: AppState,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user = state.user

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssBlack)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "我的",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
            modifier = Modifier.padding(top = 14.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Avatar + name
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .border(1.dp, StrokeRed, RoundedCornerShape(32.dp))
                    .background(CarbonGrey, RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = userIcon(),
                    contentDescription = "Avatar",
                    tint = LifeRed,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column {
                Text(
                    text = user.nickname,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "ID: ${user.id.take(8)}",
                    style = MaterialTheme.typography.bodySmall.copy(color = DimWhite)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = "剩余时间",
                value = user.remainingSeconds.formatCompact(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "信誉点",
                value = state.reputationPoints.toString(),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = "完成任务",
                value = state.totalTaskCount.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "交易次数",
                value = state.totalTradeCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu
        MenuItem(
            icon = { Icon(chartIcon(), null, tint = LifeRed, modifier = Modifier.size(18.dp)) },
            label = "排行榜",
            onClick = onNavigateToLeaderboard
        )
        MenuItem(
            icon = { Text("⚙", style = MaterialTheme.typography.bodyLarge.copy(color = LifeRed)) },
            label = "设置",
            onClick = onNavigateToSettings
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(CarbonGrey, RoundedCornerShape(12.dp))
            .border(1.dp, StrokeLight, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = DimWhite, fontSize = 10.sp)
        )
    }
}

@Composable
private fun MenuItem(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
            icon()
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = MutedWhite)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileContentPreview() {
    TimebattleTheme {
        ProfileContent(
            state = AppState(user = User(nickname = "管理局成员", isLoggedIn = true)),
            onNavigateToLeaderboard = {},
            onNavigateToSettings = {}
        )
    }
}
