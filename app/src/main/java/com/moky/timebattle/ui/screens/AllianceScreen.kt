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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moky.timebattle.ui.components.icons.backIcon
import com.moky.timebattle.ui.components.icons.groupIcon
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.StrokeLight
import com.moky.timebattle.ui.theme.TimebattleTheme

@Composable
fun AllianceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AllianceContent(
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun AllianceContent(
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
                text = "联盟",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
            )
        }

        Spacer(modifier = Modifier.height(80.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(1.dp, LifeRed.copy(alpha = 0.4f), RoundedCornerShape(40.dp))
                    .background(LifeRed.copy(alpha = 0.06f), RoundedCornerShape(40.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = groupIcon(),
                    contentDescription = "Alliance",
                    tint = LifeRed,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "联盟功能即将开放",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "加入或创建时间联盟，与伙伴共享资源、共渡难关",
                style = MaterialTheme.typography.bodyMedium.copy(color = DimWhite),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Coming in Phase 02",
                style = MaterialTheme.typography.labelSmall.copy(color = MutedWhite, fontSize = 10.sp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AllianceContentPreview() {
    TimebattleTheme {
        AllianceContent(onBack = {})
    }
}
