package com.moky.timebattle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moky.timebattle.ui.components.AppButton
import com.moky.timebattle.ui.components.ButtonVariant
import com.moky.timebattle.ui.components.icons.logoIcon
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.TimebattleTheme
import com.moky.timebattle.ui.theme.WarmWhite

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
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
            imageVector = logoIcon(size = 96f),
            contentDescription = "Logo",
            tint = LifeRed,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "时间管理局",
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 28.sp,
                letterSpacing = 3.sp
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "time bureau",
            style = MaterialTheme.typography.labelSmall.copy(
                color = DimWhite,
                letterSpacing = 4.sp,
                fontSize = 9.sp
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(44.dp))
        AppButton(
            text = "授权登录",
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            variant = ButtonVariant.Outline
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "没有账户？注册",
            style = MaterialTheme.typography.bodySmall.copy(
                color = DimWhite,
                fontSize = 11.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    TimebattleTheme {
        LoginScreen(onLoginClick = {})
    }
}
