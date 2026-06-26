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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moky.timebattle.ui.components.icons.backIcon
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.CarbonGrey
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.StrokeLight
import com.moky.timebattle.ui.theme.StrokeRed
import com.moky.timebattle.ui.theme.TimebattleTheme

@Composable
fun SettingsScreen(
    vibrationEnabled: Boolean,
    onVibrationToggle: (Boolean) -> Unit,
    onClearCache: () -> Unit,
    onLogout: () -> Unit,
    onRestart: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsContent(
        vibrationEnabled = vibrationEnabled,
        onVibrationToggle = onVibrationToggle,
        onClearCache = onClearCache,
        onLogout = onLogout,
        onRestart = onRestart,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun SettingsContent(
    vibrationEnabled: Boolean,
    onVibrationToggle: (Boolean) -> Unit,
    onClearCache: () -> Unit,
    onLogout: () -> Unit,
    onRestart: () -> Unit,
    onBack: () -> Unit,
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
                text = "设置",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "通用",
            style = MaterialTheme.typography.labelMedium.copy(color = DimWhite),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ToggleItem(
            label = "震动反馈",
            checked = vibrationEnabled,
            onCheckedChange = onVibrationToggle
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "数据",
            style = MaterialTheme.typography.labelMedium.copy(color = DimWhite),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ActionMenuItem(
            label = "清除缓存",
            description = "清空通知与交易挂单记录",
            onClick = onClearCache
        )
        ActionMenuItem(
            label = "重新开始",
            description = "重置生命时间并清空任务进度",
            onClick = onRestart,
            tint = LifeRed
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "账号",
            style = MaterialTheme.typography.labelMedium.copy(color = DimWhite),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ActionMenuItem(
            label = "退出登录",
            description = "返回登录页，本地数据保留",
            onClick = onLogout,
            tint = LifeRed
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "时间管理局 v0.1.0 · Phase 01",
            style = MaterialTheme.typography.labelSmall.copy(color = DimWhite),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun ToggleItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CarbonGrey, RoundedCornerShape(10.dp))
            .border(1.dp, StrokeLight, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = MutedWhite)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = LifeRed,
                checkedTrackColor = LifeRed.copy(alpha = 0.5f),
                uncheckedThumbColor = MutedWhite,
                uncheckedTrackColor = StrokeLight
            )
        )
    }
}

@Composable
private fun ActionMenuItem(
    label: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = MutedWhite
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = tint)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.labelSmall.copy(color = DimWhite, fontSize = 9.sp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    TimebattleTheme {
        SettingsContent(
            vibrationEnabled = true,
            onVibrationToggle = {},
            onClearCache = {},
            onLogout = {},
            onRestart = {},
            onBack = {}
        )
    }
}
